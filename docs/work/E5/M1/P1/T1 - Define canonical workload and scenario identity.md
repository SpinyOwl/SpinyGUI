# T1: Define Canonical Workload and Scenario Identity

Parent phase: `docs/work/E5/M1/P1 - Approve evidence identity and comparability contracts.md`

## Scope

Inventory the behavior-affecting dimensions of the existing E4 CPU and rendering workloads and
define the versioned canonical semantic identity contract that later E5 CPU, normal-text, input,
textarea, visible, offscreen, and unchanged-submission scenarios must use. Add executable golden
fixtures for identity behavior without adding counters, new scaled workloads, report comparability,
or renderer seams from later nodes.

## Dependency Status

- Depends on: accepted and committed E4/M1/P1 (satisfied by committed E4 task documents and
  benchmark implementation, including commit `6edab9d8`).
- Enables: M1/P1/T2.
- Parallelizable with: none; execute sequentially.

## Required Work

- Inventory every current JMH benchmark operation and all declared behavior-affecting inputs, modes,
  measurement settings, workload content/shape/font choices, and rendering scene dimensions.
- Define a canonical semantic ID and separate display label with deterministic field ordering,
  escaping/canonicalization, and an explicit identity schema version.
- Define the dimension vocabulary needed by the planned normal-text, input, textarea, visible,
  offscreen, and unchanged-submission scenarios without implementing those later workloads.
- Keep all declared behavior-affecting dimensions in identity, including parameter values when E5
  parameterizes a workload.
- Exclude observed glyph, run, fragment, line, command, cull, and other output counts from identity;
  those values remain evidence attached to the same declared-input series.
- Preserve E4 unparameterized history as a distinct legacy namespace/series instead of rewriting or
  merging archived E4 artifacts.
- Add focused golden tests for CPU and each required renderer/control scenario category, including a
  fixture proving changed observed outputs do not change semantic identity.
- Update only the M1/P1/T1 checkboxes that are fully supported by the implementation and verification.

## Scope Limits

- Do not implement M1/P1/T2 fingerprint equality/report delta behavior or M1/P1/T3 run modes,
  pairing, and warmup correction.
- Do not add P2 counters/workloads, P3 renderer seams, or capture any benchmark run.
- Do not edit, merge, renumber, or replace E4 plans or archived E4 report artifacts.
- Preserve the approved uncommitted E5 restructuring and unrelated user changes, especially
  the Chart.js/E3.5/E4 work, benchmark report/template/assets, `docs/drafts/`,
  `docs/work/E6 - Frame pipeline performance.md`,
  `spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/overflow-demo.css`, and
  `.worktrees/`.

## Authorized Retry Corrections

The user explicitly authorized a fresh implementer retry after two reviews rejected the previous
implementation. Preserve and repair that unaccepted work in place:

- Replace integer `measurement-utf16-offset` with finite-decimal pixel
  `measurement-offset-x-px` for `TextMeasurer.measureText(... offsetX ...)`, consistently across
  source constants, schemas, documentation, fixtures, canonicalization, and tests.
- Use explicit operation-specific complete schemas for current and planned P2 workloads. Represent
  exact behavior-affecting inputs such as caret index/offset policy, selection start/end or exact
  span, deferred-suffix length, paragraph count, declared source/visual line count, and offscreen
  ratio/extent. Do not place observed result counts in identity.
- Permit exact finite-decimal zero wrap width for the planned boundary workload while rejecting only
  values outside the approved non-negative domain.
- Include current setup/fixture inputs that can change execution, including JMH
  `@Setup(Level.Trial)`/fixture-preparation policy and renderer inline-layout start Y.
- Give every dimension one schema-driven canonical value domain independent of caller runtime type;
  equivalent numeric/string/enum representations must canonicalize identically.
- Fail closed for omitted required dimensions, extra dimensions, and unknown operation/category
  combinations. Every current and planned scenario must have an authoritative complete schema.
- Extend golden and source/reflection-alignment tests to prove complete current inventory coverage,
  per-input identity sensitivity, equivalent representation equality, and output-only evidence
  exclusion.
- Correct the phase checkbox state: leave a box checked only when the complete corrected diff and
  required verification support it.

## Authorized Fresh Correction: Complete Source Alignment

The user explicitly authorized a new implementer and reviewer after the previous fresh attempt still
allowed source-to-identity checks to false-pass. Do not weaken acceptance criteria.

- Before review, uncheck every T1 box not yet supported; re-check it only after the complete diff and
  verification prove it.
- Prefer declarative CPU operation specifications and renderer scene/setup specifications consumed by
  both benchmark execution and identity construction/alignment, so behavior-affecting runtime values
  are not duplicated among benchmark methods, fixtures, and tests. Keep JMH annotations and Gradle
  task settings separately source-aligned where they cannot consume those descriptors.
- Bind direct-font CPU identities to `Font.DEFAULT` and every actual behavior-affecting invocation
  argument: content, ordered font chain, font size, line height, measurement offset X, maximum width,
  and wrapping policy as applicable. Add negative drift tests.
