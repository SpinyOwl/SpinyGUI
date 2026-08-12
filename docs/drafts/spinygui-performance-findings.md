# SpinyGUI Performance Findings and Proposed Improvements

## Document Status

- Status: Draft
- Scope: SpinyGUI CPU and allocation behavior observed through the Rogue Crawler client
- Evidence source: `build/e85-diagnostics.jfr`
- Related closeout: [E8.5/M33.5/P4/T2](<../work/E8.5/M33.5/P4/T2 - Smoke Profile and Close E8.5.md>)
- Roadmap effect: None. This draft does not authorize implementation or change an epic or milestone status.

## Purpose

Record the exact SpinyGUI implementation hotspots identified during E8.5 profiling, explain why they
are expensive, and propose an ordered optimization strategy. The findings distinguish SpinyGUI-owned
costs from the uncapped Rogue Crawler render loop that amplifies them.

This document is not evidence that every sampled allocation originates from one named line. JFR
allocation events are sampled and stack-search counts are indicators rather than invocation counts.
The listed implementation sites are the strongest code-level matches for the observed categories and
must be validated through focused benchmarks and matched before/after recordings.

## Executive Summary

The E8.5 XML fragment parser is not the performance bottleneck. The recording contains one execution
sample involving `ClientUiFragmentSource.newInstance` and no allocation sample involving that method.
Automated E8.5 tests separately prove that unchanged diagnostics updates do not parse or build XML
fragment instances.

The dominant problem is allocation during every rendered UI frame:

- child-list access creates read-only wrappers and filtered child lists;
- render traversal repeatedly creates positions, rectangles, affine transforms, and state scopes;
- text rendering clones fragments and creates native UTF-8 buffers;
- style recalculation scans all rules, splits class strings with regular expressions, and reconstructs
  property maps;
- layout invalidation rebuilds temporary layout trees and can repeat complete layout up to four times;
- string-keyed `TreeMap` style storage adds comparison and node-allocation overhead.

The client was rendering at approximately 2,874.4 FPS during the smoke. This is outside SpinyGUI, but
it multiplies every per-render SpinyGUI cost. Frame limiting or VSync should therefore precede or
accompany library optimization so measurements represent a realistic presentation cadence.

## JFR Baseline

### Recording

| Fact | Value |
|---|---:|
| Runtime | Java 25.0.3, Windows amd64 |
| Entry point | `ClientShellMain` |
| Duration | 122 seconds |
| Start | 2026-07-25 14:03:31 UTC |
| Events | 265,035 |
| Observed frame rate | Approximately 2,874.4 FPS |

### Allocation and Garbage Collection

| Metric | Value |
|---|---:|
| Main-thread allocation | 198.1 GB |
| Main-thread allocation share | 99.92% |
| Approximate allocation rate | 1.62 GB/s |
| G1 young collections | 466 |
| Total GC pause | 348 ms |
| Average GC pause | 0.748 ms |
| P95 GC pause | 0.987 ms |
| P99 GC pause | 1.29 ms |
| Maximum GC pause | 1.68 ms |
| Observed live post-GC heap | Approximately 153-154 MB |

The recording shows high transient allocation rather than retained-heap growth. No object-statistics
class exceeded one percent of the heap. Short pauses explain why the smoke remained responsive, but
they do not make the allocation rate acceptable.

At the observed FPS, the allocation rate is roughly 565 KB per rendered frame. If the per-render
portion scaled linearly, 60 FPS would reduce it to approximately 34 MB/s and 120 FPS to approximately
68 MB/s. Those are estimates, not acceptance thresholds; matched capped recordings are required.

### CPU and Sampled Hot Methods

| Metric | Value |
|---|---:|
| JVM user load | 6.87% |
| JVM system load | 1.47% |
| Main-thread user load | 5.67% |
| Main-thread system load | 0.38% |
| Machine average load | 21.90% |

