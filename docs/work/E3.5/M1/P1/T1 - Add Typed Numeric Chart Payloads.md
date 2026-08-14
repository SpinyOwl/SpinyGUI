# T1 - Add Typed Numeric Chart Payloads

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md), Task 1.
- Next: [T2 - Pin And Embed Chart.js Assets](T2%20-%20Pin%20And%20Embed%20Chart.js%20Assets.md).

## Status and dependency

- Depends on: none.
- Enables: T2.
- Initial implementation commit: `d58908b2bc43cd6e90b0d3dcd386ae36be9f5f25`.
- Current review state: accepted at `f8eb7687` after correcting the recorded P2/Important regression involving absent optional CPU metrics.

## Sources

- Plan: `../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md`, Task 1.
- Approved design: `../../../../superpowers/specs/2026-07-24-chartjs-benchmark-charts-design.md`.
- Repository style: `../../../../../AGENTS_CODE_STYLE.md`.
- Existing brief: `../../../../../.superpowers/sdd/2026-07-24-chartjs-benchmark-charts/task-1-brief.md`.
- Existing report: `../../../../../.superpowers/sdd/2026-07-24-chartjs-benchmark-charts/task-1-report.md`.
- Review evidence: `../../../../../.superpowers/sdd/2026-07-24-chartjs-benchmark-charts/review-cbe79f79..d58908b2.diff`.
- Ledger ruling: `../../../../../.superpowers/sdd/2026-07-24-chartjs-benchmark-charts/progress.md`.

Read these sources and inspect the current checkout before editing.

## Assigned correction

Preserve reports accepted by the existing parser when optional CPU `scoreError` and `gc.alloc.rate` metrics are absent:

1. Change only `BenchmarkReportView.CpuChartDatum.uncertainty` and `.allocationRate` to nullable `Double`; required payload fields remain primitive `double`.
2. Preserve valid latency and allocation chart rows when optional tooltip metadata is absent.
3. Serialize missing optional tooltip metadata as JSON `null`; Task 3 will display `not reported` in Chart.js tooltips.
4. Add `finiteOrNull(String metric, Double value)`, returning `null` unchanged and otherwise delegating to the existing finite-value validation.
5. Add a focused missing-optional-metrics regression test. Remove `scoreError` and `gc.alloc.rate` from the CPU fixture, load/generate the report, assert both payload accessors are `null`, and assert the raw table still contains `not reported`.
6. Add or retain focused coverage proving non-null optional values must be finite. Required values remain covered by `finite`.

## TDD and verification

- Follow strict red-green TDD: add the regression first, run it, and record the expected failing result before production edits.
- Run the focused generator test and then the benchmark module test suite as prescribed by Task 1:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest --rerun-tasks
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
```

- Inspect `git diff --check` for the task changes.

## Scope and preservation

- Limit production/test changes to Task 1 files and the approved plan update already present in the working tree.
- Do not stage, modify, revert, reformat, or absorb `../../../../../spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/overflow-demo.css` or `../../../../../.worktrees`.
- Do not clean or reset the checkout.
- Do not alter required payload field nullability or broaden parser behavior.

## Commit and handoff

The user explicitly authorized focused task commits. After tests and self-review pass, create one focused correction commit that includes Task 1 production/test changes and the already-approved Task 1 plan ruling if appropriate. Do not include manager-owned `../../../../../work` documents, SDD reports, unrelated files, or `../../../../../.worktrees`.

Return a handoff packet with: node ID, model used, fallback status/reason, RED and GREEN commands/results, files changed, behavior changed, commit SHA/subject, plan checkbox changes, self-review, tests not run, and residual risks/blockers.

## Acceptance checks

- Missing optional metrics no longer unbox `null` or prevent report generation.
- Both optional payload fields are nullable and serialize as null when absent.
- Present non-finite optional metrics are rejected with `Non-finite benchmark chart value`.
- Required chart values remain finite-guarded and primitive.
- Raw table behavior still renders `not reported` for absent optional metrics.
- Focused and module tests pass.
- Commit scope is clean and unrelated work is untouched.

## Implementer handoff evidence

- Implementer/model: reusable `s-implementer-terra`, GPT-5.6-Terra; no fallback.
- RED command: `.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest`
- RED result: expected test-compilation failure after adding the regression because primitive `double` optional payload accessors could not be compared with `null`.
- GREEN focused command: `.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest --rerun-tasks` — passed.
- GREEN module command: `.\gradlew.bat :spinygui.benchmark:test --rerun-tasks` — passed.
- Whitespace verification: `git diff --check` — passed.
- Correction commit: `f8eb7687 Preserve missing optional chart metrics`.
- Plan checkboxes: none changed.
- Tests not run by implementer: full repository suite, deferred to final verification.
- Residual dependency note: T2 must configure Gson with `serializeNulls()` so absent optional tooltip metadata is emitted explicitly as JSON `null`.
