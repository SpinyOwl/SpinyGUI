# T3 - Replace Overview Charts

## Status

- Depends on: T1, T2 (implemented in the current worktree; preserve all in-progress changes).
- Scope: replace only the four benchmark overview renderers with Chart.js. Do not replace the SVG history chart in this task.

## Requirements

- Follow `docs/superpowers/specs/2026-07-24-chartjs-benchmark-charts-design.md` and Task 3 in `docs/superpowers/plans/2026-07-24-chartjs-benchmark-charts.md`.
- Render four accessible canvas chart shells with local horizontal scrolling and visible fallback links to existing precise tables.
- Initialize CPU latency/allocation and rendering CPU/GPU grouped bar charts independently in `benchmark-charts.js`.
- Keep charts offline and self-contained; no external resources or plugins.
- Remove obsolete overview CSS renderer model and `chartRow.jte` only when no longer used.
- Preserve all unrelated worktree changes. Do not commit.

## Verification

- Add or update focused tests for the overview chart shells and generated report output.
- Run `./gradlew.bat :spinygui.benchmark:test --rerun-tasks`.
- Report changed files, test results, known gaps, model used, and fallback status.
