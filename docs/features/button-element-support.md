# Button Element Support Plan

## Goal
Add first-class support for the HTML-like `<button>` element: parse and build it as a dedicated button control, preserve child content semantics, give it predictable control layout and default styling, focus and activate it from mouse/keyboard input, and expose a visible demo that proves `<button>` differs from `<input type="button">`.

## Non-Goals
- Full browser form submission, validation, reset, or submitter serialization.
- Disabled form-control semantics beyond a scoped attribute guard if needed for activation.
- Complete accessibility roles, tab navigation, or ARIA behavior.
- Rich user-agent stylesheet parity for all browser button pseudo states.
- Implementing every `<input type="button">`, `<input type="submit">`, and `<input type="reset">` behavior in the same step.

## Context
- `NodeBuilder` already defines `NODE_BUTTON` and `button(...)` helpers, but they currently return a generic `Element`.
- `DefaultNodeParser` already parses unknown non-empty tags, including `<button>`, as generic `Element` with child nodes.
- `InputElement` is an `EmptyElement`; its `type="button"` label comes from the `value` attribute. `<button>` remains content-based and may contain text and nested inline elements. The implemented input-button contract is tracked in `input-button-support.md`.
- `BlockLayout` has explicit control sizing for `InputElement` and `TextareaElement`; a button should not reuse text-input editing behavior.
- `NvgRenderer` already renders generic element backgrounds, borders, and child text. A button may only need a specialized renderer if pressed/default-state visuals cannot be handled through style/state.
- Mouse and keyboard system listeners already manage focus, pressed state, and GUI events; activation should fit that pipeline instead of bypassing it.
- Prior text-control work showed that demo-path input behavior and visible rendering must be verified, not inferred from unit tests alone.

## Assumptions and Defaults
- Assumption: `<button>` without a `type` attribute defaults to `type="submit"` for HTML compatibility, but v1 should only expose activation events and not submit forms.
- Assumption: `type="button"`, `type="submit"`, and `type="reset"` should parse and serialize as attributes; unsupported values should be retained but treated as `submit` or inert only after an explicit decision in implementation.
- Assumption: the runtime model should be a dedicated `ButtonElement extends Element`, not another `InputElement` type, because button label/content semantics are child-node based.
- Assumption: activation means emitting existing mouse/key/click events consistently; adding a new high-level `ActionEvent` or `ButtonClickEvent` is optional only if current events cannot represent activation clearly.
- Assumption: v1 button layout can be block-level, consistent with current control layout, unless existing CSS/default style already makes buttons inline.
- Cross-feature scope: `<button>` and `<input type="button">` now share the `ActionEvent` activation contract, but they do not share content semantics. `<button>` is represented by `ButtonElement` and renders child content; `<input type="button">` is represented by `InputElement` and renders only its `value`.

## Step-by-Step Plan

### Step 1: Add Button Node Model and Builders
**Purpose:** Represent `<button>` as a distinct content-bearing control without mixing it into `InputElement`.

**Changes:**
- [x] Add `ButtonElement extends Element` in `spinygui.core.node`.
- [x] Add runtime helpers for `type`, defaulting policy, and activation eligibility without storing label text separately from child nodes.
- [x] Update `NodeBuilder.button(...)` overloads to return `ButtonElement`.
- [x] Add a `NodeBuilder.buttonType(...)` or reuse `type(...)` in tests and examples; avoid duplicating input-only constants if the existing `TYPE_BUTTON`, `TYPE_SUBMIT`, and `TYPE_RESET` constants are sufficient.
- [x] Ensure `NodeBuilder.addAttributes(...)` initializes `ButtonElement` state after attributes are added, matching `InputElement` behavior.

**Acceptance Checks:**
- [x] Unit tests prove `NodeBuilder.button(text("Save"))` returns `ButtonElement` with one text child.
- [x] Unit tests prove missing `type` resolves to the chosen default and explicit `type="button"` is preserved.
- [x] Unit tests prove changing attributes through builder helpers refreshes button runtime type.

**Dependencies:** None.

**Risks:** Do not make `ButtonElement` an `EmptyElement`; that would erase the key difference from `<input type="button">`.

### Step 2: Parse and Serialize `<button>`
**Purpose:** Convert button markup into the dedicated node while preserving nested content.

