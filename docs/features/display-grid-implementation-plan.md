# Display Grid Implementation Plan

## Goal
Implement CSS Grid as a first-class SpinyGUI layout mode. `display: grid` should parse through the normal CSS property pipeline, resolve to typed style values, dispatch to a dedicated grid formatting context, and produce stable layout boxes, scroll metrics, hit-testing geometry, and render traversal data through the existing frame layout pipeline.

## Non-Goals
- `subgrid`, masonry layout, CSS Grid Level 2 features, or browser-specific extensions.
- CSS custom properties, `var()`, or general `calc()` support beyond any syntax already supported by the parser.
- Replacing the current block, flex, inline, overflow, rendering, or event pipelines.
- Introducing a browser engine or external layout library for grid.
- Implementing unrelated unsupported CSS properties such as `box-sizing`, transforms, floats, or text shaping changes.

## Current Architecture Context
- `Display` currently registers supported display values, and `DisplayPropertyProvider` validates `display` through `Display::contains`.
- `ResolvedStyle` is a typed facade over the resolved CSS property map. Grid properties that layout consumes should have typed accessors instead of remaining raw parser terms.
- CSS properties are registered by `PropertyProvider` implementations under `core.style.stylesheet.property`; the default property store discovers these providers.
- The CSS value visitor already produces `TermIdent`, `TermLength`, `TermFloat`, `TermInteger`, `TermFunction`, and nested `TermList` values, which can represent grid grammar constructs such as slash-separated placement, `repeat(...)`, `minmax(...)`, and comma-separated template areas.
- `LayoutServiceProvider` wires display values to `ElementLayout` implementations. `FlexLayout` is the closest precedent: it first uses block layout to establish the container box, then applies a dedicated layout algorithm to children.
- `LayoutServiceImpl` owns hidden-subtree cleanup, child layout dispatch, layout-tree population, scroll/client size updates, and scroll offset clamping. Grid must preserve those contracts.
- `docs/features/css-properties-support.md` records the currently supported Grid subset and its
  explicit deferrals.

## Current Implementation Status

The original phase checklist below predates the current implementation. The following boundaries
are now present in the code and covered by focused tests or the complex demo:

- Phase 1/2: typed grid track, placement, template-area, gap, and auto-flow values; shorthand
  expansion; resolved-style accessors; and defaults.
- Phase 3: `Display.GRID` dispatches to `GridLayout`, which reuses block container sizing and
  preserves hidden/absolute child eligibility rules.
- Phase 4/5: explicit and implicit tracks, fixed/percentage/`fr`/`minmax`/`fit-content` sizing,
  gaps, explicit placement, named areas, row/column auto-flow, and dense placement.
- Phase 6: item alignment/stretch, nested-grid reflow, and grid overflow metrics.
- Phase 7: parser/style tests, layout tests, and a real complex-demo grid example exist.

The remaining work is deliberately narrower than the original plan: container-level
`justify-content`/`align-content`, complete intrinsic-sizing/two-pass convergence, named-line and
`span <name>` semantics, broader invalid-grammar and control/text interaction coverage, renderer
and manual proof, and final support-matrix/plan checkbox reconciliation.

## Implementation Phases

### Phase 1: Style Contract and Parser Support
**Status:** Implemented subset; unsupported grammar rejection remains open.
**Purpose:** Make grid syntax parseable, validated, and resolved through the existing CSS property pipeline.

**Changes:**
- [x] Add `Display.GRID` and ensure `display: grid` resolves through `DisplayPropertyProvider`.
- [x] Add property constants for grid longhands and shorthands, including `grid`, `grid-area`, `grid-auto-columns`, `grid-auto-flow`, `grid-auto-rows`, `grid-column`, `grid-column-end`, `grid-column-gap`, `grid-column-start`, `grid-gap`, `grid-row`, `grid-row-end`, `grid-row-gap`, `grid-row-start`, `grid-template`, `grid-template-areas`, `grid-template-columns`, and `grid-template-rows`.
- [x] Add missing alignment constants needed by CSS Grid Level 1 behavior, including `justify-items`, `justify-self`, `place-content`, `place-items`, and `place-self`, if they are not already supported by the time this phase is implemented.
- [x] Add `GridPropertyProvider` for grid longhand validation and shorthand expansion.
- [x] Reuse existing `TermFunction` and `TermList` structures for functions and slash-separated values, adding parser visitor changes only if the current semantic conversion cannot preserve required grid syntax.
- [ ] Reject unsupported grid grammar explicitly rather than silently accepting declarations that layout cannot honor.

**Acceptance Checks:**
- [x] `display: grid` resolves to `Display.GRID`.
- [ ] Each grid longhand resolves to a typed value or a documented default.
- [ ] Invalid declarations such as malformed template areas, impossible spans, and unsupported function forms are not applied.
- [x] Parser/style tests cover real CSS strings, not only direct object construction.

**Dependencies:** None.

