# Scrollbar Support

## Goal
Add visible scrollbars for existing scroll containers and allow authors to style scrollbar parts with `::-webkit-scrollbar` and `::-webkit-scrollbar-*` pseudo-elements. The final behavior should integrate with the current overflow, layout, event, hit-test, and NanoVG rendering pipeline without applying pseudo-element declarations to the real element style.

## Non-Goals
- Full browser parity for every WebKit scrollbar state, platform-native scrollbar theming, overlay scrollbars, momentum scrolling, or scroll snapping.
- CSS standard scrollbar properties such as `scrollbar-width`, `scrollbar-color`, and `scrollbar-gutter`.
- Generated content pseudo-elements such as `::before` and `::after`.
- Transform-aware scrollbar hit-testing or fixed-position viewport scrollbars.

## Context
- `docs/features/scrollable-overflow-containers.md` completed overflow parsing, scroll metrics, scroll input, clipping, hit-testing, and render clipping; it explicitly deferred scrollbar rendering and styling.
- `Element` already stores `scrollTop`, `scrollLeft`, `scrollWidth`, `scrollHeight`, `clientWidth`, and `clientHeight`; `clientWidth`/`clientHeight` comments already mention exclusion of rendered scrollbar gutters.
- `SelectorVisitor` currently recognizes `::scrollbar` through `ScrollbarSelector`, but not `::-webkit-scrollbar` or WebKit scrollbar part names.
- `StyleManagerImpl` resolves one `ResolvedStyle` per real element. There is no pseudo-element style store, so pseudo-element declarations must be kept separate before rendering uses them.
- `NvgRenderer` traverses the layout tree and delegates element background, border, input, textarea, text, and debug drawing to localized renderers. Scrollbar painting belongs in the backend renderer after element content/border state is known.
- `SystemScrollEventListener` already supports wheel scrolling and chaining, but there is no mouse interaction for dragging scrollbar thumbs or clicking tracks.

## Assumptions and Open Questions
- Assumption: scrollbars are non-overlay gutters. A rendered vertical scrollbar reduces `clientWidth`; a rendered horizontal scrollbar reduces `clientHeight`.
- Assumption: `overflow: scroll` always shows the relevant scrollbar; `overflow: auto` shows one only when that axis overflows after gutter resolution.
- Assumption: scrollbar pseudo-element styles use normal specificity and source order, but they resolve into a pseudo-style map keyed by part, not into `element.resolvedStyle()`.
- Assumption: support the WebKit part names `::-webkit-scrollbar`, `::-webkit-scrollbar-thumb`, `::-webkit-scrollbar-track`, `::-webkit-scrollbar-track-piece`, `::-webkit-scrollbar-button`, `::-webkit-scrollbar-corner`, and `::-webkit-scrollbar-resizer`.
- Assumption: first rendering support should make `scrollbar`, `track`, `thumb`, and `corner` visible. `button`, `track-piece`, and `resizer` can be parsed and stored, with rendering deferred unless a later step adds concrete geometry for them.
- Question: Should `::-webkit-scrollbar:horizontal` and `::-webkit-scrollbar-thumb:hover` be supported now? This plan defers pseudo-classes on scrollbar parts unless a user-facing requirement depends on them.

## Step-by-Step Plan

### Step 1: Add Scrollbar Pseudo-Element Model
**Purpose:** Represent supported scrollbar parts explicitly so parser, style resolution, renderer, and tests share one vocabulary.

**Changes:**
- [x] Add a core type such as `ScrollbarPart` with values for `SCROLLBAR`, `THUMB`, `TRACK`, `TRACK_PIECE`, `BUTTON`, `CORNER`, and `RESIZER`.
- [x] Replace or extend `ScrollbarSelector` so it stores a `ScrollbarPart`, exposes the canonical CSS spelling, and still implements `PseudoElementSelector`.
- [x] Update `SelectorVisitor.visitPseudo` to recognize `-webkit-scrollbar` and `-webkit-scrollbar-*` names, while preserving the existing `scrollbar` selector only if it is intentionally kept as a project alias.
- [x] Add parser tests for selectors including `.panel::-webkit-scrollbar`, `*::-webkit-scrollbar-thumb`, `div::-webkit-scrollbar-track`, and an unsupported pseudo-element name.