- Bind inline-layout identity to all effective setup/style inputs: display, position, ordered family,
  style, weight, effective stretch/default treatment, font size, line height, color, white-space,
  text alignment, overflow-wrap, word-break, tab size, container dimensions, text-node count, content,
  and layout start Y. Add negative drift tests.
- Bind rendering identity to the exact ordered `Font` objects used by prewarm and scene layout plus
  all companion-scene/order assumptions, without treating observed glyph/run counts as identity.
  Add negative drift tests.
- Isolate source alignment for the `jmhCpu` task. A native-access value found only in `jmhRendering`
  or another script section must fail the CPU check; include a negative fixture/test proving this.
- Preserve every previously corrected identity behavior: finite-decimal measurement offset X,
  operation-specific complete schemas, zero wrap width, setup/fixture dimensions, schema-driven
  canonicalization, fail-closed validation, output-only evidence exclusion, renderer pair/order and
  native-access identity, and exact corpus declarations.
- Review and test the complete T1 diff, not only the final correction patch.

## Authorized Narrow Correction: Warmups, JMH, and Golden IDs

The user explicitly authorized another fresh implementer and reviewer to close only the remaining
alignment blockers and checkbox truthfulness:

- Bind every trial font warmup to the exact complete CPU operation specification it executes, not
  merely an operation name/order. Validate all warmup execution fields: corpus/version/repeat shape,
  exact ordered `Font` identities and paths/traits, font size, line height, measurement offset X,
  maximum width, wrapping mode/policy, round-to-pixel/resolver/configuration, and operation/API.
  Same-name warmup drift in any field must fail. Derived caret-end width remains output; its declared
  policy and inputs remain identity.
- Reflect and inventory the complete supported class- and method-level JMH annotation contract:
  `@BenchmarkMode`, `@OutputTimeUnit`, `@State`, `@Threads`, `@Fork`, `@Warmup`, and `@Measurement`,
  including iteration/time/time-unit/batch fields and overrides. Reject any unsupported annotation
  shape or map it into the operation identity. Add negative tests for each listed annotation family,
  while keeping `jmhCpu` profiler/native-access/task settings separately and specifically aligned.
- Make every fixture `expectedSemanticId` a literal independently reviewed golden value. Tests must
  never compute, replace, or rewrite it before comparison. If a regeneration utility is useful, keep
  it explicit and outside tests. Add a test showing a stale/altered golden ID fails, and retain the
  fixed-ID proof that output-only evidence changes neither semantic nor series identity.
- Reset unsupported inventory, canonical, no-collision, or fixture boxes before correction and
  re-check only those supported by the final complete implementation and fresh review. T2-owned
  fingerprint-inclusive boxes remain unchecked.
- Preserve and review all prior accepted-in-principle T1 schema/source-alignment behavior; this
  narrow retry must not regress it.

## Acceptance Checks

- Two cases that can execute different behavior cannot share a semantic ID, while presentation-only
  labels do not define identity.
- Golden fixtures cover CPU, normal text, input, textarea, visible, offscreen, and
  unchanged-submission scenarios.
- A fixture changes observed output counts with fixed declared inputs and proves semantic ID and
  series identity remain unchanged.
- Existing E4 entries remain addressable as legacy unparameterized history and cannot collide with
  E5 parameterized identities.
- The benchmark module remains buildable and existing report behavior is not regressed.

## References

- `AGENTS_CODE_STYLE.md`
- `docs/work/E5 - Text performance improvements.md`
- `docs/work/E5/M1 - Repair evidence and comparability.md`
- `docs/work/E5/M1/P1 - Approve evidence identity and comparability contracts.md`
- `docs/work/E4/M1/P1 - Add and run text benchmarks.md`
- `docs/work/E4/M1/P1/T1 - Add benchmark infrastructure.md`
- `docs/work/E4/M1/P1/T2 - Add text calculation benchmarks.md`
- `docs/work/E4/M1/P1/T3 - Add NanoVG rendering benchmark.md`
- `spinygui.benchmark/build.gradle.kts`
- `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/TextWorkloads.java`
- `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/cpu/TextCalculationBenchmark.java`
- `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/rendering/RenderingBenchmarkMain.java`
- `spinygui.benchmark/src/main/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGenerator.java`
- `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/TextWorkloadsTest.java`
- `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java`

## Verification

- `./gradlew :spinygui.benchmark:test --tests 'com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest'`
- `./gradlew :spinygui.benchmark:test --tests 'com.spinyowl.spinygui.benchmark.TextWorkloadsTest'`
- Run any additional focused identity-contract test class added by this task.
- `git diff --check`
- Do not run JMH or `benchmarkReport` in this contract task.

## Handoff Requirements

Return the node ID, files changed, behavior changed, tests run and not run, exact checkbox updates,
and risks/blockers. Do not stage or commit any changes.
