# Text Whitespace Handling in Inline Layout and Rendering

## Goal
Make spaces between words consistently survive parsing, inline layout, measurement, wrapping, and NanoVG rendering so text such as `Horizontal auto` is measured with a space advance and painted with visible separation between words.

## Non-Goals
- Full CSS Text Level 3 conformance beyond the currently supported `white-space`, `word-break`, `overflow-wrap`, and `tab-size` subset.
- Hyphenation, bidi text, shaping, ligature control, justification, or `text-spacing` behavior.
- Rich text editing behavior or caret movement around whitespace.
- Visual scrollbar rendering or overflow behavior changes.

## Context
- `DefaultNodeParser.createNodeFromContent` currently uses `text.getWholeText().trim()`, which removes leading and trailing whitespace from every text node before layout sees it.
- `InlineFormattingContext.textUnits` normalizes whitespace and emits explicit `" "` units for collapsible spaces, then converts units to `InlineFragment` instances.
- `InlineFormattingContextTest` already covers several whitespace fragments, but coverage is mostly at the layout-fragment level and does not prove parser-to-renderer consistency.
- `FontServiceImpl.measureText` measures spaces as code points and has a unit test for whitespace advance.
- `NvgTextRenderer.renderFragment` paints each `InlineFragment` independently at `offset + fragment.x()`, so any missing space advance must be caught before or at this boundary.
- The observed demo issue is that labels can visually collapse words, e.g. `Horizontalauto`, even when source text contains a space.

## Assumptions and Open Questions
- Assumption: The first supported behavior should match CSS `white-space: normal`: collapse whitespace runs to a single space, trim line-edge collapsible spaces, and preserve a measurable inter-word advance.
- Assumption: `white-space: pre`, `pre-wrap`, and `pre-line` should continue to follow the behavior already encoded in `InlineFormattingContext.normalize`.
- Assumption: Parser behavior can change from unconditional per-node `trim()` to CSS-compatible text-node preservation without requiring a new DOM model.
- Question: Should text nodes that are only indentation/newline whitespace between block elements be preserved in the node tree but later ignored by layout, or dropped at parse time when they contain no non-collapsible content? This choice affects parser tests and pretty XML input.

## Step-by-Step Plan

### Step 1: Add Reproduction Tests for Parser Whitespace Boundaries
**Purpose:** Prove whether spaces are lost before layout, especially around text-node boundaries and inline elements.

**Changes:**
- [x] Add parser tests in `spinygui.core/src/test/java/com/spinyowl/spinygui/core/parser/impl/DefaultNodeParserTest.java` or a new focused test class.
- [x] Cover a single text node with an internal space, e.g. `<div>Horizontal auto</div>`, and assert the `Text.content()` retains the internal space.
- [x] Cover text around inline child elements, e.g. `<div>Hello <span>wide</span> world</div>`, and assert leading/trailing text-node spaces are either preserved or explicitly documented as collapsed later.
- [x] Cover indentation-only text nodes in multiline XML so parser behavior for formatting whitespace is intentional.

**Acceptance Checks:**
- [x] Tests fail on any currently broken parser behavior without requiring renderer execution.
- [x] The expected parser behavior is documented in test names and assertions.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests *DefaultNodeParser*`.

**Dependencies:** None.

**Risks:** Removing `trim()` blindly can introduce whitespace-only text nodes from formatted XML into layout. Mitigate by making indentation-only behavior explicit in this step before changing parser code.

### Step 2: Define and Centralize Inline Whitespace Normalization
**Purpose:** Make whitespace transformation inspectable and consistent instead of spread across parser trimming and inline layout splitting.

**Changes:**
- [x] Add a small package-local helper near `InlineFormattingContext`, for example `InlineWhitespace`, that normalizes text according to `WhiteSpace`.
- [x] Move the existing `normalize` behavior from `InlineFormattingContext` into the helper without changing behavior yet.
- [x] Add focused unit tests for `normal`, `nowrap`, `pre`, `pre-wrap`, and `pre-line`, including tabs and CRLF normalization.
- [x] Decide and encode whether leading/trailing collapsible spaces are kept as units and later trimmed at line boundaries, or removed during normalization.

**Acceptance Checks:**
- [x] Existing `InlineFormattingContextTest` continues to pass.
- [x] New helper tests prove internal word spaces remain as a single normalized space for `normal`/`nowrap`.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests *InlineWhitespace* --tests *InlineFormattingContextTest`.

