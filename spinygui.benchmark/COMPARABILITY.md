# Benchmark Comparability Fingerprints

This document defines comparability fingerprint schema v2 for E5 benchmark results. Workload and
scenario semantic identity is defined separately in [`IDENTITY.md`](IDENTITY.md). Schema v2 adds the
approved evidence modes, paired-run ownership, and truthful renderer pre-measure metadata.

## Required metadata

Every result that can display a signed delta carries one `comparability` object. JMH entries carry it
beside `primaryMetric`; rendering scenes carry it beside their measured values. The object contains:

| Equality group | Required fields |
| --- | --- |
| Identity | `fingerprintSchemaVersion`, `benchmarkVersion`, `workloadVersion`, `resultSchemaVersion`, `behaviorContractVersion`, `evidenceMode`, and the T1 `semanticId` |
| Immutable workload inputs | SHA-256 hashes of exact workload content, workload shape/construction inputs, and ordered font inputs |
| Environment | Scope, JVM vendor/version, OS name/version/architecture, CPU model when available, and—for rendering—GL vendor, renderer, driver version, and API version |
| Execution settings | A non-empty canonical key/string-value map containing every declared setting that can affect execution |

`evidenceMode` is either `counter-only-diagnostics-enabled` or
`timed-allocation-diagnostics-disabled`; changing it changes the identity fingerprint. `displayLabel`
is required presentation metadata but does not participate in equality. `cpuModel` is
required in both scopes. Discovery is best effort, but an unavailable value must be emitted explicitly
as `unavailable` rather than omitted. GL fields are required for rendering and forbidden for CPU
results because an irrelevant driver change must not split a CPU comparison. The current rendering
producer records the complete `GL_VERSION` string as both the available driver-version evidence and
the GL version because OpenGL exposes no portable independent driver-version query.

The three immutable-input fields are lowercase `sha256:<64 hexadecimal digits>` digests over separate
length-prefixed manifests: `workload-content-v1`, `workload-shape-v1`, and `font-inputs-v1`. They do
not hash an observed glyph, run, fragment, line, command, cull, pixel, timing, allocation, or other
result count. Those observed values remain raw structural/performance evidence attached to the T1
semantic series and can change without changing a fingerprint.

- The content manifest hashes the exact UTF-8 bytes of every executed source/prewarm content value.
  It does not trim or NFC-normalize, so canonically equivalent but byte-distinct content differs.
- The shape manifest contains only explicit immutable construction/shape fields. It excludes content,
  font inputs, execution settings, and the whole semantic ID.
- Shape fields are category-exact: normal-text excludes all control/caret/selection/scroll/wrap state;
  input includes only its consumed control dimensions, caret/selection, and horizontal scroll;
  textarea additionally includes vertical scroll, fixed wrap width, deferred suffix, and line-start
  kerning shape. Unconsumed constructor placeholders do not enter any manifest or fingerprint.
- The font manifest preserves execution order and role and includes each exact descriptor/path plus a
  SHA-256 digest of the bytes actually loaded from that resource. Replacing bytes at an unchanged path
  changes the fingerprint. Typography configuration is derived from the scenario's actual execution
  inputs; CPU measurement fonts do not inherit renderer style defaults.

## Fingerprints and canonical hashing

`ComparabilityMetadata` computes four required equality fingerprints: `identity`, `workload`,
`environment`, and `settings`. It also computes `required`, a convenience digest of those four
fingerprints. Equality requires all four component fingerprints to match; `required` is not a
substitute for retaining human-readable source metadata.

Comparability metadata serialization uses UTF-8, Unicode NFC, canonical lowercase kebab-case field/
setting keys, and ascending key order. Each value is encoded as
`<key>=<UTF-8-byte-length>:<value>\n` after the `spinygui-comparability:v2` header and component name.
Input manifests have their own versioned headers and length-prefix both field names and exact values;
content values deliberately bypass metadata NFC normalization. SHA-256 is emitted as lowercase
hexadecimal with the `sha256:` prefix. Timestamps are not fields and cannot enter equality.

Changing a benchmark harness contract, workload meaning, result schema, or behavior contract uses
the corresponding approved version field. An incompatible canonicalization/field-set change requires
a new `fingerprintSchemaVersion`. A behavior migration must never be inferred from a commit ID.

## Environment and settings relevance

Environment equality contains JVM vendor/version, OS name/version/architecture, and CPU identity when
available. Rendering additionally contains GL vendor, renderer, driver version, and GL API version.
The settings map contains all applicable JMH or rendering execution settings, including benchmark
mode, timing/iteration/fork/thread/profiler configuration, renderer frame/sample configuration, and
other declared settings. Schema v2 fails closed unless the setting keys exactly match one of these
scope-specific schemas:

- CPU: benchmark mode, output time unit, state scope, threads, forks, warmup forks/iterations/time/
  batch size, measurement iterations/time/batch size, profiler, and native access.
- Rendering: clear policy, context visibility, measured frames, pair-total and per-scene alternating
  warmup frames, measurement order/index, warmup order, complete per-scene pre-measure and validation
  exposure counts, validation synchronization, pre-measure sequence, validation policy, swap interval,
  measurement synchronization, window resizability, and native access.
- CPU counter-only: one thread, no timing, native access, one exact-scenario prewarm, one recorded
  operation, and explicit setup/reset/snapshot policies.
- Rendering counter-only: one thread, no timing, hidden non-resizable context, swap interval and clear
  policy, fresh context/renderer/font state per scenario, one font prewarm, zero or one exact
  predecessor frame, one recorded frame, and explicit setup/reset/snapshot policies.

## Evidence modes

`counter-only-diagnostics-enabled` is for untimed structural investigation. Diagnostic hooks may be
enabled and its counters remain useful evidence, but neither its timing nor allocation values may be
selected as a baseline. `timed-allocation-diagnostics-disabled` is the only mode eligible for timing
and allocation baselines. Producers write the mode both into comparability identity and into artifact
run metadata; a counter/timed transition is never presented as a comparable delta.

Counter artifacts retain declared identity inputs and independently attached observed structural
outputs. Construction fails when declared source/line/fallback/deferred/kerning, geometry, selection,
wrap, visibility ratio, or predecessor state disagrees with the source-bound evidence. Observed
glyph/run/fragment/command/cull values are outputs only and remain excluded from semantic IDs,
series IDs, immutable-input manifests, and equality fingerprints.

Counter-artifact schema v2 uses exact category-specific declared and `observed-*` field sets. The
declared map must reproduce the embedded semantic ID exactly. Observed source hashes and shape come
from the executed CPU text or prepared `Text`, `InputElement`, and `TextareaElement` values; visual
line/run/glyph/fallback/deferred/line-start evidence comes from produced metrics and diagnostic
counters; and renderer geometry, control dimensions, focus, caret/selection, scroll, wrap width, and
offscreen placement come from the prepared frame/container/control objects. Predecessor render
execution is incremented only after that render returns. Missing, extra, or category-inapplicable
fields fail closed: in particular, normal-text evidence cannot carry control, selection, scroll, or
wrap observations. `ExpectedShape` remains a declared acceptance fixture and is never serialized as
an observation.

## Paired-run ownership

One `benchmarkReport` invocation reserves one run ID and owns exactly one CPU artifact plus one
rendering artifact. Both artifacts carry strict `benchmarkRun` schema-v1 metadata with that run ID,
their distinct `cpu`/`rendering` roles, `paired-report` eligibility, and the timed diagnostics-disabled
mode. Archive selection requires both roles, equal embedded and filename run IDs, paired eligibility,
timed mode, and valid scope-correct comparability metadata on every CPU result and rendering scene.
Each result's comparability evidence mode must exactly match its artifact run metadata. All results
within each artifact must report one environment and implementation provenance; CPU and rendering
must also agree on their shared JVM, OS, architecture, CPU, and implementation fields (rendering's
GL-only environment fields remain rendering-specific).

The report-owned CPU task precedes the report-owned rendering task even under `--parallel`. Their
dynamic, runtime-reserved archive paths use Gradle's documented non-trackable-task policy, so every
producer and report task executes freshly rather than being silently `UP-TO-DATE`. The report receives
the reserved ID and fails rather than publishing a prior eligible pair if that exact fresh complete pair
was not produced.

Direct `jmhCpu` and `jmhRendering` invocations are standalone investigations. They receive fresh run
IDs but carry `unpaired-investigation`; even if two filenames happen to form a pair, the report cannot
select them as an accepted baseline. A missing half, mismatched run ID, malformed run metadata,
counter mode, or standalone eligibility leaves the raw JSON files untouched and excludes that run
from report comparison.

## Rendering pre-measure metadata

Execution remains 60 alternating pair warmups, starting with small: 30 small and 30 large. Structural
validation then renders and completes the small scene once before production-command recording. Scene
JSON and fingerprint settings therefore report 30 alternating warmups plus one synchronized
validation and 31
total pre-measure exposures for small, versus 30 plus zero and 30 for large. No field claims 60
per-scene warmups. Archive eligibility cross-checks all three scene counts against their comparability
settings, requires raw `measuredFrameCount` to agree with fingerprinted `measured-frames`, and binds
the complete scene array in declared order to the exact current `RenderingWorkloadSpecifications`
small and large semantic IDs and execution profiles. Swapped complete scene objects, rewritten
positional settings, or arbitrary internally consistent values fail closed.