**Acceptance Checks:**
- [x] Parsed WebKit scrollbar selectors produce the expected `ScrollbarSelector` part.
- [x] Unsupported pseudo-elements remain unsupported and do not create broad always-matching selectors.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests "*Parser*"`.

**Dependencies:** None.

**Risks:** The CSS grammar may already tokenize leading-hyphen identifiers correctly, but this must be proven with parser tests before changing style resolution.

### Step 2: Resolve Pseudo-Element Styles Separately
**Purpose:** Prevent `::-webkit-scrollbar-*` declarations from mutating the element's real style while making the computed pseudo styles available to the renderer.

**Changes:**
- [x] Add pseudo-style storage to `Element`, for example `Map<ScrollbarPart, ResolvedStyle> scrollbarStyles`, with read-only accessors and a clear/reset path during recalculation.
- [x] Update `StyleSheet`/`Ruleset` search or `StyleManagerImpl` so rules whose last selector applies to a scrollbar pseudo-element are matched against the owning element but applied to the matching pseudo-style bucket.
- [x] Keep default property declarations available for pseudo styles so unspecified colors, dimensions, borders, and radii have stable fallback values.
- [x] Ensure inline `style` attributes continue to apply only to the real element, not to pseudo-elements.

**Acceptance Checks:**
- [x] Add style-manager tests proving `.panel::-webkit-scrollbar-thumb { background-color: red; }` sets the thumb pseudo-style and leaves `.panel` background unchanged.
- [x] Add specificity/source-order tests where element rules and pseudo-element rules do not overwrite each other.
- [x] Add a regression test that existing non-pseudo selector behavior is unchanged.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests "*StyleManager*"`.

**Dependencies:** Step 1.

**Risks:** Current selector specificity is computed against real elements only. If pseudo selectors are not separated before applying declarations, scrollbar styles will leak into normal layout and rendering.

### Step 3: Define Scrollbar Geometry and Gutter Semantics
**Purpose:** Make scrollbar presence and dimensions deterministic before rendering and hit-testing depend on them.

**Changes:**
- [x] Add a core geometry helper such as `ScrollbarGeometry`/`ScrollbarUtils` that computes vertical track, horizontal track, thumb, and corner rectangles from an element's border/content box, scroll metrics, overflow modes, and pseudo-style width/height.
- [x] Define default scrollbar thickness, minimum thumb size, and fallback colors in one place.
- [x] Update `LayoutServiceImpl.updateScrollAndClientSize` so rendered scrollbar gutters reduce `clientWidth` and `clientHeight`, including the case where adding one axis causes overflow on the other axis.
- [x] Clamp scroll offsets after gutter-adjusted client sizes are finalized.
- [x] Keep `scrollWidth` and `scrollHeight` content-driven; do not include the scrollbar gutter itself in scroll size.

**Acceptance Checks:**
- [x] Add core tests proving `overflow-y: scroll` reserves a vertical gutter even when content fits.
- [x] Add tests proving `overflow: auto` reserves a gutter only when content overflows, including the two-axis interaction case.
- [x] Add tests proving `::-webkit-scrollbar { width: 12px; height: 10px; }` changes gutter geometry.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests "*Overflow*" --tests "*Layout*"`.

**Dependencies:** Steps 1 and 2.

**Risks:** Gutter reservation can require a second pass because vertical and horizontal scrollbars affect each other's available client size. Keep the loop bounded and covered by tests.

### Step 4: Add Scrollbar Hit-Testing and Mouse Interaction
**Purpose:** Allow scrollbars to behave as controls, not only painted indicators.

**Changes:**
- [x] Add event-side hit helpers that identify whether a mouse point is over a scrollbar track, thumb, or corner for a specific element.
- [x] Add runtime state for active scrollbar dragging, including target element, axis, drag origin, starting scroll offset, and thumb travel range.
- [x] Wire mouse press, drag, and release handling so dragging a thumb updates `scrollTop`/`scrollLeft` through `OverflowUtils` and emits `ScrollEvent` when offsets change.
- [x] Add track click behavior that scrolls by one client page toward the click point.
- [x] Preserve existing wheel scrolling and clipped descendant hit-testing behavior.

**Acceptance Checks:**
- [x] Add event tests where dragging a vertical thumb changes `scrollTop` proportionally and clamps at both ends.
- [x] Add event tests where dragging a horizontal thumb changes `scrollLeft`.
- [x] Add track-click tests for page-up/page-down style movement.
- [x] Add a regression test proving clicks inside normal content still target content when not over a scrollbar gutter.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests "*System*Scroll*" --tests "*Mouse*"`.

**Dependencies:** Step 3.

**Risks:** Current GUI events are exact-class based and split across system listeners. Keep the drag state localized to the event/input layer and avoid renderer-side state mutation.

### Step 5: Render Styled Scrollbar Parts in NanoVG
**Purpose:** Paint scrollbars using pseudo-element styles after normal element content is rendered.

**Changes:**
- [x] Add `NvgScrollbarRenderer` with an injectable shape sink so tests can assert rectangles without a live OpenGL context.
- [x] Render vertical and horizontal tracks, thumbs, and the corner using `ScrollbarGeometry` and the corresponding pseudo-style buckets.
- [x] Apply supported visual properties from pseudo styles: `background-color`, `border-color`, `border-width`, `border-radius`, and opacity where already available.
- [x] Integrate `NvgScrollbarRenderer` into `NvgRenderer.renderElement` after children/content and before debug overlays.
- [x] Ensure scrollbar drawing is clipped to the element border box, not to the scroll container content clip.