**Changes:**
- [x] Update `DefaultNodeParser` to instantiate `ButtonElement` for `button` tags.
- [x] Preserve child text and nested inline child elements during parsing.
- [x] Serialize `ButtonElement` as a non-empty `<button>...</button>` element with current attributes and children.
- [x] Add parser tests for plain text content, nested inline content, missing type, and explicit `type="button"`.
- [x] Add a regression test proving `<input type="button" value="Save">` still parses as `InputElement` and remains childless/value-based.

**Acceptance Checks:**
- [x] `<button>Save</button>` parses as `ButtonElement` with a text child, not generic `Element`.
- [x] `<button><span>Save</span></button>` preserves the nested child tree.
- [x] `toHtml(...)` round-trips button child content and attributes.
- [x] Existing input and textarea parser tests still pass.

**Dependencies:** Step 1.

**Risks:** Jsoup may normalize whitespace around button text; tests should match current parser behavior rather than claiming full browser whitespace parity.

### Step 3: Define Button Layout Defaults
**Purpose:** Give buttons usable geometry when authors do not specify width or height.

**Changes:**
- [x] Extend layout handling so `ButtonElement` auto width is based on child content plus padding and border, or document why current block-width behavior is intentionally retained for v1.
- [x] Compute auto height from child inline content or font line-height plus padding and border.
- [x] Ensure explicit CSS width, height, min/max width, and min/max height still override defaults.
- [x] Add tests for text-only button sizing, nested content sizing, and styled-size overrides.
- [x] Verify button layout does not trigger text-input caret/scroll calculations.

**Acceptance Checks:**
- [x] Layout tests prove a text-only button has non-zero content and border-box size.
- [x] Layout tests prove explicit `width` and `height` are respected.
- [x] Layout tests prove nested inline content contributes to height/width or is handled by the documented v1 fallback.

**Dependencies:** Steps 1 and 2.

**Risks:** Content-based auto width may require careful ordering because current block layout often resolves width before children. If that is too invasive, prefer a conservative default button width in v1 and document content-based width as deferred.

### Step 4: Add Activation Semantics
**Purpose:** Make buttons behave like controls instead of passive elements.

**Changes:**
- [x] Define the v1 activation contract: mouse click activates on press/release inside, keyboard activates on `Space` and/or `Enter` when focused, and disabled buttons do not activate if disabled support is included.
- [x] Reuse existing focused/pressed/click state where possible.
- [x] Add a small backend-agnostic `ButtonBehavior` only if listener logic would otherwise become scattered.
- [x] Wire `SystemMouseClickEventListener` so button activation coexists with existing focus and `MouseClickEvent` emission.
- [x] Wire `SystemKeyEventListener` so focused button activation does not invoke text-input or textarea behavior.
- [x] Decide whether activation is represented by existing `MouseClickEvent`/`KeyboardEvent`, a new GUI event, or both; add tests for the chosen contract.

**Acceptance Checks:**
- [x] Listener tests prove mouse press/release/click on a button emits the expected activation signal.
- [x] Listener tests prove `Enter` and/or `Space` on a focused button emits the expected activation signal.
- [x] Listener tests prove non-button elements keep current click/key behavior.
- [x] Listener tests prove text input and textarea behavior is unchanged.

**Dependencies:** Step 1.

**Risks:** Current event dispatch is exact-class based. If a new activation event is introduced, it needs explicit processor/listener tests and should not silently rely on superclass dispatch.

### Step 5: Add Default Button Styling and Render-State Proof
**Purpose:** Make an unstyled button visibly identifiable and show focus/hover/pressed state through existing rendering.

**Changes:**
- [x] Locate where default/user-agent-like styles are applied, if any, and add button defaults there; if none exist, add scoped demo CSS first and defer global defaults.
- [x] Define conservative defaults for padding, border, background, foreground, and focused/pressed/hovered variants using existing supported selectors/states.
- [x] Confirm generic `NvgElementRenderer`, `NvgBorderRenderer`, and `NvgTextRenderer` render button content correctly.
- [x] Add `NvgButtonRenderer` only if default/pressed visuals require rendering behavior that cannot be expressed with style.
- [x] Add backend tests only where useful; no new renderer code was added, and the narrow proof covers button text through the existing text renderer.

