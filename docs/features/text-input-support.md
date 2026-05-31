eqwe# Text Input Support Plan

## Goal
Add first-class support for `<input type="text">`: parse/build it as an input element, lay it out as a single-line control, render its value and caret, focus it on click, and edit its value from character/key events.

## Non-Goals
- Selection ranges, clipboard, undo/redo, IME composition.
- Other input types: password, number, checkbox, radio, submit, etc.
- Full browser form submission semantics.
- Rich CSS pseudo-classes beyond existing `focused`, `hovered`, `pressed` state.

## Context
- `NodeBuilder` already has input constants and builders, but parser currently creates generic `EmptyElement` for `<input>`.
- Core already has focus, mouse click, char, and keyboard events.
- `TextMeasurer.getTextCaretMetrics(...)` supports caret positioning.
- NanoVG rendering should remain backend-specific; input state and behavior should live in core.
- The demo harness may need GLFW char/key event wiring before manual typing works end-to-end.
- Input types should use composition, not per-type element inheritance: keep one `InputElement` node and dispatch type-specific behavior through handlers/strategies keyed by the `type` attribute.

## Assumptions and Open Questions
- Assumption: `<input>` with missing `type` should behave as `type="text"`.
- Assumption: runtime text value should be explicit node state, not only a raw attribute map mutation.
- Question: Should edited value serialize back into the `value` attribute in `DefaultNodeParser.toHtml(...)`, or should serialization preserve original attributes only?

## Step-by-Step Plan

### Step 1: Add Input Element Model
**Purpose:** Give text inputs explicit runtime state without special-casing generic `Element` everywhere.

**Changes:**
- [x] Add `InputElement extends EmptyElement` in `core.node`.
- [x] Add text-input state fields: `type`, `value`, `caretIndex`, optional horizontal text scroll.
- [x] Initialize `type` from `type` attribute, defaulting to `text`.
- [x] Initialize `value` from `value` attribute.
- [x] Update `NodeBuilder.input(...)` methods to return `InputElement`.
- [x] Document that `InputElement` is the only input node class; future input types should add composed behavior handlers, not subclasses such as `TextInputElement` or `CheckboxInputElement`.

**Acceptance Checks:**
- [x] Unit tests prove `NodeBuilder.input()` creates `type=text` with empty value.
- [x] Unit tests prove `NodeBuilder.input("text", "name", "abc")` initializes runtime value as `abc`.
- [x] Existing node tests still compile.

**Dependencies:** None.

**Risks:** Keep `InputElement` small; do not turn it into a general form framework or a subclass hierarchy for every input type.

### Step 2: Parse and Serialize Text Inputs
**Purpose:** Ensure HTML-like input markup produces the new node type.

**Changes:**
- [x] Update `DefaultNodeParser` to create `InputElement` for `input` tags.
- [x] Preserve current empty-element behavior: no children.
- [x] Add parser tests for `<input>`, `<input type="text">`, and `<input type="button">`.
- [x] Decide and test `toHtml(...)` behavior for runtime value vs original `value` attribute.

**Acceptance Checks:**
- [x] `<input value="abc">` parses as `InputElement` with `type=text` and value `abc`.
- [x] Unsupported/non-text input types still parse safely, even if not interactive yet.
- [x] `DefaultNodeParserTest` passes.

**Dependencies:** Step 1.

**Risks:** Avoid breaking existing generic empty-element parsing.

### Step 3: Layout Single-Line Text Inputs
**Purpose:** Make text inputs occupy sensible geometry and expose a content box for text rendering.

**Changes:**
- [x] Add input-specific layout handling in `BlockLayout` or a small helper used by `BlockLayout`.
- [x] For `type=text`, compute auto height from font metrics plus padding/border.
- [x] Keep width CSS-driven; if no width is specified, use a small default control width.
- [x] Clip visible text to the input content box through existing overflow/scissor behavior.

**Acceptance Checks:**
- [x] Layout tests prove text input gets non-zero width/height without children.
- [x] Styled width/height still override defaults.
- [x] Scroll/client size remains coherent.

**Dependencies:** Step 1.

**Risks:** Current display defaults are block-level. Do not introduce `inline-block` unless separately planned.

### Step 4: Implement Core Text Editing Behavior
**Purpose:** Make focused text inputs editable through existing system event flow.

**Changes:**
- [x] Add a small backend-agnostic text input behavior/controller for `type=text`.
- [x] On `SystemCharEvent`, insert printable input into focused `InputElement`.
- [x] On `SystemKeyEvent`, handle `BACKSPACE`, `DELETE`, `LEFT`, `RIGHT`, `HOME`, `END`.
- [x] Continue emitting existing `CharEvent`/`KeyboardEvent` so user listeners still work.

