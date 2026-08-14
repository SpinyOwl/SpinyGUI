# T7 - Regenerate And Inspect The Current Report

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [T6 - Reconcile The Current Report Plan](T6%20-%20Reconcile%20The%20Current%20Report%20Plan.md), [benchmark README](../../../../../spinygui.benchmark/README.md), [benchmark Gradle build](../../../../../spinygui.benchmark/build.gradle.kts).
- Next: [T8 - Complete Real-Browser Report Verification](T8%20-%20Complete%20Real-Browser%20Report%20Verification.md).

## Purpose

Produce a current report from existing evidence and establish the exact artifact that will receive
real-browser verification without silently running timed benchmarks.

## Prerequisites

- T6 is complete.
- The local archive contains a valid paired history suitable for exercising history interaction.

## Changes

- [x] Run `./gradlew :spinygui.benchmark:generateBenchmarkReport` from the repository root.
- [x] Inspect `reports/index.html` and `reports/report-manifest.json` for five chart canvases when two
  eligible runs exist, five adjacent fallbacks, embedded Chart.js 4.5.1/bootstrap/data, the labelled
  history `<select>`, raw tables, and the selected/current archive identity.
- [x] Confirm there are no external script or stylesheet references, source-map directives, SVG chart
  remnants, obsolete history panels, or radio/button selector mappings.
- [x] Run `./gradlew :spinygui.benchmark:test` and
  `./gradlew :spinygui.benchmark:precompileJte :spinygui.benchmark:jar`, then inspect the JAR for the
  precompiled report template and embedded chart resources.

## Acceptance Checks

- [x] Report-only generation succeeds without executing `jmhCpu` or `jmhRendering`.
- [x] The manifest and HTML select the same current eligible run and retain incomplete/ineligible
  artifacts only as archive-health evidence.
- [x] Static inspection and benchmark module tests pass without being reported as browser-runtime proof.
- [x] No generated archive file is staged or committed.

## Verification Evidence

- `generateBenchmarkReport` dry-run and execution passed without `jmhCpu`, `jmhRendering`,
  `benchmarkReportCpu`, or `benchmarkReportRendering` in the task graph.
- The regenerated report selects `20260812-180405-466967300`, matching the manifest. It contains
  five canvases, five adjacent fallbacks, three embedded scripts, Chart.js 4.5.1, the chart data and
  bootstrap, the labelled history `<select>`, and the CPU, rendering, and history tables.
- The manifest retains two eligible pairs, excludes the incomplete CPU-only
  `20260812-175228-819394700` artifact, and classifies counter diagnostics as separate evidence.
- Static inspection found zero external script tags, external stylesheet tags, source-map directives,
  SVG tags, obsolete trend selector mappings, or radio inputs.
- `precompileJte` and `jar` passed. The JAR contains `JtereportGenerated.class`,
  `chart.umd.min.js`, `benchmark-charts.js`, and `THIRD-PARTY-LICENSES.txt`.
- The initial module run exposed a stale pre-optimization assertion:
  `scaledCpuFixturesExposeCurrentQuadraticGlyphMovementWithoutClocks()` expected `28` moved glyph
  slots but the accepted linear `resolveRuns` implementation reported `0`. The test now verifies
  exact linear behavior for 8/16/32-glyph fixtures: one copy and append per glyph, zero moved slots,
  one glyph-list freeze, and one run freeze.
- The corrected focused diagnostic test passed, followed by the complete benchmark module suite.
  These automated and static results do not substitute for T8 browser-runtime verification.

## Risks

A machine-local archive may be absent, stale, or contain fewer than two eligible pairs. Stop and
record that limitation rather than silently running expensive timed captures outside the approved
verification scope.
