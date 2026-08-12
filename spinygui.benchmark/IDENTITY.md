# Benchmark Workload and Scenario Identity

This document defines identity schema v1 for E5 text-performance evidence. It inventories the E4
workloads that E5 extends, but it does not rewrite archived E4 JSON. Comparability fingerprints,
report run modes/CPU-rendering artifact pairing, and corrected warmup reporting are defined in
[`COMPARABILITY.md`](COMPARABILITY.md).

## Contract

- The semantic ID is produced by `WorkloadIdentity` and has this form:
  `spinygui-benchmark:v1:<namespace>:<workload>;<dimension>=<value>...`.
- The namespace is `e5` for new declared-input identities and `e4-legacy` for an addressable E4
  unparameterized series. Namespace separation prevents an E4 series from colliding with an E5
  parameterized series.
- E5 dimensions are ordered by their canonical ASCII key, regardless of insertion order. Names are
  lowercase kebab-case. String values are normalized to Unicode NFC and UTF-8 percent-encoded with
  uppercase hexadecimal digits; only RFC 3986 unreserved bytes remain literal.
- Category and operation select one exact schema. The builder rejects every omitted required field,
  every field outside that schema, unknown operation/category combinations, and a workload name that
  does not belong to the selected schema. The schemas cover all nine current JMH operations, the
  current normal-text renderer operation, and every P2 CPU, normal-text, input, and textarea
  operation listed below.
- Each dimension owns one value schema independent of its caller's runtime type. Counts and UTF-16
  indices canonicalize integral numbers and numeric strings; pixel offsets and geometry canonicalize
  finite decimals and strip insignificant zeroes; durations canonicalize to `Duration` text;
  booleans accept only boolean-equivalent values; ordered font chains trim list items without
  reordering them; and enum-like dimensions reject values outside their vocabulary. Equivalent
  number/string, enum/string, duration/string, boolean/string, and font-list representations cannot
  split a semantic series.
- `wrap-width-px` has the approved non-negative finite-decimal domain, including exact zero for the
  direct counter boundary workload. A fixed-width schema requires it; an unbounded schema forbids it.
  Finite-width `wordWrap=true` execution is `word-wrap`; finite-width `wordWrap=false` execution still
  breaks at the next code-point boundary and is therefore `character-wrap`. Only execution with no
  finite maximum width is `unwrapped`. A fixed width paired with `unwrapped` fails closed.
- The display label is presentation-only. It is excluded from `semanticId`, `seriesId`, equality,
  and hash code.
- `seriesId` is the semantic ID in schema v1. A result metric and its observed evidence attach to
  that series; they do not redefine it.
- Every value declared before execution that can select a different operation, input, path, scene,
  control state, or output behavior must be a dimension. Every future JMH `@Param` value must
  therefore be present in the E5 identity.
- Golden corpus declarations contain the exact current source strings and the long/derived rendering
  construction rules. The declarative operation specifications reject a known corpus declaration
  paired with different text; a content change must use a new `workload-content` declaration. The CPU
  fallback chain is likewise ordered identity; reversing its `Font` objects is not equivalent.
- A `font-chain` item identifies an exact `Font` by family, style, stretch, weight, and resource path.
  Renderer items are additionally prefixed with `prewarm=` or `layout=` so the two exact ordered
  chains cannot be confused or silently collapsed. Family-only renderer or CPU values are invalid;
  every current and planned fixture uses this exact structure.
- Observed glyph, resolved-run, fragment, line, command, cull, optional image-comparison, latency,
  allocation, and other output values are deliberately absent from the dimension vocabulary. A
  changed output remains evidence for the same declared-input series. The later T2 comparability
  fingerprint must likewise exclude every observed output; this task does not define its other
  fields or equality behavior.
- `historical-series-key` is legal only in `e4-legacy`. It records the exact old report key needed to
  find an E4 CPU method or rendering scene without claiming that the old artifact carried E5 fields.
  A rendering legacy key may retain the old report's fragment/node/code-point/glyph/run tuple solely
  to address that immutable history; those observed values remain forbidden in every E5 identity.

Changing the meaning of a workload requires a `workload-version` change. An incompatible change to
the serialization or dimension rules requires an identity-schema version change.

## Existing E4 CPU Inventory