| Sampled method | Samples |
|---|---:|
| `HashMap.getNode` | 8.96% |
| `TreeMap.getEntry` | 5.82% |
| Regex greedy matching | 5.42% |
| `String.compareTo` | 3.40% |
| `AffineTransform.multiply` | 3.11% |
| `Node.layoutAbsolutePosition` | 2.80% |
| `TreeMap.fixAfterInsertion` | 2.59% |
| `NvgTextRenderer.renderFragment` | 1.18% |

Stack-search aggregation found SpinyGUI renderer methods in 1,564 of 6,785 execution samples,
`StyleManagerImpl` in 1,158, and `LayoutServiceImpl` in 62. These figures include samples where the
method appears anywhere in the stack and are not invocation counts.

### Sampled Allocation Leaders

| Allocation site | Pressure |
|---|---:|
| `Collections.unmodifiableList` | 14.16% |
| `AffineTransform.multiply` | 7.86% |
| `Pattern.compile()` | 6.99% |
| `StreamSupport.stream` | 4.81% |
| `Rect.position` | 3.47% |
| Stream filtering | 3.38% |
| `SpinedBuffer` construction | 3.02% |
| `Pattern.compile(String)` | 2.37% |
| `HashMap.resize` | 2.31% |
| `Pattern.matcher` | 1.62% |
| `InlineFragment.Builder.build` | 1.24% |

## Findings

### F1: Read-Only Child Access Allocates on Every Call

**Evidence:** `Collections.unmodifiableList` is the largest sampled allocation site at 14.16%.

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Element.java:210-221`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Text.java:37`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/ResolvedStyle.java:158-159`

`Element.childNodes()` and `Element.inlineFragments()` call `Collections.unmodifiableList` every time.
That method creates a new wrapper even when the backing list did not change. These accessors are used
throughout render, style, layout, input, and mutation paths.

`Node.children()` compounds the problem:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Node.java:155-162`

It creates a stream pipeline and collects a new mutable list of element children on every call.

**Proposed changes:**

- Create one retained read-only view for each mutable backing list and return that view.
- Add package-private allocation-free traversal methods for SpinyGUI internals, such as
  `forEachChildElement(Consumer<Element>)` or a stable element-child view.
- Consider maintaining a child-element list alongside the node list if profiling shows repeated type
  filtering remains material.
- Preserve public mutation rules; do not expose the mutable backing collection.

**Expected effect:** Remove the largest sampled allocation category and much of the stream/list churn
visible in rendering and style traversal.

### F2: Position and Box Accessors Allocate Temporary Geometry

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/layout/Rect.java:17-34`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/layout/Box.java:37-139`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Node.java:217-242`

`Rect.position()` and `Rect.size()` create `Vector2f` instances. `Box` builds more vectors and expanded
`Rect` objects for content, padding, border, and margin queries. `Node.layoutAbsolutePosition()` then
recursively creates and mutates vectors while walking offset parents.

JFR attributed 3.47% of sampled allocation to `Rect.position` and 2.80% of hot-method samples to
`Node.layoutAbsolutePosition`.

**Proposed changes:**

- Add primitive geometry accessors for renderer, layout, and hit-testing paths.
- Store resolved layout-space and viewport-space X/Y values as primitive fields after layout.
- Add output-parameter overloads only where a vector API remains useful.
- Avoid building expanded `Rect` values when callers need only four primitive bounds.
- Invalidate cached absolute coordinates only when layout, ancestor scroll, or presentation transform
  changes.

**Expected effect:** Remove recurring vector and rectangle allocation from every node traversal.

### F3: Affine Transform Composition Allocates Per Element Per Frame

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/AffineTransform.java:23-58`
- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgRenderer.java:116-137`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/util/PresentationCoordinates.java:78-105`

Every translation and multiplication returns a new immutable `AffineTransform`. Rendering composes
translations around each element's border box every frame. Input coordinate mapping additionally
builds an ancestor list, reverses it, and repeatedly creates transforms and points.

