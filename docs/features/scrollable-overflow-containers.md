# Scrollable Overflow Containers

## Goal
Support CSS `overflow`, `overflow-x`, and `overflow-y` for block and flex elements with `visible`, `hidden`, `auto`, and `scroll`, preserving constrained element boxes while allowing overflowing child content to affect scroll ranges, render clipping, scroll input, and hit-testing.

## Non-Goals
- Native scrollbar painting, scrollbar pseudo-element styling, scrollbar layout gutters, or draggable scrollbar thumbs.
- Full CSS overflow edge cases such as `clip`, `overlay`, scroll anchoring, scroll snapping, momentum scrolling, or `scroll-behavior`.
- Inline element overflow behavior beyond descendants inside block/flex scroll containers.
- Reworking existing block/flex layout algorithms except where needed to keep scroll/client metrics correct.

## Context
- `Element` already stores `scrollTop`, `scrollLeft`, `scrollWidth`, `scrollHeight`, `clientWidth`, and `clientHeight`.
- `LayoutServiceImpl.updateScrollAndClientSize` already computes scroll/client sizes and clamps offsets after layout, but it is not overflow-style aware.
- `Node.absolutePosition()` subtracts `offsetParent.scrollLeft()` and `scrollTop()`, so render positions already account for ancestor scroll offsets once scroll offsets are set.
- `SystemScrollEventListener` currently updates every element under the cursor that has larger scroll size than client size, without checking `overflow-x`/`overflow-y` or scroll chaining limits.
- `NvgRenderUtils.createScissor` currently clips by all offset parents' padding boxes. This is too broad for `overflow: visible` and not explicit enough for scroll-container clipping to content boxes.
- Hit-testing uses `NodeUtilities.getTargetElement*` and `RectangleIntersection`, but does not reject children outside an ancestor scroll container's viewport.

## Assumptions and Open Questions
- Assumption: `overflow` shorthand should accept one or two identifiers. One value applies to both axes; two values map to x then y.
- Assumption: `overflow: visible` is the default for both axes, matching CSS defaults and avoiding current unconditional clipping.
- Assumption: `hidden`, `auto`, and `scroll` create clipped scroll containers; `hidden` is programmatically scrollable but should not consume wheel input unless already scrolled by code.
- Assumption: scroll deltas should keep the existing `50` pixel multiplier unless a separate input-tuning task changes it.
- Assumption: clipping should use the content box, not the padding box, because the requested behavior explicitly says content box.
- Question: Should focused scroll containers receive wheel input even when the cursor is not over them? The plan treats hovered containers first and focused containers as fallback only when no hovered scroll container can consume the delta.

## Step-by-Step Plan

### Step 1: Add Overflow Style Contract
**Purpose:** Make overflow values parseable and available through `ResolvedStyle`.

**Changes:**
- [x] Add constants and predefined values to `Overflow`: `VISIBLE`, `HIDDEN`, `AUTO`, and `SCROLL`.
- [x] Add `overflow()`, `overflowX()`, and `overflowY()` accessors/mutators to `ResolvedStyle`.
- [x] Add a localized property provider, for example `OverflowPropertyProvider`, that registers `overflow-x` and `overflow-y` as non-inherited longhands with default `visible`.
- [x] Implement `overflow` shorthand expansion for one or two `TermIdent` values, writing longhands into `OVERFLOW_X` and `OVERFLOW_Y`.
- [x] Reject unsupported identifiers through the existing property validator path.

**Acceptance Checks:**
- [x] Add parser/style tests proving `overflow: hidden`, `overflow: auto scroll`, `overflow-x: auto`, and `overflow-y: scroll` resolve to the expected longhand values.
- [x] Add a negative parser/style test proving an unsupported value such as `overflow: overlay` is not applied.
- [x] Run `.\gradlew.bat :spinygui.core:test` and confirm the new and existing core tests pass.

**Dependencies:** None.

**Risks:** Shorthand parsing may need a custom updater instead of existing `Property.put`; keep it contained in the new provider and avoid changing generic property semantics.

### Step 2: Centralize Overflow Semantics
**Purpose:** Avoid duplicating fragile checks across layout, input, rendering, and hit-testing.

**Changes:**
- [x] Add a small utility in core, for example `OverflowUtils`, with methods for `clipsX`, `clipsY`, `clipsAny`, `acceptsWheelX`, `acceptsWheelY`, `maxScrollLeft`, `maxScrollTop`, and `clampScrollOffsets`.
- [x] Define `auto` and `scroll` as wheel-scrollable only when `scrollWidth > clientWidth` or `scrollHeight > clientHeight`.
- [x] Define `hidden` as clipped but not wheel-scrollable.
- [x] Define `visible` as neither clipped nor wheel-scrollable.
- [x] Replace ad hoc scroll clamping in `LayoutServiceImpl.updateScrollAndClientSize` with the utility while preserving current zero-reset behavior when content fits.

