# T2 - Pin And Embed Chart.js Assets

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md), Task 2.
- Next: [T3 - Replace Overview Charts](T3%20-%20Replace%20Overview%20Charts.md).

## Status and dependency

- Depends on: T1, accepted at `f8eb7687` after one correction/review retry.
- Enables: T3.
- Execute sequentially in the current checkout using the reusable T1 implementer.

## Sources

- Plan: `../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md`, Task 2.
- Approved design: `../../../../superpowers/specs/2026-07-24-chartjs-benchmark-charts-design.md`.
- Repository style: `../../../../../AGENTS_CODE_STYLE.md`.
- Parent graph: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Accepted T1 context: [T1 - Add Typed Numeric Chart Payloads](T1%20-%20Add%20Typed%20Numeric%20Chart%20Payloads.md).

Read these sources and re-check all relevant current files before editing. The plan's complete Task 2 steps, exact asset source URLs, hashes, license text, file list, APIs, and commands are binding.

## Assignment

Implement only Task 2: pin and embed Chart.js assets, add the page boundary, serialize safe chart JSON, and embed the trusted scripts in the prescribed order.

Task-specific acceptance emphasis:

1. Vendor exactly Chart.js `4.5.1` UMD with only `sourceMappingURL` removed. Required stripped UTF-8 SHA-256: `84d0e233daba702b8f77d669d8c137cad36d441a10f200b6f2d3ab553bdfcf6b`.
2. Preserve Chart.js and bundled `@kurkle/color` banners and add both complete MIT license texts.
3. Add LF normalization rules before importing hash-pinned `.js`/`.txt` resources.
4. Do not use Node, npm, a package manager, CDN runtime loading, `<script src>`, or stylesheet links.
5. Use Gson HTML-safe JSON and call `serializeNulls()` so T1's absent optional uncertainty/allocation-rate fields are emitted explicitly as JSON `null`.
6. Add a regression assertion that the generated chart data includes null optional metadata when the optional CPU metrics are absent, in addition to the malicious-label script-termination coverage from the plan.
7. Keep benchmark labels only in the application/json payload; never interpolate them into executable JavaScript.
8. The initial report-owned bootstrap only parses the payload and leaves all existing charts unchanged for T3/T4.

## TDD and verification

- Follow strict red-green TDD. Add and run the asset integrity tests before creating resources; add and run embedding/escaping tests before production/template changes. Record each expected RED.
- Run every Task 2 verification command from the plan:

```powershell
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkChartAssetsTest
.\gradlew.bat :spinygui.benchmark:test --tests com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGeneratorTest
.\gradlew.bat :spinygui.benchmark:test --rerun-tasks
.\gradlew.bat :spinygui.benchmark:precompileJte :spinygui.benchmark:jar
```

- Inspect the built benchmark JAR and verify the three resources plus `gg/jte/generated/precompiled/JtereportGenerated.class` are present.
- Run `git diff --check` on the task diff.

## Scope and preservation

- Restrict changes to Task 2's listed files and tests.
- Do not begin overview/history chart replacement from T3/T4.
- Do not stage, modify, revert, reformat, or absorb `../../../../../spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/overflow-demo.css`, `../../../../../.worktrees`, or manager-owned `../../../../../work` documents.
- Do not clean or reset the checkout.

## Commit and handoff

The user explicitly authorized focused task commits. After verification and self-review, create one focused T2 commit. Exclude manager-owned documents and unrelated work.

Return the full handoff packet: node ID, model/fallback, RED and GREEN commands/results, files/behavior changed, commit SHA/subject, plan checkbox changes, JAR inspection result, tests not run, self-review, and residual risks/blockers.

## Acceptance checks

- Asset version, hash, stripped source-map directive, notices, licenses, and LF rules match the plan.
- Generated report is self-contained and contains three trusted inline script blocks in the required order.
- JSON remains HTML-safe and emits missing optional metrics as explicit null fields.
- No runtime resource-loading tags are introduced.
- Focused/module tests, JTE precompilation, JAR build/inspection, and diff check pass.
- Commit scope is clean and unrelated work remains untouched.