JFR attributed 7.86% of sampled allocation and 3.11% of hot-method samples to
`AffineTransform.multiply`.

**Proposed changes:**

- Compute and retain each element's composed presentation transform when style/layout/scroll changes.
- Cache the inverse transform used for hit testing at the same invalidation boundary.
- Use primitive matrix coefficients or an internal mutable accumulator while composing transforms.
- Preserve immutable `AffineTransform` at public API boundaries if that contract remains desirable.
- Remove the ancestor-list allocation by using cached ancestry results or a non-allocating traversal.

**Expected effect:** Remove one of the largest per-element render allocation sources and reduce input
mapping overhead.

### F4: NanoVG Render State Allocates Scope Objects Per Element

Relevant code:

- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgRenderer.java:106-137`
- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgTransformState.java:18-33`
- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgSubtreeContentState.java:18-49`

The renderer creates `NvgTransformState` and `NvgSubtreeContentState` objects while entering every
element. Clipped elements also allocate position vectors and expanded padding/border rectangles.

**Proposed changes:**

- Replace allocated `AutoCloseable` state scopes with direct `nvgSave`/`nvgRestore` guarded by
  `try/finally`.
- Submit cached transform coefficients directly to NanoVG.
- Compute clip bounds from primitives instead of temporary vectors and rectangles.
- Retain full-tree painting for correctness initially; optimize allocations before introducing render
  caching.

**Expected effect:** Reduce per-element per-frame allocation without changing immediate-mode rendering
semantics.

### F5: Class Selectors Recompile Regex and Retokenize Classes

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/selector/simple/ClassAttributeSelector.java:23-30`

Each class selector test runs:

```java
var classList = classAttributes.split("\\s+");
return Arrays.asList(classList).contains(className);
```

This compiles or resolves a regular expression, allocates an array, wraps it as a list, and performs a
linear search. It happens while every stylesheet rule is tested against every element.

JFR attributed 6.99% plus 2.37% of sampled allocation to regex compilation and 5.42% of hot-method
samples to regex matching.

**Proposed changes:**

- Parse class tokens when the `class` attribute changes, not during selector testing.
- Store tokens in an immutable or narrowly mutable set optimized for membership checks.
- Invalidate only selector-match caches affected by the changed attribute.
- Index stylesheet rules by class so an element does not test unrelated class selectors.

**Expected effect:** Remove a major CPU/allocation source from every style refresh.