**Risks:** The parser grammar can recognize more CSS than the semantic model supports. Keep support bounded to typed, tested grid values.

### Phase 2: Grid Value Model and Resolved Style Accessors
**Status:** Implemented subset.
**Purpose:** Provide strongly typed grid values for layout without passing raw parser terms into `GridLayout`.

**Changes:**
- [ ] Add grid value types under `core.style.types.grid` for track lists, track sizes, line names, template areas, placement lines, spans, gaps, auto-flow direction, and dense packing.
- [ ] Support fixed pixel lengths, percentages, `auto`, `fr`, `minmax(...)`, `fit-content(...)`, and `repeat(...)` in the value model.
- [ ] Normalize shorthand declarations into longhand resolved values during property update.
- [ ] Add `ResolvedStyle` accessors/mutators for grid properties consumed by layout.
- [ ] Add defaults matching CSS Grid expectations: no explicit tracks, `grid-auto-flow: row`, `grid-auto-rows: auto`, `grid-auto-columns: auto`, and zero gaps.

**Acceptance Checks:**
- [ ] Equivalent longhand and shorthand CSS produces equivalent `ResolvedStyle` values.
- [ ] Template areas produce a rectangular named-area model.
- [ ] `repeat(...)`, `minmax(...)`, `fit-content(...)`, `fr`, fixed, percentage, and `auto` tracks round-trip into typed style values.
- [ ] Style defaults are available even when no grid declarations are provided beyond `display: grid`.

**Dependencies:** Phase 1.

**Risks:** Overly generic value objects can become a second CSS AST. Keep the model focused on values needed by layout.

### Phase 3: Grid Formatting Context and Layout Service Wiring
**Status:** Implemented.
**Purpose:** Add a dedicated layout algorithm and dispatch `Display.GRID` to it.

**Changes:**
- [ ] Add `GridLayout implements ElementLayout` under `core.layout.impl`.
- [ ] Wire `Display.GRID` to `GridLayout` in `LayoutServiceProvider`.
- [ ] Reuse `BlockLayout` to establish the grid container border, padding, margin, width, and height before grid-specific placement.
- [ ] Call the existing `LayoutService` for child internal layout so nested block, flex, inline, input, button, textarea, and nested grid content remains coherent.
- [ ] Preserve existing hidden and absolute-positioned child behavior: `display:none` children do not create grid items, and absolute children follow positioned-ancestor rules.

**Acceptance Checks:**
- [ ] A grid container with fixed explicit tracks lays out children at expected content-box coordinates.
- [ ] `display:none` children are absent from grid placement and `layoutChildNodes`.
- [ ] Absolute-positioned descendants do not consume normal grid cells.
- [ ] Grid containers participate in layout-tree population and render traversal through existing `LayoutServiceImpl` behavior.

**Dependencies:** Phase 2.

**Risks:** Grid item layout may require a second child layout pass after assigned item sizes are known. Keep the pass explicit and test nested content.

### Phase 4: Track Sizing Algorithm
**Status:** Implemented subset; intrinsic sizing/convergence remains open.
**Purpose:** Compute explicit and implicit grid tracks according to CSS Grid Level 1 sizing behavior.

**Changes:**
- [ ] Expand explicit row and column templates, including `repeat(...)` and named lines.
- [ ] Build implicit rows and columns from auto-placement and out-of-range explicit placement.
- [ ] Resolve fixed lengths, percentages, `auto`, `fr`, `minmax(...)`, and `fit-content(...)`.
- [ ] Subtract row and column gaps before distributing remaining space to flexible tracks.
- [ ] Use child intrinsic sizes from existing child layout where tracks or item sizes depend on content.
- [ ] Respect container padding, border, explicit width/height, min/max width/height, and the repository's current border-box sizing behavior.

**Acceptance Checks:**
- [ ] Fixed, percentage, `auto`, and `fr` tracks produce expected dimensions.
- [ ] `minmax(...)` clamps tracks to minimum and maximum bounds.
- [ ] `fit-content(...)` respects the provided limit.
- [ ] Implicit tracks use `grid-auto-rows` and `grid-auto-columns`.
- [ ] Overflowing grid content contributes to `scrollWidth` and `scrollHeight`.

**Dependencies:** Phase 3.

**Risks:** Intrinsic sizing is the highest-complexity part of full Grid. Add tests around each track sizing rule before broadening coverage.

### Phase 5: Item Placement Algorithm
**Status:** Implemented subset; named-line and edge-case compatibility remains open.
**Purpose:** Place grid items into explicit or implicit grid areas using CSS Grid line, span, area, and auto-placement rules.

**Changes:**
- [ ] Implement explicit placement from `grid-row-start`, `grid-row-end`, `grid-column-start`, and `grid-column-end`.
- [ ] Implement `grid-row`, `grid-column`, and `grid-area` shorthand placement.
- [ ] Implement numeric lines, named lines, named areas, and `span N` or `span <name>` forms.
- [ ] Implement `grid-template-areas`, including validation that areas are rectangular and row widths match.
- [ ] Implement auto-placement for `grid-auto-flow: row` and `grid-auto-flow: column`.
- [ ] Implement dense packing for `grid-auto-flow: row dense` and `grid-auto-flow: column dense`.

