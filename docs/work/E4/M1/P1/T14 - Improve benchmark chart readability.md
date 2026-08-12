# T14: Improve Benchmark Chart Readability

Parent phase: `docs/work/E4/M1/P1 - Add and run text benchmarks.md`

## Goal

Make all five generated benchmark charts easier to interpret by showing an accessible, JavaScript-independent lower-is-better note directly below each caption and by giving every Chart.js axis an exact name and unit, including the selected history metric.

## Non-Goals

- Do not change chart payloads, datasets, scales, tooltips, chart types, benchmark calculations, archives, fallbacks, raw tables, or report navigation.
- Do not change embedded Chart.js 4.5.1, add external resources, or weaken the one-file offline report.
- Do not redesign responsive behavior or page layout; preserve each chart's local horizontal scrolling.
- Do not update `spinygui.benchmark/README.md` unless its existing report description becomes inaccurate. The approved output does not currently require a README change.

## Context

- `spinygui.benchmark/src/main/jte/report.jte` owns the five chart figures, inline report CSS, visible no-JavaScript fallbacks, and self-contained document structure.
- `spinygui.benchmark/src/main/resources/com/spinyowl/spinygui/benchmark/report/benchmark-charts.js` owns shared bar-chart options and the initial and interactive history-chart configuration.
- Existing trend IDs and units already distinguish `cpu-*` CPU-latency series from `gpu-*` GPU-p99 series; the axis-title change must consume that existing identity without changing the payload.
- `spinygui.benchmark/src/test/java/com/spinyowl/spinygui/benchmark/report/BenchmarkHtmlReportGeneratorTest.java` verifies the generated HTML, embedded scripts, offline guarantees, accessibility hooks, and narrow-layout contracts.

## Dependencies

- **Depends on:** T13 complete.
- **Enables:** None.
- **Parallelizable with:** None. Keep the test, template, and JavaScript edits in one sequential task because they share the generated-HTML contract and report files.

## Required Changes

- [x] Start with strict generated-HTML assertions in `BenchmarkHtmlReportGeneratorTest`: require exactly five complete `<p class="chart-guidance">How to read: Lower values are better.</p>` elements, prove one occurs immediately after each of the five `figcaption` elements, and assert the subdued guidance style. Run the focused test and confirm it fails for the missing guidance rather than fixture or build failure.
- [x] In the same red test pass, assert axis titles in their Chart.js scale-title contexts rather than accepting bare strings that could come from captions, legends, or tables. Cover CPU latency (`Latency (us/op)` / `CPU operation`), CPU allocation (`Allocation (B/op)` / `CPU operation`), CPU rendering (`Latency (us)` / `Rendering scene`), GPU rendering (`Latency (us)` / `Rendering scene`), and history (`Benchmark run` / the selected metric title).
- [x] Add a strict assertion that history initialization selects `CPU latency (us/op)` or `GPU p99 latency (us)` from the existing trend identity and that `activateTrend` assigns the newly selected y-axis title before `historyChart.update()`. Confirm the focused test is red before changing report production files.
- [x] In `report.jte`, add a subdued `.chart-guidance` style and insert the exact visible text `How to read: Lower values are better.` directly after every chart `figcaption`, before the scroll viewport. Keep each note outside the chart-ready fallback-hiding rule so it remains visible when JavaScript is unavailable.
- [x] Extend the shared options in `benchmark-charts.js` to accept x- and y-axis titles and configure both scale title objects with `display:true`, the supplied exact text, and `colors.text`, while preserving all existing scale type/range, ticks, grid, legend, tooltip, and horizontal-bar behavior.
- [x] Pass the approved title pairs to the four overview chart configurations: CPU latency (`Latency (us/op)`, `CPU operation`), CPU allocation (`Allocation (B/op)`, `CPU operation`), CPU rendering (`Latency (us)`, `Rendering scene`), and GPU rendering (`Latency (us)`, `Rendering scene`).
- [x] Initialize the history x-axis title as `Benchmark run` and its y-axis title from the initial CPU/GPU trend, style both title objects with `colors.text`, and update only the y-axis title text together with the existing dataset, range, accessible canvas label, and active-trend state when a metric button is selected.
- [x] Run the focused test again and confirm it turns green without relaxing exact counts, placement, title strings, or update assertions; retain the existing regression checks for five canvases/fallbacks, Chart.js 4.5.1, three embedded scripts, no external assets, raw tables, and local chart scrolling.

## Acceptance Checks

- [x] Generated HTML contains exactly five visible guidance notes with the exact text `How to read: Lower values are better.`, one directly below each chart caption, both before and after Chart.js initializes.
- [x] The four overview charts display the exact approved x/y title pairs in `colors.text` without changing their orientation, scales, datasets, tooltips, budget markers, or clipping behavior.
- [x] The history chart always displays x `Benchmark run`; its initial and selected y title is exactly `CPU latency (us/op)` for CPU trends or `GPU p99 latency (us)` for GPU trends, both titles use `colors.text`, and switching either direction updates the rendered title.
- [x] The report remains one self-contained offline HTML file with embedded Chart.js 4.5.1, accessible canvas/fallback/table paths, visible no-JavaScript guidance, and chart-local rather than page-level narrow-width scrolling.
- [x] Changes remain limited to the three approved implementation/test files; `spinygui.benchmark/README.md` stays untouched while its wording remains accurate, and all unrelated dirty E5, demo CSS, code, draft, worktree, and root-level `work/` content is preserved.

## Verification Strategy

1. Red, then green focused generated-HTML contract:
   ```powershell
   .\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest
   ```
2. Benchmark module tests:
   ```powershell
   .\gradlew.bat :spinygui.benchmark:test
   ```
3. Regenerate the benchmark archive report through the supported task:
   ```powershell
   .\gradlew.bat :spinygui.benchmark:benchmarkReport
   ```
4. Full project tests:
   ```powershell
   .\gradlew.bat test
   ```
5. When the generated report and browser rendering are observable, open `spinygui.benchmark/reports/index.html` at desktop and narrow widths. Inspect all five guidance notes and title pairs, switch between at least one CPU and one GPU history metric in both directions, and verify titles remain readable while overflow stays inside each chart viewport. If native report generation or browser observation is unavailable, record the exact limitation instead of claiming the manual check passed.
6. Review a path-limited diff plus `git status --short` before handoff to prove no root-level `work/`, concurrent E5, demo CSS, or other unrelated dirty content changed.

## Risks and Stop Criteria

- Generated HTML already contains some title text in legends, captions, and tables. Mitigate false-positive tests by asserting scale-title configuration and the dynamic y-title assignment in context, not only `html.contains` on a bare label.
- Axis titles reduce the canvas plot area and may expose clipping at the fixed readable chart width. Preserve the existing frame and local-scroll contract and use the desktop/narrow inspection to catch regressions; do not solve them with an unapproved layout redesign.
- `spinygui.benchmark/README.md` has unrelated concurrent edits. Do not touch it unless the final behavior makes a sentence false; if that occurs, stop and isolate the smallest wording-only patch without overwriting existing changes.
- If implementation appears to require a payload, Chart.js version, scale/range, chart-type, fallback, archive, calculation, or raw-table change, stop rather than expanding this task beyond the approved scope.

## Review Boundary

Implement and review T14 as one small sequential change set. Do not commit, stage, or alter prior task completion markers.