## Producer wiring

The CPU tasks run `CpuBenchmarkMain`, which delegates to JMH and then atomically enriches every completed
JMH JSON entry through its exact `CpuWorkloadSpecifications` operation. The producer emits the T1
semantic ID, immutable input hashes, complete CPU settings, runtime environment, and implementation
metadata beside `primaryMetric`. An unknown operation, missing result path, malformed JMH identity
field, or enrichment failure fails the task rather than publishing a silently comparable result.
Each entry also receives the artifact's identical `benchmarkRun` metadata.

`RenderingBenchmarkMain` builds each scene's metadata directly from the same
`RenderingWorkloadSpecifications` object used to construct and measure that scene. Every serialized
`SceneReport` therefore contains `comparability` beside its observed counts and latency summaries.
The report root also emits `benchmarkRun`; the report-level environment emits `cpuModel` and
`glDriverVersion`. The root structural evidence uses the closed
`structural-validation-report-v1` schema, exact approved boundary-scene order, synchronized small-scene
proof, source-expectation hashes, full command-stream hashes, and validator-success hashes. Invalid,
missing, fabricated, reordered, or unknown structural fields make a rendering artifact ineligible.

Implementation values can be supplied through `spinygui.benchmark.implementationRevision`,
`spinygui.benchmark.buildRevision`, and `spinygui.benchmark.commitRevision`; explicit local fallback
values are emitted when those properties are unavailable. CPU model discovery can be overridden with
`spinygui.benchmark.cpuModel`.

## Non-equality implementation metadata

`implementation.implementationRevision`, `implementation.buildRevision`, and
`implementation.commitRevision` are required traceability fields and are shown for the current and
historical runs. They are deliberately absent from every equality fingerprint and from semantic
series selection. Changing only these values remains comparable and does not create a new workload
series. An actual behavior-contract migration instead changes an approved behavior-contract,
workload, benchmark, or schema version.

## Evolution and missing data

- Missing `comparability` metadata is legacy/unknown, never equal. Raw values remain visible and a
  delta is marked `not comparable: required comparability metadata missing` when raw inputs are
  compared directly, but the artifact cannot enter the accepted baseline timeline.
- Missing required fields, malformed values, and unsupported fingerprint-schema versions are invalid
  metadata. Direct raw rendering retains the result and marks a comparison non-comparable with the
  parse reason; archive baseline selection excludes the complete pair.
- Fingerprint schema v1 and old renderer `warmupFrameCount` artifacts remain raw files but are not
  eligible as E5 timing baselines. A corrected paired run must be recaptured under schema v2.
- Schema, environment, setting, hash, label, and implementation fields have exact JSON primitive
  types. Numbers and booleans are never coerced into required strings, and the fingerprint schema
  version must be a JSON integer.
- Unknown fields inside schema-v2 equality-bearing objects fail closed so a producer cannot add a
  silent equality input. Every setting-map entry participates in the settings fingerprint.
- An explicit `extensions` object may appear at the comparability, environment, or implementation
  level. Its contents are defined as non-equality metadata and ignored by schema v2. Unrelated outer
  result fields are likewise not part of this contract.

## Report behavior

History and trend values from eligible complete pairs retain raw measurements. Ineligible and
incomplete artifacts remain on disk but do not enter the accepted comparison timeline. A signed
percentage is produced only when a prior logical result exists, its semantic series matches, and every
required fingerprint matches. A
required mismatch within that series produces `not comparable: <group>.<field> differs`; a different
valid semantic ID selects a separate series and therefore has no cross-series delta. After exact valid
semantic matches are assigned, one unmatched current and one unmatched prior result may be paired by
CPU method or rendering-scene ordinal only when at least one side has missing/invalid metadata. This
makes both valid-to-legacy and legacy-to-valid transitions explicit without ever cross-comparing
same-method parameterized valid IDs; ambiguous groups remain `not available`. Rendering series use
the T1 semantic ID, never observed fragment/glyph/run/command counts, so an output-only regression
remains in one series. Implementation revision and display-label changes alone neither suppress a
delta nor split a series.
Rendering median/p95/p99 change triplets are also suppressed when any corresponding prior percentile
is zero, avoiding an infinite or partially signed delta.