**Acceptance Checks:**
- [x] Add unit tests for max scroll and clamping: negative offsets clamp to `0`, oversized offsets clamp to `scrollSize - clientSize`, and fitting content resets offsets to `0`.
- [x] Add tests proving `visible` does not consume wheel scroll, `hidden` clips without wheel consumption, and `auto`/`scroll` consume only when the axis overflows.
- [x] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Step 1.

**Risks:** Existing demos may rely on current implicit scroll behavior even without CSS overflow. This is a deliberate behavior change; tests should make the new contract explicit.

### Step 3: Preserve Layout Boxes While Measuring Overflow
**Purpose:** Ensure fixed or constrained containers keep their own content box while child overflow contributes to scroll ranges.

**Changes:**
- [x] Audit `BlockLayout.childrenHeight` and `FlexLayout.layout` to verify explicit `height`, `max-height`, and flex-assigned heights preserve the container content size even when children exceed it.
- [x] Adjust scroll size calculation in `LayoutServiceImpl.updateScrollAndClientSize` so child margin boxes beyond the content box increase `scrollWidth`/`scrollHeight`, but parent `clientWidth`/`clientHeight` remain the laid-out content size.
- [x] Use layout children or actual child nodes consistently for scroll-size calculation; document and test the choice for positioned children. Initial recommendation: keep absolute-positioned descendants out of normal scroll-size expansion to match current `affectsScrollSize`.
- [x] Clamp offsets after every layout pass using the centralized utility.

**Acceptance Checks:**
- [x] Add a block layout test where a `height: 100px; overflow-y: auto` container has a 300px-tall child: container `clientHeight` remains `100`, `scrollHeight` becomes at least `300`, and `scrollTop` clamps to `scrollHeight - clientHeight`.
- [x] Add a flex layout test where a flex child overflows its allocated height: the flex item content box remains the Yoga-assigned size and scroll range reflects child overflow.
- [x] Add a test that `overflow: visible` preserves the same layout box and scroll metrics but is not wheel-scrollable.
- [x] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Steps 1 and 2.

**Risks:** Flex layout currently lays out children before Yoga applies final child sizes. If scroll metrics are stale for flex children, run scroll-size updates after final Yoga positions and sizes, not during child layout.

### Step 4: Implement Scroll Event Consumption and Chaining
**Purpose:** Update only the nearest eligible scroll container, and bubble/chains scroll to ancestors only when the current container cannot consume more in that direction.

**Changes:**
- [x] Refactor `SystemScrollEventListener` to build a target chain from hovered elements, deepest first.
- [x] Include `frame.getFocusedElement()` as a fallback chain only when no hovered candidate can consume the event.
- [x] For each axis, find the deepest candidate whose overflow mode allows wheel scrolling and whose offset can move in the requested direction.
- [x] Apply delta per axis and clamp through `OverflowUtils`; if one axis is consumed by a child, do not also scroll an ancestor on that axis.
- [x] Push `ScrollEvent` only for elements whose scroll offset actually changed.
- [x] Keep existing event offsets in the generated `ScrollEvent` so listeners can see the original wheel delta.

**Acceptance Checks:**
- [x] Add listener tests where an inner scroll container consumes vertical scroll and the outer ancestor remains unchanged.
- [x] Add a test where an inner container at max scroll passes further scroll to the outer container.
- [x] Add a test where `overflow-y: hidden` does not consume wheel input and an eligible ancestor can scroll.
- [x] Add a horizontal scroll test using `offsetX`.
- [x] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Steps 1 through 3.

**Risks:** Existing tests expect a scroll event for every target under the cursor. Update those tests to the new consumption model rather than preserving the old broadcast behavior.

### Step 5: Add Overflow-Aware Hit-Testing
**Purpose:** Prevent events from reaching content clipped outside a scroll container and make scrolled child positions hit-test correctly.

**Changes:**
- [x] Add a helper that checks whether a point is inside every clipping ancestor's content box before accepting an element as a target.
- [x] Update `NodeUtilities.getTargetElement`, `getTargetElementList`, and recursive helpers to apply the clipping-ancestor check before descending into children.
- [x] Preserve z-index ordering and `pointer-events` behavior.
- [x] Use `absolutePosition()` for child position checks so existing offset subtraction applies scroll offsets during hit-testing.

**Acceptance Checks:**
- [x] Add a hit-test test where a child visually outside an `overflow: hidden` container is not returned.
- [x] Add a hit-test test where a child scrolled into view by `scrollTop` is returned at its visible viewport position.
- [x] Add a nested scroll-container test where the inner clipped viewport limits its descendants independently from the outer container.
- [x] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Steps 1 through 3.

