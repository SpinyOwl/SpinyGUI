# Input Button Support Plan

## Goal
Add first-class support for `<input type="button">` as an empty, value-labelled input control: parse and serialize it through the existing `InputElement`, lay it out from its `value` text, render the visible label without text-edit affordances, activate it from mouse and keyboard input, and prove the difference from content-bearing `<button>` in the complex demo.

## Non-Goals
- Full form submission, reset, validation, submitter serialization, or form ownership.
- Support for every non-text input type in the same slice.
- Child content for `<input type="button">`; input remains an empty element and its label comes from the `value` attribute.
- Browser-complete disabled, autofocus, tab-order, ARIA, or user-agent stylesheet behavior.
- Replacing the existing `<button>` implementation.

## Context
- `InputElement` already represents all `<input>` nodes and defaults missing `type` to `text`.
- `InputElement.textInput()` currently gates single-line editing, caret placement, viewport scrolling, and NanoVG text-input rendering.
- `DefaultNodeParser` already parses `<input type="button" value="...">` as `InputElement` and serializes `type` and `value`.
- `ButtonElement` already has content-based activation semantics and emits `ActionEvent`; `<input type="button">` should reuse the activation contract, not the content model.
- `BlockLayout` currently gives default sizing only to `InputElement` when `textInput()` is true, so `type="button"` falls through generic empty-element layout.
- `NvgInputRenderer` currently returns early for non-text inputs, so `type="button"` has no value-label renderer.
- `SystemMouseClickEventListener` only emits `ActionEvent` for `ButtonElement`; `SystemKeyEventListener` checks `InputElement` before `ButtonElement`, so focused `input[type=button]` receives no activation behavior.
- Prior SpinyGUI control work requires demo-path verification in addition to unit tests.

## Assumptions and Open Questions
- Assumption: `<input type="button">` activation should emit the existing `ActionEvent`, matching `<button type="button">`.
- Assumption: keyboard activation should use the same keys as `<button>`: `Enter`, `Numpad Enter`, and `Space`.
- Assumption: `value` is the rendered label; an absent value renders as an empty label but still produces a usable control box.
- Assumption: `input[type=submit]` and `input[type=reset]` remain deferred unless the implementation can support them by reusing the same predicate without extra form semantics.
- Question: Should `InputElement` expose a narrow `buttonInput()` predicate only for `type=button`, or a broader `activatableInput()` predicate for `button`, `submit`, and `reset` with form behavior still deferred?

## Step-by-Step Plan

### Step 1: Add Input-Type Predicates and Contract Tests
**Purpose:** Make `type="button"` explicit in the model without introducing a separate node class.

**Changes:**
- [x] Add a predicate on `InputElement` for button input behavior, preferably `buttonInput()` or `activatableInput()` depending on the type-scope decision.
- [x] Keep `textInput()` unchanged so only `type=text` receives editing, caret, selection, and text-scroll behavior.
- [x] Add `NodeBuilder` and `InputElement` tests proving `input(TYPE_BUTTON, name, value)` initializes `type=button` and preserves `value`.
- [x] Add parser tests proving `<input type="button" value="Save">` remains an `InputElement`, remains childless, and round-trips `type` and `value`.

**Acceptance Checks:**
- [x] `input[type=button]` does not satisfy `textInput()`.
- [x] `input[type=button]` satisfies the new button/activatable predicate.
- [x] Parser tests distinguish `<input type="button" value="Save">` from `<button>Save</button>`.

**Implementation Notes:**
- Chose the narrow `InputElement.buttonInput()` predicate for Step 1 because the requested feature is specifically `<input type="button">`; `submit` and `reset` inputs remain deferred until form semantics are designed.

**Dependencies:** None.

**Risks:** Do not create `InputButtonElement`; this repo already chose one `InputElement` model with type-specific composed behavior.

### Step 2: Add Button-Input Layout Defaults
**Purpose:** Give `input[type=button]` sensible geometry when no explicit CSS width or height is supplied.

**Changes:**
- [x] Extend `BlockLayout` so button inputs use value-text measurement plus padding and border for auto width.
- [x] Compute auto height from font line height plus padding and border, matching the control sizing approach used by text inputs and buttons.
- [x] Respect explicit `width`, `height`, `min-*`, and `max-*` constraints.
- [x] Add layout tests for value-based auto size, empty-value fallback size, and explicit styled size.
- [x] Add a regression check that text-input sizing still uses the existing text-input path.

