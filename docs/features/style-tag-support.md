# Style Tag Support Plan

## Goal
Allow XML/HTML-like UI markup to include inline stylesheet blocks with `<style>...</style>`, so parsed `winframe` structures can carry their own CSS in `Frame.styleSheets()` and work without separate caller-side stylesheet parsing.

## Non-Goals
- External stylesheet loading through `<link rel="stylesheet">` or CSS `@import`.
- Browser-complete `<head>` semantics.
- Dynamic stylesheet mutation APIs after parsing.
- Supporting `<style>` as a visible layout/rendering node.
- Changing CSS selector, cascade, specificity, or property support beyond consuming existing parsed stylesheets.

## Context
- `DefaultNodeParser.fromHtml(...)` currently returns a `Node` and has a no-arg constructor used by demos and tests.
- `Frame` already owns ordered `styleSheets()` and `StyleManagerImpl.recalculate(frame)` consumes those stylesheets before inline `style` attributes.
- `StyleSheetParser` is a separate service created with a `PropertyStore`, so node parsing cannot parse CSS unless a stylesheet parser is explicitly provided.
- Current demo examples parse XML and CSS separately, then call `frame.styleSheets().add(styleSheetParser.parse(styles))`.
- `<textarea>` is precedent for parser special-casing tags whose text content is runtime data rather than normal child nodes.

## Assumptions and Open Questions
- Assumption: v1 support is frame-scoped. Parsed `<style>` blocks attach only when the resulting root is a `Frame`.
- Assumption: `<style>` blocks are consumed during parsing and omitted from the runtime child tree.
- Assumption: stylesheet order follows document order, and parsed `<style>` sheets are appended to `Frame.styleSheets()` in that order.
- Assumption: existing inline `style` attributes remain highest priority because `StyleManagerImpl` already applies them after frame stylesheets.
- Assumption: the no-arg `DefaultNodeParser` remains source-compatible; stylesheet-aware parsing is enabled through a constructor or factory path that receives a `StyleSheetParser`.
- Question: Should `<style>` blocks outside a parsed `winframe` be rejected, ignored with a warning, or preserved as inert elements? The least surprising v1 behavior is to warn and skip because non-frame roots have nowhere to store stylesheets.

## Step-by-Step Plan

### Step 1: Define Parser Contract for Embedded Stylesheets
**Purpose:** Make the behavior explicit before changing parser internals.

**Changes:**
- [ ] Add or update feature documentation with the v1 contract for `<style>` blocks, supported placement, ordering, serialization, and failure behavior.
- [ ] Decide how `DefaultNodeParser` receives an optional `StyleSheetParser` while preserving the current no-arg constructor and `NodeParser` interface.
- [ ] Document that `<style>` is not represented as a normal `Element` in the parsed child tree.

**Acceptance Checks:**
- [ ] The docs state where parsed stylesheets are stored and when no stylesheet parser is configured.
- [ ] The chosen API does not require existing no-arg `new DefaultNodeParser()` call sites to change.

**Dependencies:** None.

**Risks:** If the contract allows style tags in arbitrary non-frame roots, the current `Node` return type has no stylesheet attachment point. Keep v1 frame-scoped unless a broader document model is introduced.

### Step 2: Add Stylesheet-Aware Node Parsing
**Purpose:** Parse `<style>` content into `Frame.styleSheets()` without creating renderable style nodes.

**Changes:**
- [ ] Add stylesheet-parser injection to `DefaultNodeParser`, likely `DefaultNodeParser()` plus `DefaultNodeParser(StyleSheetParser styleSheetParser)`.
- [ ] Extend `NodeConverterContext` to track the current frame and collect stylesheet parse failures.
- [ ] Special-case `style` tags in `DefaultNodeParser`: read raw CSS text, parse it with the configured `StyleSheetParser`, append it to the current frame, and return `null` so it is not added as a child.
- [ ] Keep existing behavior for input, button, textarea, empty elements, text nodes, and normal elements unchanged.

**Acceptance Checks:**
- [ ] Parser tests prove `<winframe><style>div { color: red; }</style><div id="target">A</div></winframe>` returns a `Frame` with one stylesheet and one child `div`.
- [ ] Parser tests prove multiple `<style>` blocks preserve document order in `Frame.styleSheets()`.
- [ ] Parser tests prove parsing without a configured `StyleSheetParser` does not crash and has documented behavior.
- [ ] Existing `DefaultNodeParserTest` and whitespace parser tests still pass.

**Dependencies:** Step 1.

**Risks:** Jsoup may expose `<style>` contents differently from normal text nodes. Use the element's data/raw text access rather than child text parsing if needed.

### Step 3: Verify Style Resolution End to End
**Purpose:** Prove embedded stylesheets participate in the existing cascade exactly like manually added frame stylesheets.