**Risks:** `visibleInParents` already contains incomplete clipping logic. Prefer replacing or bypassing it with a narrowly tested helper instead of extending the stale TODO path blindly.

### Step 6: Apply Scroll Container Rendering Clips
**Purpose:** Render scroll-container contents only inside their content box while allowing `overflow: visible` descendants to paint outside ancestors.

**Changes:**
- [ ] Split NanoVG clipping into an injectable or testable clip helper, for example `NvgClipStack`, so tests can assert scissor/intersect/reset calls without requiring a live OpenGL context.
- [ ] Change `NvgRenderUtils.createScissor` or its replacement so it intersects only ancestors whose overflow clips on at least one axis.
- [ ] Clip children of scroll containers to the element content box; do not clip the element's own background/border to its own content box.
- [ ] Preserve clipping from multiple nested scroll containers by intersecting from root to leaf.
- [ ] Ensure text and inline-fragment render paths use the same clipping helper as element rendering.

**Acceptance Checks:**
- [ ] Add backend tests with a fake clip sink proving `overflow: visible` ancestors do not emit clip calls.
- [ ] Add backend tests proving `overflow: hidden`, `auto`, and `scroll` ancestors clip to content-box coordinates.
- [ ] Add a nested clipping test proving root-to-leaf intersection order.
- [ ] Run `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`.

**Dependencies:** Steps 1, 2, and 5.

**Risks:** The current renderer clips every element by parent padding boxes, so changing this can reveal content that was previously hidden. This is required for correct `overflow: visible`; include visual/manual verification in Step 8.

### Step 7: Keep Rendered Child Offsets and Layout Tree Consistent
**Purpose:** Verify scroll offsets affect all render paths, including block children, flex children, text, and inline fragments.

**Changes:**
- [ ] Audit `Node.absolutePosition()`, `NvgElementRenderer.inlineFormattingOffset`, and `NvgTextRenderer.inlineFormattingOffset` for double-subtraction or missing subtraction of ancestor scroll offsets.
- [ ] Add tests around absolute position calculations for nested scroll parents, including `scrollTop` and `scrollLeft`.
- [ ] Adjust inline rendering offset if inline text inside a scrolled block does not move with the block content.
- [ ] Confirm positioned descendants follow the existing `offsetParent` model and document any limitation for absolute-positioned children inside scroll containers.

**Acceptance Checks:**
- [ ] Add a unit test where a child under a scrolled parent has `absolutePosition().y == unscrolledY - scrollTop`.
- [ ] Add a test covering nested scroll offsets accumulating from multiple ancestors.
- [ ] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Steps 3, 5, and 6.

**Risks:** Absolute-positioned descendants may use a different offset parent than their DOM parent. Do not silently change that model in this feature unless tests prove it is necessary for scroll correctness.

### Step 8: Add Demo Coverage and Documentation
**Purpose:** Make the feature easy to manually verify and keep support docs accurate.

**Changes:**
- [ ] Update `docs/features/css-properties-support.md` to mark `overflow`, `overflow-x`, and `overflow-y` as supported after tests pass.
- [ ] Add or update a demo HTML/CSS resource with nested vertical and horizontal scroll containers.
- [ ] Include one `overflow: visible` case in the demo to catch accidental clipping regressions.

**Acceptance Checks:**
- [ ] Run the relevant demo and manually verify: vertical scroll, horizontal scroll, nested scroll chaining, clipped content, and visible overflow.
- [ ] Run `.\gradlew.bat test` for full validation.
- [ ] Confirm no unrelated generated files or build artifacts are included in the change.

**Dependencies:** Steps 1 through 7.

**Risks:** Manual demo verification depends on the local backend/OpenGL environment. If unavailable, record that limitation and rely on unit/backend tests for CI coverage.

## Verification Strategy
- After style work: `.\gradlew.bat :spinygui.core:test --tests "*Style*"`
- After layout and hit-test work: `.\gradlew.bat :spinygui.core:test`
- After NanoVG clipping work: `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`
- Final validation: `.\gradlew.bat test`

## Review Boundaries
- Step 1 can stand alone as a style parsing/resolved-style change.
- Steps 2 and 3 should be reviewed together only if scroll-size clamping naturally moves into the same utility.
- Step 4 should be a separate event-behavior change because it alters scroll dispatch semantics.
- Step 5 should be a separate hit-testing change with focused tests.
- Steps 6 and 7 can be split if renderer clipping requires a larger helper extraction.
- Step 8 should be documentation/demo-only after behavior is tested.

## Deferred Work
- Scrollbar rendering and `::-webkit-scrollbar`/`::scrollbar` styling.
- Keyboard scrolling, page scrolling, and touch/drag scrolling.
- `scroll-behavior`, smooth scrolling, scroll snapping, and scroll anchoring.
- Scrollbar gutter effects on layout metrics.
- Support for `overflow: clip` and `overflow-clip-margin`.
