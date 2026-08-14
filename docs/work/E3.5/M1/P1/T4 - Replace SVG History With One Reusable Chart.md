# T4 - Replace SVG History With One Reusable Chart

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md), Task 4.
- Next: [T5 - Document And Verify Offline Report](T5%20-%20Document%20And%20Verify%20Offline%20Report.md).

## Status

- Depends on: T3, accepted in the Chart.js report implementation.
- Scope: replace the remaining SVG history chart, radio controls, and panel mechanism with one Chart.js line chart and the current labelled native `<select>`.

## Requirements

- Follow Task 4 in `../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md` and the design specification.
- Use `ChartPayload.historyRuns` and `ChartPayload.trends` as the only history chart data source.
- Render a labelled native `<select>` for keyboard-operable metric selection, including a safe insufficient-history state.
- Update one Chart instance in place, retaining null gaps, the selected y-axis title, aligned tooltip change information, and the selected series in the canvas accessible label.
- Remove SVG-only template/component, CSS, and Java model state after tests cover Chart.js history output.
- Preserve all unrelated worktree changes. Do not commit.

## Verification

- Update focused report tests for the final history markup, payload behavior, and removed SVG implementation.
- Run `./gradlew.bat :spinygui.benchmark:test --rerun-tasks`.
- Report changed files, test results, known gaps, model used, and fallback status.