**Acceptance Checks:**
- [x] A value-labelled input button has non-zero border-box width and height without child nodes.
- [x] Explicit width and height override auto sizing.
- [x] `input[type=text]` layout tests still pass unchanged.

**Implementation Notes:**
- Button input auto width uses the `value` text when present and a 64px content-width fallback for an empty value so the control still has usable geometry.

**Dependencies:** Step 1.

**Risks:** Measuring by `value` may duplicate button text measurement logic. Prefer a small shared helper only if it removes concrete duplication without obscuring the different content sources.

### Step 3: Render the Value Label Without Editing Chrome
**Purpose:** Display the `value` text for button inputs while avoiding caret, selection, and text-scroll affordances.

**Changes:**
- [x] Update NanoVG input rendering to handle button inputs separately from text inputs.
- [x] Render the value label centered vertically, clipped to the content box, and styled with the resolved text color/font.
- [x] Do not render caret or selection for button inputs, even when focused.
- [x] Add backend renderer tests or sink-level tests proving text-input rendering still gates caret/selection and button-input rendering draws only the label.

**Acceptance Checks:**
- [x] `input[type=button]` renders its `value` label.
- [x] Focused `input[type=button]` does not render a caret.
- [x] Existing `NvgInputRendererTest` text-input expectations still pass.

**Implementation Notes:**
- `NvgInputRenderer` now accepts text and button inputs, draws button input values through the existing clipped text sink, and skips selection/caret/text-scroll affordances for button inputs.

**Dependencies:** Steps 1 and 2.

**Risks:** The existing renderer returns early when `textMeasurer` is missing. Keep that behavior consistent unless tests show a practical demo failure.

### Step 4: Wire Mouse and Keyboard Activation
**Purpose:** Make button inputs interactive through the same public event contract as `<button>`.

**Changes:**
- [x] Add backend-agnostic behavior for input-button activation, either by generalizing `ButtonBehavior` to `Element` plus predicates or by adding a narrow `InputButtonBehavior`.
- [x] Update `SystemMouseClickEventListener` so releasing a focused button input inside itself emits `ActionEvent` along with the existing click/release events.
- [x] Update `SystemKeyEventListener` so focused button inputs activate on `Enter`, `Numpad Enter`, and `Space`.
- [x] Ensure char input, text-edit keys, caret placement, and selection logic do not affect button inputs.
- [x] Add listener tests for mouse activation, keyboard activation, release clearing `pressed`, and non-editability.

**Acceptance Checks:**
- [x] Mouse press/release on `input[type=button]` emits `ActionEvent`.
- [x] Keyboard activation on focused `input[type=button]` emits `ActionEvent` and preserves `value`.
- [x] Printable char events and text-edit keys do not mutate `input[type=button]`.
- [x] Existing `<button>`, text input, and textarea listener tests still pass.

**Implementation Notes:**
- `ButtonBehavior` now handles both `ButtonElement` and `InputElement.buttonInput()`, while `SystemKeyEventListener` checks button inputs before text-edit behavior.
- `SystemMouseClickEventListener` emits `ActionEvent` for button inputs through the same focused-release path used by `<button>`.

**Dependencies:** Step 1.

**Risks:** `SystemKeyEventListener` currently branches on `InputElement` before `ButtonElement`; button-input activation must be checked before text-input editing or inside the input branch.

### Step 5: Update Demo Styling and Feedback
**Purpose:** Provide a real manual verification path for the input-button path and its distinction from `<button>`.

**Changes:**
- [x] Extend `button-demo.xml`/`button-demo.css` or add a focused input-button demo section showing `<button>Save</button>` beside `<input type="button" value="Save">`.
- [x] Add `ActionEvent` feedback for the input button in `ButtonExample`.
- [x] Add focused/active CSS for `input[type=button]` if selector support permits; otherwise use an id/class selector and document the limitation.
- [x] Verify the demo keyboard layout still maps `Enter`, `Numpad Enter`, and `Space`.

**Acceptance Checks:**
- [x] `:spinygui.demo.complex:classes` succeeds.
- [ ] Manual demo check confirms click activation updates visible feedback for `input[type=button]`.
- [ ] Manual demo check confirms keyboard activation updates visible feedback for `input[type=button]`.
- [x] Manual demo check confirms `<button>` can render nested child content while `<input type="button">` uses only `value`.