All operations are methods on
`com.spinyowl.spinygui.benchmark.cpu.TextCalculationBenchmark`. There are no JMH `@Param` fields.
Common declared settings are average-time mode, microseconds, benchmark-scoped state, one thread,
two forks, zero separate warmup forks, three 500 ms warmup iterations, five 500 ms measurement
iterations, batch size one, the
GC profiler, and `--enable-native-access=ALL-UNNAMED`. Trial setup creates `FontServiceImpl` with
`FontStorageImpl`, `roundToPixel=false`, and the default font-chain resolver, warms the current corpus,
computes the near-end caret offset, and creates/reuses operation fixtures outside measured methods.
The `trial` setup level and exact fixture/font preparation policies are mandatory identity dimensions,
not implied metadata. These common settings are mandatory in every timed JMH CPU identity; the P2
direct counter operation uses the separate contract below and must not claim JMH class, fork,
warmup, iteration, state-scope, profiler, or output-time-unit fields.

JMH supplies the selected benchmark name to trial setup through `BenchmarkParams`; that name selects
the operation from `CpuWorkloadSpecifications.currentOperations()`. All measurement wrappers dispatch
through the selected typed specification, so exchanging a wrapper's old constant is not a possible
alternate path. Each trial font warmup executes one complete canonical `MeasurementSpec`, and trial
setup rejects same-name drift in operation/API, corpus and repeat shape, exact ordered font objects,
font size, line height, measurement offset, maximum width, wrapping behavior, round-to-pixel mode,
fixture policy, or resolver. Identity alignment rejects every JMH `@Param` field and every
method-level JMH configuration override until a schema explicitly models it.

| Operation | Declared workload and behavior-affecting inputs |
| --- | --- |
| `measureLatin` | `LATIN`; exact `Font.DEFAULT`; 16 px; line height 1.2; unwrapped `measureText` |
| `measureWrappedParagraph` | `WRAPPED_PARAGRAPH`; measurement start x offset 0 px; exact `Font.DEFAULT`; 16 px; line height 1.2; fixed width 240 px; word wrapping enabled |
| `measureMixedCjk` | `MIXED_CJK`; Roboto Regular then Noto Sans CJK SC Regular; 16 px; line height 1.2 |
| `measureSupplementaryUnicode` | `SUPPLEMENTARY_UNICODE`; the same fallback chain; 16 px; line height 1.2 |
| `measureMissingGlyphs` | `MISSING_GLYPHS`; the same fallback chain; 16 px; line height 1.2 |
| `measureLongSingleFont` | `LONG_SINGLE_FONT` (`LATIN + " "` repeated 128 times); exact `Font.DEFAULT`; 16 px; line height 1.2 |
| `findCaretNearBeginning` | `LONG_SINGLE_FONT` repeated 128 times; exact `Font.DEFAULT`; 16 px; fixed x-offset policy and exact offset 1 px |
| `findCaretNearEnd` | `LONG_SINGLE_FONT` repeated 128 times; exact `Font.DEFAULT`; 16 px; line height 1.2 during trial preparation; x-offset policy `measured-width-minus-inset` with exact inset 1 px (the observed width is not identity) |
| `layoutTextDenseInlineContent` | Three `WRAPPED_PARAGRAPH` text nodes; `InlineFormattingContext.layout`; parent width 240 px; start y 0; Roboto family resolved to the exact current regular/light/bold face order, normal style/weight/effective stretch, 16 px, line height 1.2, black, normal whitespace/overflow/word-break, left aligned, tab size 4 |

The shared content choices are stable Latin, wrapped paragraph, mixed CJK, supplementary Unicode,
missing-glyph, and long-single-font shapes from `TextWorkloads`.

## E5 Direct Counter CPU Inventory

`measureParameterizedText` is an untimed direct-harness operation, not a JMH benchmark method. Each
scenario creates its exact font service and fixtures at application-run scope, executes the same
scenario once to prewarm native font state, resets diagnostics, records exactly one complete
`measureText` operation, and snapshots immediately afterward. Its identity contains source size,
exact ordered fonts and typography, measurement offset, fixed wrap width/mode, paragraph/source/
visual-line shape, fallback transitions, deferred word-wrap suffix work, and wrapped-line-start
kerning transitions. The zero-width case truthfully declares zero visual lines and zero fallback,
deferred-suffix, and line-start work because production returns before scanning source content.
The finite-width run-assembly cases declare `character-wrap`, not `unwrapped`, because the production
loop still enforces `maxWidth` when `wordWrap` is false.

## Existing E4 Rendering Inventory