**Acceptance Checks:**
- [ ] Explicitly placed items occupy the requested cells.
- [ ] Named template areas place items across the expected row and column ranges.
- [ ] Auto-placement fills cells in CSS order.
- [ ] Dense packing backfills earlier holes while preserving explicit item constraints.
- [ ] Overlapping explicit placements are deterministic and covered by tests.

**Dependencies:** Phase 4.

**Risks:** Browser behavior around invalid and overlapping placement has many edge cases. Document any intentionally unsupported or simplified behavior in the tests and support matrix.

### Phase 6: Alignment, Stretch, Overflow, and Nested Layout Integration
**Status:** Implemented subset; container alignment and broader interaction proof remain open.
**Purpose:** Make grid layout behave correctly with existing style, overflow, render, and input systems.

**Changes:**
- [ ] Apply container alignment with `justify-content` and `align-content`.
- [ ] Apply item alignment with `justify-items`, `align-items`, `justify-self`, `align-self`, `place-items`, and `place-self`.
- [ ] Implement stretch behavior for auto-sized grid items.
- [ ] Re-run child layout after assigned grid item sizes change so nested content uses final dimensions.
- [ ] Verify scroll containers inside grid items and grid containers with overflow continue to use existing `OverflowUtils`, scroll metrics, clipping, and hit-testing behavior.
- [ ] Verify text fragments, input caret behavior, buttons, and textarea layout remain valid inside grid items.

**Acceptance Checks:**
- [ ] Grid tracks align correctly inside containers with extra space.
- [ ] Grid items align or stretch correctly inside assigned grid areas.
- [ ] Nested block, flex, inline-block, text input, button, and textarea content keeps expected dimensions and interaction geometry.
- [ ] Scrollable grid containers and scrollable descendants expose correct client and scroll metrics.

**Dependencies:** Phase 5.

**Risks:** Alignment properties are shared with flex concepts but not identical. Keep grid-specific behavior separate when semantics differ.

### Phase 7: Regression Tests, Demo Coverage, and Docs Update
**Status:** In progress.
**Purpose:** Lock behavior down and make support status visible.

**Changes:**
- [ ] Add `GridStyleManagerTest` for parser, property provider, shorthand, function, template area, default, and invalid-value coverage.
- [ ] Add `GridLayoutTest` for geometry, track sizing, placement, hidden children, absolute children, nested content, and scroll metrics.
- [ ] Add a small complex-demo example or screen section using real `display: grid`.
- [ ] Update `docs/features/css-properties-support.md` after tests pass, marking only actually implemented grid properties as supported.
- [ ] Document deferred Grid Level 2 or unsupported CSS edge cases in the support matrix or this plan.

**Acceptance Checks:**
- [ ] Focused grid style and layout tests pass.
- [ ] Existing block, flex, overflow, inline, and parser tests still pass.
- [ ] Demo classes compile and the grid example uses real CSS grid declarations.
- [ ] Documentation reflects implementation truth and does not overstate support.

**Dependencies:** Phases 1 through 6.

**Risks:** Updating the support matrix before implementation is complete can mislead downstream users. Treat docs status updates as final-phase work only.

## Test Plan
- Add parser/style tests for grid longhands, shorthands, functions, template areas, defaults, and invalid declarations.
- Add layout tests for fixed tracks, percentage tracks, `fr`, `auto`, implicit tracks, named lines, named areas, auto-placement, dense packing, gaps, hidden children, absolute children, nested layout, and scroll metrics.
- Run focused verification after the grid test classes exist:
  - `.\gradlew.bat :spinygui.core:test --tests *GridStyleManagerTest --tests *GridLayoutTest`
- Run regression verification for existing affected layout systems:
  - `.\gradlew.bat :spinygui.core:test --tests *BlockLayoutTest --tests *FlexLayoutTest --tests *OverflowLayoutTest`
- Run final module verification:
  - `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`

## Risks and Assumptions
- Assumption: The implementation target is CSS Grid Level 1 behavior for the grid properties already listed in `docs/features/css-properties-support.md`.
- Assumption: Grid will use SpinyGUI's existing style and layout architecture rather than an external layout engine.
- Assumption: Existing block, flex, inline, overflow, rendering, and hit-test behavior must remain compatible.
- Risk: Full Grid is subsystem-scale work. Implement it in phases and keep each phase independently reviewable.
- Risk: Track sizing and intrinsic size contribution are complex. Add narrow geometry tests before expanding behavior.
- Risk: Shorthand parsing can accidentally accept unsupported syntax. Validators should reject unsupported forms until layout behavior exists.
- Risk: Alignment properties overlap with flex names but not all semantics match. Avoid sharing implementation blindly.