**Implementation Notes:**
- `ButtonExample` now attaches activation feedback to the existing `input-button` element using the same `ActionEvent` listener path as native buttons.
- `button-demo.css` uses the supported simple `input:focus` and `input:active` selectors for input-button visual feedback.
- Verified `Demo.defaultKeyboardLayout()` maps `Space`, `Enter`, and `Numpad Enter`; `.\gradlew.bat :spinygui.demo.complex:classes` succeeds.
- Manual validation can be launched with `.\gradlew.bat :spinygui.demo.complex:runButtonExample`.
- Screenshot evidence shows visible `Activated Input button 15` feedback, nested `<button>` content rendering (`Save` plus nested `span` text), and the input button rendering only its `value` label (`Save`). The screenshot does not identify whether activation came from click or keyboard.

**Dependencies:** Steps 3 and 4.

**Risks:** Static rendering is not enough. The demo must prove the real event path via visible activation feedback.

### Step 6: Document Scope and Update Feature Plans
**Purpose:** Keep the supported contract clear and avoid accidental claims of full form-control parity.

**Changes:**
- [x] Update this plan's checkboxes and implementation notes as evidence accumulates.
- [x] Update `button-element-support.md` if needed so its `<input type="button">` comparison points to the implemented input-button contract.
- [x] Document supported input-button behavior: value label, activation event, activation keys, and no child content.
- [x] Document deferred form semantics for `submit` and `reset` inputs.

**Acceptance Checks:**
- [x] Documentation distinguishes `ButtonElement` content semantics from `InputElement type=button` value semantics.
- [x] Documentation names unsupported form behavior explicitly.
- [ ] Final verification commands and manual demo evidence are recorded.

**Implementation Notes:**
- Added the supported contract section below and cross-referenced it from `button-element-support.md`.
- Final validation passed: `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`.
- Partial manual demo evidence is recorded from screenshot verification; click-specific and keyboard-specific activation evidence are still pending.

**Dependencies:** Steps 1-5.

**Risks:** Avoid updating completed `<button>` checkboxes unless the new input-button work actually verifies those items.

## Supported Contract
- `<input type="button">` remains an `InputElement` and does not create a `ButtonElement`.
- The rendered label is the `value` attribute. Child nodes are not supported because `InputElement` is an empty element.
- The control uses input rendering for the element box, but button-input rendering draws only the value label and does not draw text-input caret, selection, or text-scroll affordances.
- Mouse press/release activation and focused keyboard activation emit the existing `ActionEvent`.
- Supported activation keys are `Enter`, `Numpad Enter`, and `Space`.
- `<button>` remains the content-bearing control. Its label can come from text and nested child elements, while `<input type="button">` is value-labelled only.
- `input[type=submit]` and `input[type=reset]` form semantics remain deferred. There is no native form submission, reset, submitter serialization, validation, form owner behavior, or name/value submit payload support in this feature.

## Verification Strategy
- Model/parser checks: `.\gradlew.bat :spinygui.core:test --tests *NodeBuilderTest --tests *DefaultNodeParserTest`
- Layout checks: `.\gradlew.bat :spinygui.core:test --tests *BlockLayoutTest`
- Event checks: `.\gradlew.bat :spinygui.core:test --tests *SystemMouseClickEventListenerTest --tests *SystemKeyEventListenerTest --tests *SystemCharEventListenerTest`
- Backend checks: `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests *NvgInputRendererTest`
- Demo compile check: `.\gradlew.bat :spinygui.demo.complex:classes`
- Manual demo launch: `.\gradlew.bat :spinygui.demo.complex:runButtonExample`
- Final validation: `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`
- Manual demo validation: click activation, keyboard activation, focused/pressed visuals, value-label rendering, no caret, and visual comparison with nested `<button>` content.

## Review Boundaries
- Commit 1: input-button model predicates, parser/builder tests, and contract documentation.
- Commit 2: layout and rendering support with focused tests.
- Commit 3: activation behavior and listener tests.
- Commit 4: demo update, manual verification notes, and final documentation.

## Deferred Work
- `input[type=submit]` and `input[type=reset]` form semantics.
- Disabled/autofocus/tab-order behavior.
- Accessibility role/name support.
- Global user-agent default styles for form controls.
- Other input types such as checkbox, radio, color, range, file, number, and password.