`RenderingBenchmarkMain` exercises the normal `Text` path through `NvgRenderer` in a hidden,
non-resizable 1280 x 720 GLFW/OpenGL context with swap interval zero. Rendering layout uses
`FontServiceImpl` with `roundToPixel=true` and the default font-chain resolver. A container at
(20, 20) has a 1240 x 680 content box. Each pre-laid-out scene creates either 100 or 1,000
declared source text nodes and validates that layout produced the same actual number of fragments.
The nodes alternate space-stripped `LATIN` and `MIXED_CJK` content. Styling requests Roboto then
Noto Sans CJK SC with normal weight/style/effective stretch, 16 px, line height 1.2, white, normal
whitespace/overflow/word-break, left aligned, and tab size 4. The exact prewarm chain is Roboto
Regular then Noto Sans CJK SC Regular. The default resolver's exact layout chain is Roboto Regular,
Roboto Light, Roboto Bold, then Noto Sans CJK SC Regular. Inline layout starts at y = 0 px.
Scene creation, one mixed-CJK font warmup before each scene (therefore twice, before renderer
initialization), and the non-resizable hidden-window policy are declared fixture inputs.
`prewarm-workload-content` identifies that exact prewarm corpus separately from scene content; the
current declaration is validated against the exact transformed mixed-CJK source.

The rendering JavaExec also declares `--enable-native-access=ALL-UNNAMED`. The two current scenes
form one order-dependent pair: the small scene declares the large scene as its companion and vice
versa; both declare the same workload/style/layout/geometry companion shape, pair count two, and
their own order index. Pair warmup alternates small then large starting with small. Structural validation
then adds one small-scene exposure, and measurement executes the complete small scene before the
complete large scene. Companion count, pair shape, warmup order, measurement order, and order index
all participate in identity because changing any of them can change warmed renderer/GPU state.

The harness alternates 60 warmup renders (30 small and 30 large), then renders and synchronizes the
small scene once before its production-command structural recording, so pre-measure exposure is 31
small and 30 large. It then
measures 200 frames for each scene. Color/stencil clear and an initial `glFinish` occur before each
timer; CPU submission ends when `NvgRenderer.render` returns and GPU-complete time includes the final
`glFinish`. Schema-v2 reporting records these exact per-scene exposures as specified in
`COMPARABILITY.md`.

Requested scene shape, content alternation, frame and container geometry (including container
position), renderer path, fonts/style/layout, warmup/measured frame counts, pre-measure/validation
sequence, clearing, synchronization, visibility classification, and changed/unchanged submission
state are mandatory declared-input dimensions.
Actual fragment, code-point, glyph, run, command, cull, and pixel counts are observed outputs and
never identify an E5 series, even where an old E4 report displayed them as a scene label. The source
text-node count is a declared scene-construction input and therefore remains in identity.

P2 renderer counter scenarios are isolated: each scenario owns a fresh hidden context,
`NvgRenderer`, font registry, `FontServiceImpl`, and prepared scene. Changed scenarios record one
frame with no predecessor; unchanged scenarios render one exact predecessor frame, reset, then
record one frame. Their staged font identity and font-byte manifest include the prewarm chain and
the complete resolver-reachable layout chain: Roboto Regular, Light, Bold, and Noto Sans CJK SC
Regular. Content, shape, and font manifests remain separate.

## E5 Dimension Vocabulary

The schema-v1 enum in `WorkloadIdentity.Dimension` is authoritative. Its fields cover these groups:

| Group | Dimensions |
| --- | --- |
| Operation | `harness`, `benchmark-class`, `operation`, `api`, `category`, `workload-version` |
| Execution/settings | Timed JMH `benchmark-mode`, `output-time-unit`, `state-scope`, `threads`, measured and warmup `forks`, warmup/measurement iteration, time, and batch settings, `profiler`; direct counter `harness=direct` without those JMH fields; plus CPU/rendering `native-access` |
| Setup | `setup-level`, `fixture-preparation-policy`, `font-fixture-policy` |
| Content/shape | `workload-content`, source code-point/UTF-16 length, repeat count, text-node count, content alternation/transform, paragraph count, declared source/visual line counts, fallback and line-start-kerning transitions, deferred-suffix length |
| Measurement/caret | finite-decimal `measurement-offset-x-px`, wrap width/policy, wrapping policy, exact caret offset/inset policy, caret UTF-16 index, and selection start/end UTF-16 indices |
| Typography/layout | `font-chain`, `font-resolver`, `font-size-px`, `font-style`, `font-stretch`, `font-weight`, `line-height`, `round-to-pixel`, display/position, whitespace, alignment, overflow-wrap, word-break, tab size, and color |
| Scene/path | `renderer-path`, native access, context visibility/resizability, swap interval, scene/frame dimensions, container position/size, inline-layout start y, warmup/measured frames, exact prewarm corpus, pre-measure/validation/clear policy, measurement order/index, synchronization, clip state, `visibility`, and exact offscreen ratio/extent |
| Current renderer pair | companion scene shape and text-node count, pair count, warmup order, and small-then-large measurement history |
| Controls | `control-type` (`none`, `input`, or `textarea`), control dimensions/focus/caret state, exact caret/selection indices, input horizontal scroll, textarea horizontal/vertical scroll, and wrapping policy |
| Reuse | `submission-state` (`changed` or `unchanged`) |