**Dependencies:** Step 1.

**Risks:** Centralizing normalization can look like a refactor but change layout. Keep this step behavior-preserving except for test-visible documentation of intended behavior.

### Step 3: Preserve Required Text-Node Spaces Through Parsing
**Purpose:** Stop parser-level trimming from deleting spaces that inline layout needs to measure between words and inline descendants.

**Changes:**
- [x] Replace unconditional `text.getWholeText().trim()` in `DefaultNodeParser` with a parser policy that preserves non-empty text content and discards only whitespace nodes that are safe to ignore.
- [x] If preserving whitespace-only nodes is necessary for inline separation, defer trimming/collapsing to inline layout rather than parser.
- [x] Update parser tests from Step 1 to reflect the final policy.
- [x] Add a regression test for parsed XML with text before and after an inline element producing the expected text node contents.

**Acceptance Checks:**
- [x] Parsed `Horizontal auto` still contains the internal space.
- [x] Parsed `Hello <span>wide</span> world` retains enough boundary whitespace for inline layout to produce `Hello wide world`.
- [x] Pretty-formatted block XML does not create visible indentation text in block layout.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests *DefaultNodeParser* --tests *BlockLayoutTest --tests *InlineFormattingContextTest`.

**Dependencies:** Steps 1 and 2.

**Risks:** Parser changes can alter many documents. Stop and split further if formatted block markup starts generating visible whitespace unexpectedly.

### Step 4: Verify Inline Fragment Advances for Spaces
**Purpose:** Prove inline layout emits measurable advances for spaces and positions following fragments correctly.

**Changes:**
- [x] Add or extend `InlineFormattingContextTest` with simple labels such as `Horizontal auto` and `Visible overflow`.
- [x] Assert the fragment sequence includes a `" "` fragment or an equivalent measured advance between words.
- [x] Assert the second word's `x` equals first word width plus space width under the fake `TextMeasurer`.
- [x] Add a wrapping case where a line break trims trailing collapsible spaces but preserves inter-word spaces on the same line.
- [x] Add a boundary case where separate text nodes around an inline element preserve visual spacing.

**Acceptance Checks:**
- [x] Tests fail if the space fragment is removed without advancing `cursorX`.
- [x] Tests prove trailing line spaces are still trimmed intentionally.
- [x] Run `.\gradlew.bat :spinygui.core:test --tests *InlineFormattingContextTest`.

**Dependencies:** Steps 2 and 3.

**Risks:** Existing tests may assert exact fragment counts. Update them only when the new fragment model is explicitly better and documented by assertions.

### Step 5: Add Renderer-Level Text Sink Tests
**Purpose:** Verify `NvgTextRenderer` paints fragments at their computed positions without merging or re-normalizing away spaces.

**Changes:**
- [x] Extract NanoVG text drawing behind a small testable sink, similar to `NvgClipStack`, so tests can assert text draw calls without an OpenGL context.
- [x] Keep the production path using NanoVG `nvgText` through the sink.
- [x] Add tests that render fragments for `Horizontal`, `" "`, and `auto`, asserting the draw calls use increasing `x` positions from layout.
- [x] Add a test proving whitespace-only fragments are either drawn harmlessly or skipped only after their advance has already affected later fragment positions.

**Acceptance Checks:**
- [x] Renderer tests do not require a live OpenGL/NanoVG context.
- [x] Renderer tests prove no renderer-side concatenation produces `Horizontalauto`.
- [x] Run `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests *NvgTextRenderer*`.

**Dependencies:** Step 4.

**Risks:** Extracting a text sink can spread through renderer code. Keep it localized to `NvgTextRenderer` and avoid changing font loading behavior in this step.

### Step 6: Add End-to-End Layout Tests for Parsed Labels
**Purpose:** Cover the exact parser + style + layout path used by demos.

**Changes:**
- [x] Add an integration-style core test that parses a small XML snippet, applies CSS, runs layout, and inspects resulting `Text.inlineFragments()`.
- [x] Include the labels from the overflow demo: `Horizontal auto` and `Visible overflow`.
- [x] Assert the second word's fragment starts after a positive space advance.
- [x] Include a separate text-node boundary case, e.g. `Hello <span>wide</span> world`.

**Acceptance Checks:**
- [x] The test fails if parser trimming, normalization, or layout drops inter-word advances.
- [x] The test does not depend on NanoVG or a window.
- [x] Run `.\gradlew.bat :spinygui.core:test`.

**Dependencies:** Steps 3 and 4.

**Risks:** End-to-end tests may be brittle if exact font metrics vary. Use the existing fake `TextMeasurer` or assert relative positions where possible.

### Step 7: Update Demo Text and Manual Verification Notes
**Purpose:** Keep demos readable while preserving the regression coverage for whitespace.

**Changes:**
- [x] Update `overflow-demo.xml` labels only if needed after the engine fix; avoid hyphenating labels as a workaround once spaces are correct.
- [x] Add a short manual verification note to `docs/features/text-whitespace-inline-layout.md` or the overflow plan stating that labels should render as `Horizontal auto` and `Visible overflow`.
- [x] If a demo screenshot or manual checklist exists, update it to include whitespace between words as an explicit visual check.

**Acceptance Checks:**
- [ ] Run `OverflowExample` manually and verify labels show visible spaces between words.
- [ ] Verify vertical/horizontal/nested/visible overflow behavior still works after text changes.
- [x] Run `.\gradlew.bat test`.

**Dependencies:** Steps 5 and 6.

**Risks:** Manual verification depends on the local OpenGL environment. If unavailable, record the limitation and rely on parser/layout/backend tests.

**Step 7 note:** `overflow-demo.xml` already uses spaced labels (`Horizontal auto` and `Visible overflow`), so no demo text workaround needed to be reverted. No demo screenshot or separate manual checklist file exists; this plan section is the manual checklist. Interactive `OverflowExample` verification was not performed in this non-interactive run, so local manual verification should confirm that those labels render with visible spaces while vertical, horizontal, nested, and visible overflow behavior still works.

## Verification Strategy
- Parser changes: `.\gradlew.bat :spinygui.core:test --tests *DefaultNodeParser*`
- Inline layout changes: `.\gradlew.bat :spinygui.core:test --tests *InlineFormattingContextTest`
- Font/text metric changes: `.\gradlew.bat :spinygui.core:test --tests *FontServiceImplTest`
- NanoVG renderer changes: `.\gradlew.bat :spinygui.core.backend.lwjgl.nanovg:test --tests *NvgTextRenderer*`
- Final validation: `.\gradlew.bat test`
- Manual validation: run `OverflowExample` and verify labels render with visible spaces and overflow behavior still works.

## Review Boundaries
- Step 1 should be a test-only commit documenting current parser behavior.
- Step 2 should be a behavior-preserving normalization extraction with tests.
- Step 3 should be a parser behavior commit.
- Step 4 should be an inline layout behavior/test commit.
- Step 5 should be a NanoVG renderer testability commit.
- Steps 6 and 7 can be separate if demo/manual verification changes are not needed for the automated regression.

## Deferred Work
- Full CSS `white-space-collapse` and `text-wrap-mode`.
- Unicode line breaking classes beyond the current word/space handling.
- Bidi, shaping, kerning, and ligature-sensitive text measurement/rendering.
- Pixel-level screenshot tests for rendered text.