**Acceptance Checks:**
- [x] Tests prove character insertion updates value and caret.
- [x] Tests prove backspace/delete mutate around the caret correctly.
- [x] Tests prove arrow/home/end update caret without changing value.
- [x] Non-input focused elements still receive current events unchanged.

**Dependencies:** Steps 1 and 2.

**Risks:** Exact-class event dispatch means behavior must be wired in system listeners or another explicitly invoked core service.

### Step 5: Add Mouse Caret Placement
**Purpose:** Clicking inside text input should focus it and put caret under the pointer.

**Changes:**
- [x] Extend mouse press handling for focused/clicked `InputElement`.
- [x] Use `TextMeasurer.getTextCaretMetrics(...)` with local input text x.
- [x] Account for padding, border, and horizontal text scroll.
- [x] Keep normal focus and click events intact.

**Acceptance Checks:**
- [x] Tests prove clicking at start/middle/end sets expected caret index.
- [x] Tests prove clicking outside text but inside input clamps caret to start/end.
- [x] Existing mouse focus tests pass.

**Dependencies:** Steps 1, 3, and 4.

**Risks:** Needs consistent coordinate space: border-box click position vs content-box text position.

### Step 6: Render Text Input Value and Caret in NanoVG
**Purpose:** Make the control visible and usable in the current backend.

**Changes:**
- [x] Add `NvgInputRenderer` or extend renderer traversal to detect `InputElement`.
- [x] Draw input background/border through existing element/border renderers.
- [x] Draw single-line value text clipped to content box.
- [x] Draw caret only when focused.
- [x] Reuse `NvgFontRegistry` and `TextMeasurer`; do not calculate caret through NanoVG glyph APIs.

**Acceptance Checks:**
- [x] Backend tests with sinks verify value text and caret draw calls.
- [ ] Manual demo check: click input, type text, move caret, delete text.
- [x] Existing NanoVG debug and text renderer tests pass.

**Dependencies:** Steps 1, 3, 4, and 5.

**Risks:** Rendering must not mutate input model except possibly read-only caret blink state if later added.

### Step 7: Wire Demo and Add a Focused Example
**Purpose:** Provide a small runnable scenario for manual verification.

**Changes:**
- [x] Ensure GLFW char callback pushes `SystemCharEvent`.
- [x] Ensure GLFW key callback pushes `SystemKeyEvent` while preserving `F3` debug toggle.
- [x] Add a demo XML/CSS case with `<input type="text" value="hello">`.
- [x] Style it with explicit width, padding, border, focused background/border if supported.

**Acceptance Checks:**
- [ ] Demo accepts typed characters in the input.
- [ ] Backspace/delete/arrows/home/end behave as defined.
- [ ] Debug mode still toggles with `F3`.

**Dependencies:** Steps 4 and 6.

**Risks:** Demo key mapping may need a small GLFW-to-`KeyCode` adapter if not already available.

## Verification Strategy
- Narrow tests after model/parser work:
  - [ ] `.\gradlew.bat :spinygui.core:test --tests *NodeBuilder* --tests *DefaultNodeParser*`
- Narrow tests after editing/focus work:
  - [ ] `.\gradlew.bat :spinygui.core:test --tests *SystemCharEventListenerTest --tests *SystemKeyEventListenerTest --tests *SystemMouseClickEventListenerTest`
- Narrow tests after rendering work:
  - [ ] `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`
- Final validation:
  - [ ] `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`

## Review Boundaries
- Commit 1: model + parser.
- Commit 2: layout.
- Commit 3: editing behavior and caret placement.
- Commit 4: NanoVG rendering.
- Commit 5: demo wiring/example.

## Architecture Decision
- Use one `InputElement` class for the DOM-like node, regardless of `type`.
- Use composed behavior objects or services for type-specific layout, editing, validation, and rendering.
- Treat `type` as mutable element state. If `type` changes later, the behavior dispatch should change without replacing the node instance.
- Share behavior where possible: `text`, `search`, `email`, `tel`, `url`, and `password` can reuse most single-line text editing mechanics.
- Avoid subclasses such as `TextInputElement`, `PasswordInputElement`, or `CheckboxInputElement`; they would make runtime `type` changes and shared behavior harder to maintain.

## Deferred Work
- Text selection and clipboard.
- Placeholder support.
- Disabled/read-only attributes.
- Password masking.
- Forms and change/input event semantics.
- IME/composition support.