Normal text, input, and textarea are distinct `category` values. Visible and offscreen are distinct
`visibility` values. Unchanged submission is a declared `submission-state`, not an inference from an
observed zero command count.

## Authoritative Operation Schemas

The closed v1 operation set is:

| Category | Operation | Status and operation-specific declared inputs |
| --- | --- | --- |
| `cpu` | The nine current `TextCalculationBenchmark` method names | Current E4 inventory; each method has its own exact API and dimension set, including full-overload x/wrap inputs, repeated-content shape, exact caret policy/value, or inline fixture shape as applicable |
| `cpu` | `measureParameterizedText` | P2 direct scaled/adversarial counter measurement: source size, fallback transitions, offset, non-negative fixed wrap width (including honest zero-width early return), wrap mode, deferred suffix, paragraphs, declared source/visual lines, and line-start kerning transitions; no JMH execution fields |
| `normal-text` | `render-text` | Current pre-laid-out E4 NanoVG pair, including space removal, own/companion text-node counts, pair shape/count, setup/warmup and measurement order, native access, and inline-layout start y |
| `normal-text` | `render-normal-text-scenario` | P2 source/line/fallback shape, text-node count, fresh isolated context/order, native access, visibility, exact offscreen ratio/extent, and changed/unchanged submission |
| `input` | `render-input-scenario` | P2 source length/line shape, fresh isolated context/order/native access, exact caret UTF-16 index, exact selection start/end, control geometry/state/scroll, visibility/offscreen extent, and submission state |
| `textarea` | `render-textarea-scenario` | P2 input fields plus fresh isolated context/order/native access, paragraph/source/visual-line counts, fixed wrap width/offset, deferred suffix, and line-start kerning transitions |

No other category/operation pair is valid. Observed fragment, glyph, run, line, renderer-command,
cull, pixel, latency, and allocation counts are not members of any schema.

Executable golden fixtures live in
`src/test/resources/com/spinyowl/spinygui/benchmark/identity/workload-identities-v1.json` and are
verified by `WorkloadIdentityTest`. The test removes every field from every fixture to prove an
incomplete identity is rejected, compares the CPU schema to every current `@Benchmark` method,
proves every current/planned operation has an executable complete case, exercises per-input identity
sensitivity and equivalent runtime representations, reflects JMH annotations, and checks the exact
corpus strings/derived shapes. Current benchmark execution and identity construction consume the same
CPU operation/trial and renderer style/scene/setup specifications. Negative drift cases cover direct
and fallback fonts, every measurement argument, every inline style/geometry/content field, exact
trial warmups and prepared caret/inline specifications, renderer prewarm corpus and prewarm/layout
fonts, companion shape/order, and corpus declarations. The complete supported class-level JMH
contract is explicit and reflected: `@BenchmarkMode`, `@OutputTimeUnit`, `@State`, `@Threads`,
`@Fork`, `@Warmup`, and `@Measurement`, including fork/warmup-fork and iteration/time/time-unit/batch
fields. Inventory walks the complete class hierarchy and explicitly rejects inherited `@Benchmark`
and `@Setup` methods. Effective operation settings use JMH's closest-annotation order—method first,
then the benchmark class and its superclasses—and inherited class or method overrides cannot
false-pass alignment. Negative fixtures reject each annotation override, inherited benchmark/setup
methods, a changed state scope, and unsupported fork JVM arguments. A comment-aware Kotlin task
parser reads only the actual direct
`args(...)`/`jvmArgs(...)` calls, checks every relevant explicit JMH task setting, and includes a
fixture where comments, descriptions, unused strings, and `jmhRendering` native access cannot satisfy
CPU alignment. Every fixture's `expectedSemanticId` is a directly compared, independently reviewable
literal; tests neither patch nor regenerate golden IDs, and a stale literal is proven to fail.
