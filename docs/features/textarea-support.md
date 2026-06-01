# Textarea Element Support Plan

## Goal
Add first-class support for `<textarea>`: parse/build it as a textarea element, initialize runtime value from child text, lay it out as a multiline control, render visible text and caret, focus it on click, and edit its value through existing character/key event flow.

## Non-Goals
- Clipboard, undo/redo, IME composition, spellcheck, and form submission semantics.
- Native browser parity for every textarea attribute.
- Horizontal/vertical resize handles.

## Context
- `InputElement` already provides the single-line input precedent for explicit runtime value, caret, selection, and viewport state.
- `DefaultNodeParser` special-cases controls that need runtime state.
- Core behavior stays backend-agnostic; NanoVG only renders already-resolved control state and metrics.

## Assumptions and Defaults
- Assumption: `<textarea>hello</textarea>` initializes runtime value to `hello`.
- Assumption: serialization writes current runtime value as textarea text content, not as a `value` attribute.
- Assumption: default size is `cols=20`, `rows=2`.
- Assumption: `Enter` inserts `\n`; `Shift+Enter` is not special in v1.

## Step-by-Step Plan

### Step 1: Add Textarea Node Model and Builders
**Purpose:** Represent textarea as an explicit editable multiline element.

**Changes:**
- [x] Add `TextareaElement extends Element`.
- [x] Add runtime fields for value, caret, selection, and text scroll offsets.
- [x] Add `NodeBuilder.textarea(...)`, `NODE_TEXTAREA`, `rows(...)`, and `cols(...)`.

**Acceptance Checks:**
- [x] Unit tests cover empty textarea builder state.
- [x] Unit tests cover multiline value initialization.
- [x] Unit tests cover caret/selection clamping when value shrinks.

**Dependencies:** None.

**Risks:** Avoid subclassing `InputElement`; textarea has different tag and multiline semantics.

### Step 2: Parse and Serialize `<textarea>`
**Purpose:** Round-trip textarea markup with correct value semantics.

**Changes:**
- [x] Update `DefaultNodeParser` to create `TextareaElement`.
- [x] Initialize value from textarea text content.
- [x] Serialize runtime value as textarea text content.

**Acceptance Checks:**
- [x] Parser tests cover value parsing without normal child nodes.
- [x] Parser tests cover escaped text serialization.

**Dependencies:** Step 1.

**Risks:** Jsoup whitespace normalization should be treated as the v1 parser behavior.

### Step 3: Add Multiline Text Control Metrics
**Purpose:** Share line, caret, and hit-test calculations across input, layout, and rendering.

**Changes:**
- [x] Add `MultilineTextControlMetrics`.
- [x] Support hard line breaks and wrapped measured lines.
- [x] Provide caret-to-geometry and cursor-to-index helpers.

**Acceptance Checks:**
- [ ] Add direct metric unit tests for hard line breaks and wrapped lines.

**Dependencies:** Step 1.

**Risks:** The helper adapts hard line breaks around existing `TextMeasurer` APIs.

### Step 4: Layout Textarea Controls
**Purpose:** Give textarea predictable block geometry.

**Changes:**
- [x] Extend block layout for textarea width and height.
- [x] Derive auto width from `cols`; derive auto height from `rows`.
- [x] Respect styled width/height overrides.

**Acceptance Checks:**
- [x] Layout tests cover default auto size.
- [x] Layout tests cover `rows` and `cols`.
- [x] Layout tests cover styled size overrides.

**Dependencies:** Steps 1 and 3.

**Risks:** V1 keeps block-level behavior consistent with existing form-control layout.

### Step 5: Implement Multiline Editing Behavior
**Purpose:** Make focused textarea editable through existing events.

**Changes:**
- [x] Add `TextareaBehavior`.
- [x] Wire char and key listeners for focused `TextareaElement`.
- [x] Support printable insertion, Enter, Backspace, Delete, arrows, Home, and End.

**Acceptance Checks:**
- [x] Behavior tests cover insertion, Enter, Backspace around newline, Home, and End.
- [x] Listener tests cover char insertion and key handling.

**Dependencies:** Steps 1 and 3.

**Risks:** Up/down navigation depends on a configured `TextMeasurer`.

### Step 6: Add Mouse Caret Placement and Viewport Visibility
**Purpose:** Place and keep the caret inside visible textarea content.

**Changes:**
- [x] Add textarea mouse caret behavior.
- [x] Add textarea viewport behavior.
- [x] Wire mouse, char, and key listeners to invoke textarea viewport/caret logic.

**Acceptance Checks:**
- [ ] Add direct mouse caret tests with a fixed text measurer.
- [ ] Add viewport scroll tests with a fixed text measurer.

**Dependencies:** Steps 3 and 5.

**Risks:** Coordinate correctness depends on shared content-box and scroll-offset calculations.

### Step 7: Render Textarea in NanoVG
**Purpose:** Make textarea visible and usable in the current backend.

**Changes:**
- [x] Add `NvgTextareaRenderer`.
- [x] Wire textarea rendering into `NvgRenderer`.
- [x] Clip visible text, selection rectangles, and caret to the content box.

**Acceptance Checks:**
- [ ] Add renderer sink tests for line text and caret placement.
- [ ] Manual demo check verifies typing and caret movement.

**Dependencies:** Steps 3, 4, 5, and 6.

**Risks:** Renderer must remain read-only against textarea model state.

### Step 8: Add Demo and Documentation
**Purpose:** Provide a manual verification target and maintain the plan.

**Changes:**
- [x] Add `TextareaExample`.
- [x] Add textarea demo XML/CSS.
- [x] Add this feature plan document.

**Acceptance Checks:**
- [ ] `:spinygui.demo.complex:classes` succeeds.
- [ ] Manual demo check verifies click, type, Enter, arrows, and delete.

**Dependencies:** Steps 2, 5, and 7.

**Risks:** Demo should expose actual core behavior rather than patching around it.

## Verification Strategy
- [ ] `.\gradlew.bat :spinygui.core:test --tests *NodeBuilderTest --tests *DefaultNodeParserTest`
- [ ] `.\gradlew.bat :spinygui.core:test --tests *BlockLayoutTest`
- [ ] `.\gradlew.bat :spinygui.core:test --tests *TextareaBehaviorTest --tests *SystemCharEventListenerTest --tests *SystemKeyEventListenerTest --tests *SystemMouseClickEventListenerTest`
- [ ] `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test`
- [ ] `.\gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:classes`

## Deferred Work
- Clipboard, undo/redo, IME composition.
- Placeholder, disabled, readonly, maxlength, minlength, and wrap semantics.
- Native form submission and input/change event semantics.
- Native resize handles.
- Full browser-compatible wrapping and bidi text behavior.