### F6: Text Whitespace Normalization Uses Regex During Layout

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/impl/InlineWhitespace.java:14-24`

Text normalization uses `replaceAll` for normal, nowrap, and pre-line handling. This contributes to
regex allocation whenever text is laid out.

**Proposed changes:**

- Normalize whitespace with a direct character scanner.
- Cache normalized content until text, `white-space`, or tab-size changes.
- Keep CSS whitespace behavior covered by focused Unicode, newline, tab, and repeated-space tests.

**Expected effect:** Reduce regex work during dirty text layout.

### F7: Style Recalculation Rebuilds the Entire Style State

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/manager/StyleManagerImpl.java:76-107`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/manager/StyleManagerImpl.java:129-207`

One recalculation performs two full element-tree traversals. For each element it:

- allocates rule and scrollbar collections;
- tests all user-agent and document rules;
- creates stream/sort/to-list pipelines;
- creates filtered `Ruleset` instances;
- copies the previous style map;
- clears and reapplies all declarations;
- computes every absent property;
- may copy the completed style map again for the style listener.

`StyleManagerImpl` appeared in 1,158 of 6,785 sampled execution stacks.

**Proposed changes:**

- Index selector candidates by ID, class, tag, pseudo-state, and universal fallback.
- Cache matched static rules until relevant attributes, ancestry, or stylesheet identity changes.
- Separate static selector changes from hover/focus/pressed presentation changes.
- Recalculate only dirty elements and dependency-affected descendants.
- Avoid previous/new style-map copies when no transition listener consumes them.
- Reuse per-element rule buffers where ownership permits.

**Expected effect:** Reduce style refresh from full-tree, all-rule work to affected-element work.

### F8: Resolved Style Uses a String-Keyed TreeMap

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/ResolvedStyle.java:138-181`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/manager/StyleManagerImpl.java:87-107`

Every resolved style stores properties in `TreeMap<String, Object>`. Rendering and layout repeatedly
look up properties by string. Recalculation clears and repopulates the tree.

JFR showed `TreeMap.getEntry` at 5.82% of hot-method samples, `String.compareTo` at 3.40%, and
`TreeMap.fixAfterInsertion` at 2.59%.

**Proposed changes:**

- First test `HashMap` or `LinkedHashMap` if deterministic iteration is needed but sorted ordering is not.
- Prefer stable integer property IDs and indexed property slots for hot typed getters.
- Keep extension/custom property storage separate from built-in hot properties.
- Avoid reconstructing entries for unchanged computed properties.

**Expected effect:** Reduce string comparisons, tree traversal, and entry insertion during both render
lookups and style recalculation.

### F9: Text Rendering Clones Fragments and Re-encodes Text Every Frame

Relevant code:

- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgTextRenderer.java:69-103`
- `third_party/SpinyGUI/spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgTextRenderer.java:130-164`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/InlineFragment.java:12-45`

`withColor` rebuilds an entire `InlineFragment` to change only its color. Each resolved text run then
calls `memUTF8` and `memFree` during every render.

`InlineFragment.Builder.build` accounted for 1.24% of sampled allocation. Native text buffer work is
not fully represented by ordinary heap allocation samples.

**Proposed changes:**

- Pass presented color and opacity separately to the text sink instead of cloning fragments.
- Cache encoded UTF-8 data for unchanged rendered runs with explicit lifetime management.
- Alternatively use a reusable frame-local native buffer sized for the largest submitted run.
- Invalidate text buffers only when text, font fallback resolution, or shaping output changes.

**Expected effect:** Reduce heap and native allocation during every visible text render.

### F10: Dirty Layout Rebuilds Temporary Trees and May Run Four Full Passes

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceImpl.java:41-69`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceImpl.java:71-124`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceImpl.java:202-303`

Each dirty layout may execute up to four complete passes while scrollbar gutters settle. Every pass
creates layout contexts, reconstructs wrapper trees, creates multiple linked lists, rewrites layout
child lists, rescans scroll bounds, and clears hidden descendants.

`LayoutServiceImpl` appeared in only 62 sampled execution stacks, so this is lower priority than
per-render allocation for the captured workload. It remains expensive when input or content dirties the
frame.

**Proposed changes:**

- Retain layout-tree nodes and update membership only after structural or positioning changes.
- Track layout-dirty subtrees and affected ancestors.
- Recompute scrollbar bounds only for changed descendants and their scroll containers.
- Clear hidden subtree state only on visibility transitions.
- Reuse layout contexts and temporary buffers where thread confinement permits.
- Preserve the bounded scrollbar convergence rule and test nested scrollbar cases.

**Expected effect:** Lower the cost of the remaining 4 Hz, pointer-driven, resize, and content-driven
layout refreshes.