**Acceptance Checks:**
- [x] Add backend tests proving no scrollbar is drawn for `overflow: visible` or `overflow: hidden`.
- [x] Add backend tests proving `overflow: scroll` draws a track and thumb even when content fits.
- [x] Add backend tests proving thumb size and position follow `scrollTop`/`scrollLeft`.
- [x] Add backend tests proving pseudo-style colors and border radius are passed to the drawing sink.
- [x] Run `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`.

**Dependencies:** Steps 2 and 3.

**Risks:** Existing element renderers create/reset scissors around each paint call. Scrollbar rendering should not inherit the content clipping scissor that hides overflowing children.

### Step 6: Add Demo Coverage
**Purpose:** Make visual and interaction behavior easy to inspect manually.

**Changes:**
- [x] Add or update an overflow demo resource with vertical, horizontal, nested, and two-axis scroll containers using `::-webkit-scrollbar` and at least `track`, `thumb`, and `corner` styles.
- [x] Include a case where `overflow: scroll` shows a disabled-looking scrollbar for fitting content.
- [x] Include a case where `overflow: auto` changes from no scrollbar to scrollbar when content exceeds the fixed box.

**Acceptance Checks:**
- [ ] Run the relevant demo and manually verify gutter reservation, thumb movement, dragging, track clicks, nested scroll containers, and styled track/thumb/corner painting.
- [ ] Confirm the demo still shows existing overflow clipping and scroll chaining behavior.
- [x] Record any manual verification limitation in this document if the local OpenGL/NanoVG environment cannot run the demo.

**Step 6 note:** `OverflowExample` now includes styled vertical, horizontal, nested, two-axis, forced-scroll, auto-fit, and auto-overflow cases. Automated validation used `.\gradlew.bat :spinygui.demo.complex:classes` to compile the demo and process resources. Interactive GLFW/NanoVG verification was not performed in this non-interactive run, so the manual verification checklist above remains open for local inspection.

**Dependencies:** Steps 3 through 5.

**Risks:** Manual verification depends on local rendering support. Automated geometry and renderer-sink tests should cover the behavior that CI can validate.

### Step 7: Document Supported Scrollbar Scope
**Purpose:** Keep feature documentation accurate and prevent the parsed-but-not-rendered parts from being mistaken for full browser parity.

**Changes:**
- [x] Update `docs/features/css-properties-support.md` or add a short scrollbar section documenting supported pseudo-elements and supported properties on scrollbar pseudo-styles.
- [x] Update `docs/features/scrollable-overflow-containers.md` deferred work to point to this scrollbar plan or remove the completed item after implementation.
- [x] Add notes for intentionally limited parts such as `button`, `track-piece`, and `resizer` if they are parsed but not painted.

**Acceptance Checks:**
- [x] Documentation names the supported pseudo-elements exactly as accepted by the parser.
- [x] Documentation distinguishes painted parts from parsed-only parts.
- [x] Run `.\gradlew.bat test` for final validation.

**Dependencies:** Steps 1 through 6.

**Risks:** Over-documenting browser compatibility would be misleading. State the implemented subset explicitly.

## Verification Strategy
- Parser/model slice: `.\gradlew.bat :spinygui.core:test --tests "*Parser*" --tests "*Selector*"`
- Style resolution slice: `.\gradlew.bat :spinygui.core:test --tests "*StyleManager*"`
- Layout/geometry slice: `.\gradlew.bat :spinygui.core:test --tests "*Overflow*" --tests "*Layout*"`
- Event interaction slice: `.\gradlew.bat :spinygui.core:test --tests "*System*Scroll*" --tests "*Mouse*"`
- Backend rendering slice: `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`
- Final validation: `.\gradlew.bat test`

## Review Boundaries
- Step 1 should be a small parser/model change.
- Step 2 should be reviewed separately because it changes cascade/style resolution behavior.
- Step 3 can stand alone as layout and geometry semantics.
- Step 4 should be separate from rendering because it changes input behavior.
- Step 5 should be backend-only apart from consuming core geometry/style contracts.
- Steps 6 and 7 can be grouped as demo/documentation once behavior is tested.

## Deferred Work
- `::-webkit-scrollbar-thumb:hover`, `:active`, `:horizontal`, `:vertical`, and other scrollbar pseudo-class/state selectors.
- Detailed rendering and interaction for scrollbar buttons, track-piece segments, and resizers.
- CSS standard `scrollbar-width`, `scrollbar-color`, and `scrollbar-gutter`.
- Overlay scrollbars and platform-native theme integration.
- Keyboard page scrolling, home/end scrolling, smooth scrolling, and touch/gesture scrolling.
