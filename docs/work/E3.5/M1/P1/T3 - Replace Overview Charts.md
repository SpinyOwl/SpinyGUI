# T3 - Replace Overview Charts

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md), Task 3.
- Next: [T4 - Replace SVG History With One Reusable Chart](T4%20-%20Replace%20SVG%20History%20With%20One%20Reusable%20Chart.md).

## Status

- Depends on: T1 and T2, accepted in the Chart.js report implementation.
- Scope: replace only the four benchmark overview renderers with Chart.js. Do not replace the SVG history chart in this task.

## Requirements

- Follow `../../../../superpowers/specs/2026-07-24-chartjs-benchmark-charts-design.md` and Task 3 in `../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md`.
- Render four accessible canvas chart shells with local horizontal scrolling and visible fallback links to existing precise tables.
- Initialize CPU latency/allocation and rendering CPU/GPU grouped bar charts independently in `benchmark-charts.js`.
- Keep charts offline and self-contained; no external resources or plugins.
- Remove obsolete overview CSS renderer model and `chartRow.jte` only when no longer used.
- Preserve all unrelated worktree changes. Do not commit.

## Verification

- Add or update focused tests for the overview chart shells and generated report output.
- Run `./gradlew.bat :spinygui.benchmark:test --rerun-tasks`.
- Report changed files, test results, known gaps, model used, and fallback status.
