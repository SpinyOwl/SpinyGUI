# T4 - Replace SVG History With One Reusable Chart

## Status

- Depends on: T3 completed in this worktree; re-check accepted overview changes before editing.
- Scope: replace the remaining SVG history chart, radio controls, and panel mechanism with one Chart.js line chart and native buttons.

## Requirements

- Follow Task 4 in `../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md` and the design specification.
- Use `ChartPayload.historyRuns` and `ChartPayload.trends` as the only history chart data source.
- Render native, wrapping `button` controls with correct `aria-pressed`, including a safe empty-history state.
- Update one Chart instance in place, retaining null gaps and aligned tooltip change information.
- Remove SVG-only template/component, CSS, and Java model state after tests cover Chart.js history output.
- Preserve all unrelated worktree changes. Do not commit.

## Verification

- Update focused report tests for the final history markup, payload behavior, and removed SVG implementation.
- Run `./gradlew.bat :spinygui.benchmark:test --rerun-tasks`.
- Report changed files, test results, known gaps, model used, and fallback status.