### F11: ID Lookup Traverses and Allocates Instead of Using an Index

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Frame.java:99-172`

`getElementById` allocates a result list, recursively calls allocating `children()`, and then creates a
stream to return the first item. The `stopAtFirst` flag returns only from the current recursion frame;
the parent's `forEach` continues through sibling branches.

E8.5 mitigates this in Rogue Crawler by caching stable bindings, but the library API remains inefficient.

**Proposed changes:**

- Maintain a frame-owned ID index updated by attachment, detachment, and ID-attribute changes.
- Reject or explicitly define duplicate-ID behavior.
- At minimum, replace the result-list implementation with an early-return depth-first search.

**Expected effect:** Make bindings and dynamic lookups constant-time or allocation-free.

### F12: Child Mutation Performs Linear and Re-entrant Work

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Element.java:153-208`
- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Node.java:87-104`

`addChild` linearly scans existing children for identity. `removeChild` calls `node.parent(null)`, while
`Node.parent` calls back into the old parent's `removeChild`, creating avoidable re-entrant removal
work. Existing sibling unlinking also does not visibly update first/last child fields or clear detached
sibling references in this path.

This was not a dominant stable-frame JFR hotspot, but it affects XML-created list expansion and reorder
correctness.

**Proposed changes:**

- Make parent reassignment a single-owner operation without recursive callbacks.
- Correct first/last and detached sibling bookkeeping atomically.
- Add explicit insert, move-before, and move-after APIs that preserve node identity.
- Avoid whole-list remove/re-add algorithms for reorder.
- Add structural invariant tests after every mutation sequence.

**Expected effect:** Reduce dynamic list churn and eliminate a correctness risk before broader component
composition adoption.

### F13: XML Parsing Is Not a Stable-Path Priority

Relevant code:

- `third_party/SpinyGUI/spinygui.core/src/main/java/com/spinyowl/spinygui/core/parser/impl/DefaultNodeParser.java`

The parser invokes Jsoup and allocates a new mutable node tree, so it is not cheap. E8.5 intentionally
uses it only for new keyed component instances. The recording found one execution sample and no
allocation samples involving `ClientUiFragmentSource.newInstance`.

**Proposed decision:**

- Do not prioritize a compiled template or node-clone system based on this recording.
- Preserve source caching and new-key-only parsing.
- Revisit parser prototypes only if matched recordings with much larger dynamic lists identify initial
  expansion as a user-visible problem.

## Non-SpinyGUI Amplifier: Uncapped Rendering

Rogue Crawler submitted UI at approximately 2,874.4 FPS during the recording. This makes any per-frame
allocation severe even when each individual frame is fast.

**Proposed application/engine changes:**

- Enable VSync or implement a configurable presentation frame cap.
- Keep authoritative simulation, snapshot publication, diagnostics cadence, and rendering clocks
  separate.
- Compare 60 FPS, 120 FPS, VSync, and uncapped recordings using the same UI interaction script.
- Record FPS, allocation/s, allocation/frame, CPU, GC count, pause percentiles, and visible latency.

Frame limiting is not a substitute for fixing SpinyGUI. It is the fastest way to stop multiplying the
current per-render waste and to obtain realistic optimization baselines.

## Prioritized Change Sequence

### Stage 0: Establish Matched Baselines

- Capture separate collapsed-idle, expanded-idle, pointer-active, and resize recordings.
- Capture uncapped, 120 FPS, and 60 FPS variants.
- Record allocation per second and per frame rather than only total allocation.
- Add a representative large diagnostics frame fixture for engine/SpinyGUI benchmarks.

### Stage 1: Remove Accessor and Geometry Allocation

- Retain read-only list views.
- Add allocation-free internal child traversal.
- Add primitive box/position accessors.
- Cache layout absolute positions and composed transforms.
- Remove per-element NanoVG state-scope allocation.

This stage addresses the largest measured per-render categories with comparatively narrow behavior
changes.

### Stage 2: Remove Regex and Style-Property Overhead

- Cache class tokens.
- Replace whitespace regex normalization.
- Index selector candidates.
- Replace or redesign `ResolvedStyle` property storage.
- Avoid unchanged full-map rebuilds and copies.

### Stage 3: Reduce Text Render Allocation

- Stop cloning `InlineFragment` for presented color.
- Reuse or cache UTF-8 run buffers.
- Verify font fallback, Unicode, opacity, and dynamic text updates.

### Stage 4: Add Incremental Style and Layout

- Introduce explicit dirty reasons and affected roots.
- Recalculate styles only for affected elements/subtrees.
- Relayout affected subtrees and ancestors.
- Retain layout-tree structures across unchanged frames.
- Preserve pointer pseudo-state, scrolling, nested overflow, transforms, and scrollbar convergence.

This stage is higher risk and should follow the lower-risk allocation removals and focused tests.

### Stage 5: Harden Lookup and Mutation APIs

- Add ID indexing.
- Correct parent/sibling bookkeeping.
- Add identity-preserving move APIs.
- Prove structural invariants and listener/focus retention.

## Verification Strategy

### Automated Correctness

- Run all SpinyGUI core and NanoVG backend tests.
- Add tests that repeated read-only access returns a stable view and cannot mutate backing collections.
- Add allocation-count or object-identity tests around child traversal, transforms, and fragment color
  submission where deterministic instrumentation is practical.
- Add selector tests for multiple spaces, tabs, empty class attributes, duplicate class tokens, and
  class mutation.
- Add Unicode and whitespace-mode tests before replacing regex normalization.
- Add transform/hit-test equivalence tests for nested transforms, scrolling, singular transforms, and
  resize.
- Add mutation invariants for parent, first/last child, previous/next sibling, detach, move, and reattach.
- Add style/layout invalidation tests for hover, focus, pressed, content, class, inline style, resize,
  scrollbars, and hidden subtrees.

### Performance Evidence

- Use the same Java version, window size, client state, diagnostics rows, and interaction script.
- Warm the application before starting the comparison interval.
- Separate initial expansion from stable rendering.
- Report total allocation, allocation/s, allocation/frame, main-thread CPU, GC count, pause percentiles,
  and sampled hot methods/sites.
- Do not use one timing threshold as the only acceptance check.
- Confirm that optimized paths remain absent or materially reduced in JFR allocation and execution
  samples.

### Rogue Crawler Integration

- Run `gradlew.bat :game:client:test` and `gradlew.bat :game:engine:test` when the included SpinyGUI
  build changes.
- Run `gradlew.bat build` before integrating a completed optimization slice.
- Repeat desktop smoke for collapse/expand, scrolling, resize, direct-connect typing, hover/focus,
  passive-text movement, F3, and shutdown.
- Keep SpinyGUI performance changes out of authoritative core/server/protocol modules.

## Risks and Tradeoffs

- Cached child views must remain read-only and synchronized with structural mutation.
- Cached transforms and positions require precise invalidation for ancestor scroll and animation.
- Indexed selectors must preserve CSS specificity, source order, combinators, and pseudo-state behavior.
- Replacing `TreeMap` can change deterministic iteration if callers depend on sorted property names.
- Cached native text buffers require explicit ownership and cleanup to avoid native leaks.
- Incremental layout can leave stale geometry if dirty propagation is incomplete.
- Render caching can break animation, caret, hover, scroll, and opacity behavior; remove allocation before
  attempting broad paint caching.
- Frame limiting can hide but not remove per-frame inefficiency; always retain allocation/frame metrics.

## Recommended First Implementation Slice

The smallest high-confidence SpinyGUI slice should contain:

1. Retained read-only views for child nodes, inline fragments, and resolved rules.
2. Allocation-free internal element-child traversal.
3. Primitive geometry accessors used by NanoVG render traversal.
4. Removal of allocated NanoVG transform/content state scopes.
5. Focused unit tests and matched 60/120/uncapped JFR recordings.

Do not combine this first slice with selector indexing, `ResolvedStyle` redesign, incremental layout, or
text-buffer caching. Those changes have different correctness risks and should be reviewed separately.

## Deferred Decisions

- Whether frame limiting belongs in `game:engine` configuration or application composition.
- Whether built-in CSS properties move to indexed slots or a faster map as an intermediate step.
- Whether selector indexing should be implemented per stylesheet or as one frame-owned index.
- Whether UTF-8 text buffers are retained per fragment, per resolved run, or in a frame allocator.
- Whether incremental style/layout is upstreamed to SpinyGUI before additional Rogue Crawler UI work.
- Whether these findings become a new E9 follow-up milestone or an upstream SpinyGUI-only roadmap.