**Acceptance Checks:**
- [x] Manual or automated render proof shows text content inside the button is visible.
- [x] Hover/focus/pressed styling changes are visible if supported by current state selectors.
- [x] Existing NanoVG tests pass.

**Implementation Notes:**
- No global user-agent stylesheet mechanism was found; button defaults were added as scoped demo CSS in `button-demo.css`.
- `:focus` and `:active` now resolve through existing `focused` and `pressed` element state, alongside existing `:hover` support.
- No `NvgButtonRenderer` was added because `ButtonElement` is content-bearing and renders through the generic element, border, and text renderer path.

**Dependencies:** Steps 2, 3, and 4.

**Risks:** Adding global default styles can affect every demo. Keep first implementation scoped unless the repo already has a clear default stylesheet mechanism.

### Step 6: Update Demo Coverage
**Purpose:** Provide a real manual verification path for button behavior and the `<button>` versus `<input type="button">` difference.

**Changes:**
- [x] Add a `ButtonExample` or extend the existing complex demo with a focused button section.
- [x] Add demo XML/CSS showing `<button>Save</button>`, a button with nested inline content, and `<input type="button" value="Save">`.
- [x] Display activation feedback in the demo through existing event listeners or a small visible state update.
- [x] Ensure the demo key mapping supports `Enter` and `Space` for keyboard activation.

**Acceptance Checks:**
- [x] `:spinygui.demo.complex:classes` succeeds.
- [ ] Manual demo check verifies click activation, keyboard activation, focus behavior, and visible pressed/focused state.
- [ ] Manual demo check verifies `<input type="button">` remains value-based and does not accept child content.

**Implementation Notes:**
- `ButtonExample` loads dedicated XML/CSS resources and updates visible status text from `ActionEvent` listeners.
- The shared demo keyboard map now includes `Space`, matching the core activation listener contract.
- Screenshot evidence shows the plain and nested `<button>` controls rendering, focus/pressed styling changing, and visible `ActionEvent` status updates for button activation.

**Dependencies:** Steps 2, 4, and 5.

**Risks:** A demo that only renders a static button is insufficient; it must prove activation through the real event path.

### Step 7: Document Scope and Deferred Browser Semantics
**Purpose:** Keep the supported contract explicit and prevent future confusion with full HTML form behavior.

**Changes:**
- [ ] Update this plan's checkboxes and notes as implementation evidence accumulates.
- [ ] Add or update a short feature document if implementation creates behavior not obvious from tests.
- [ ] Document default `type` behavior, activation keys, and the difference from `<input type="button">`.
- [ ] Document that native form submission/reset semantics remain unsupported in v1.

**Acceptance Checks:**
- [ ] Documentation names supported button types and unsupported form semantics.
- [ ] Documentation references the demo verification path.

**Dependencies:** Steps 1-6.

**Risks:** Avoid documenting browser parity unless tests and demo prove it.

## Verification Strategy
- [x] Model/parser checks: `.\gradlew.bat :spinygui.core:test --tests *NodeBuilderTest --tests *DefaultNodeParserTest`
- [x] Layout checks: `.\gradlew.bat :spinygui.core:test --tests *BlockLayoutTest`
- [x] Event checks: `.\gradlew.bat :spinygui.core:test --tests *SystemMouseClickEventListenerTest --tests *SystemKeyEventListenerTest`
- [x] Backend checks if renderer code changes: `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`
- [ ] Final validation: `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`
- [ ] Manual demo validation: click, focus, keyboard activation, pressed/focused visuals, nested button content, and comparison with `<input type="button">`.

## Review Boundaries
- Commit 1: button node model, builders, parser, and serialization tests.
- Commit 2: layout defaults and layout tests.
- Commit 3: activation behavior and event listener tests.
- Commit 4: default/demo styling and rendering proof.
- Commit 5: demo example and documentation updates.

## Deferred Work
- Full form owner, submit, reset, and validation behavior.
- Disabled, autofocus, name/value submitter data, and form attributes.
- Accessibility tree/role/name support.
- Tab-order keyboard navigation.
- Rich browser-compatible default styling and pseudo-class parity.
- Dedicated renderer only if generic element/text rendering proves insufficient.