**Changes:**
- [ ] Add an integration-style parser plus style-manager test using the real `StyleSheetParserFactory` and `DefaultPropertyStoreProvider`.
- [ ] Assert a selector from `<style>` updates `ResolvedStyle` after `StyleManagerImpl.recalculate(frame)`.
- [ ] Assert inline `style` attributes still override matching `<style>` rules.
- [ ] Assert unsupported/invalid CSS in a `<style>` block follows the documented parser failure behavior.

**Acceptance Checks:**
- [ ] A parsed embedded stylesheet changes a target element's typed resolved style, for example `color` or `background-color`.
- [ ] Inline style remains the last applied ruleset and wins over embedded stylesheet declarations.
- [ ] Invalid embedded CSS is covered by a focused test rather than only manual logging.

**Dependencies:** Step 2.

**Risks:** Current stylesheet parsing throws `ParseException`; swallowing failures may hide author errors, but throwing may make UI markup brittle. Pick one behavior in Step 1 and test it directly.

### Step 4: Serialize or Intentionally Omit Embedded Styles
**Purpose:** Avoid ambiguous round-trip behavior for `NodeParser.toHtml(...)`.

**Changes:**
- [ ] Decide whether `Frame.styleSheets()` serializes back as leading `<style>` children or remains omitted from `toHtml(...)`.
- [ ] If serializing, update `DefaultNodeParser` to require a `StyleSheetParser` for `toCss(...)` and emit stylesheet blocks before normal child nodes.
- [ ] If omitting, document that `toHtml(...)` serializes the node tree only and does not round-trip `Frame.styleSheets()`.
- [ ] Add tests for the chosen behavior.

**Acceptance Checks:**
- [ ] `toHtml(frame, false)` has deterministic behavior for frames with stylesheets.
- [ ] Existing input, button, textarea, and normal child serialization tests still pass.

**Dependencies:** Step 2.

**Risks:** Serializing parsed styles changes existing `toHtml(Frame)` output for callers that already add stylesheets manually. Omit-by-default is lower risk; stylesheet serialization can be added later as an explicit API if needed.

### Step 5: Update Demo Markup to Use `<style>`
**Purpose:** Show the feature in the real demo path and reduce split XML/CSS boilerplate in at least one example.

**Changes:**
- [ ] Update one complex demo resource or inline demo XML to include a `<style>` block.
- [ ] Change that demo's parser construction to pass the existing `styleSheetParser` into `DefaultNodeParser`.
- [ ] Remove the now-redundant manual `frame.styleSheets().add(styleSheetParser.parse(styles))` call for that demo only.
- [ ] Keep other demos unchanged unless the new path is proven stable.

**Acceptance Checks:**
- [ ] The updated demo still renders with the expected visual styling.
- [ ] Manual smoke verifies that the `<style>` block is not visible as a UI element.
- [ ] The demo still works when style resolution is recalculated every frame.

**Dependencies:** Steps 2 and 3.

**Risks:** Demo visual verification is needed because parser/style tests do not prove the real render path is styled correctly.

### Step 6: Clean Up Documentation and Boundary References
**Purpose:** Make the new parsing path discoverable without overstating CSS support.

**Changes:**
- [ ] Update parser package documentation to mention embedded stylesheet support.
- [ ] Update any relevant README or demo notes that currently show XML and CSS as mandatory separate inputs.
- [ ] Leave `docs/features/css-properties-support.md` unchanged unless a new CSS construct is actually supported; `<style>` is markup integration, not a CSS property.

**Acceptance Checks:**
- [ ] Documentation distinguishes `<style>` tag support from unsupported `<link>` and `@import`.
- [ ] Documentation points callers to the stylesheet-aware `DefaultNodeParser` construction path.

**Dependencies:** Steps 2 through 5.

**Risks:** Avoid implying browser-level document/head support.

## Verification Strategy
- Run parser-focused tests after Step 2: `.\gradlew.bat :spinygui.core:test --tests "*DefaultNodeParserTest" --tests "*DefaultNodeParserWhitespaceTest"`.
- Run style integration tests after Step 3: `.\gradlew.bat :spinygui.core:test --tests "*DefaultNodeParserTest" --tests "*StyleManagerImplTest"`.
- Run full core tests before final review: `.\gradlew.bat :spinygui.core:test`.
- Run the updated complex demo after Step 5 and visually confirm embedded styles affect the UI and no `<style>` node is rendered.

## Review Boundaries
- Steps 1-2 can be one small parser contract and parser implementation change.
- Step 3 should be reviewable as test-backed cascade verification.
- Step 4 should be separate if serialization behavior is more than documentation.
- Step 5 should be a demo-only change once the parser path is proven.

## Deferred Work
- `<link rel="stylesheet" href="...">` with resource loading and path resolution.
- CSS `@import` loading, cycle detection, and ordering semantics.
- A richer document model that supports stylesheet metadata for non-frame roots.
- Runtime APIs for adding/removing embedded stylesheet blocks after parsing.
