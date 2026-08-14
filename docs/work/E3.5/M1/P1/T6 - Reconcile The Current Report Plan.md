# T6 - Reconcile The Current Report Plan

## Document Context

- Parent: [P1 - Chart.js Report Implementation](../P1%20-%20Chart.js%20Report%20Implementation.md).
- Children: None.
- Related: [Chart.js benchmark charts source plan](../../../../superpowers/plans/2026-07-24-chartjs-benchmark-charts.md), [approved design](../../../../superpowers/specs/2026-07-24-chartjs-benchmark-charts-design.md), [benchmark README](../../../../../spinygui.benchmark/README.md).
- Next: [T7 - Regenerate And Inspect The Current Report](T7%20-%20Regenerate%20And%20Inspect%20The%20Current%20Report.md).

## Purpose

Make the authoritative E3.5 documents describe the shipped select-based history interaction and
supported report-only Gradle workflow before using them as completion evidence.

## Changes

- [x] Mark implemented T1-T5 source-plan steps consistently with their accepted commits and current
  generated-report tests; retain browser verification as unchecked until it is observed.
- [x] Replace obsolete history-button and `aria-pressed` verification text with the current labelled
  `<select>`, keyboard selection, one-chart update, y-axis-title, tooltip, and accessible-label behavior.
- [x] Replace the old task-exclusion report-regeneration command with
  `./gradlew :spinygui.benchmark:generateBenchmarkReport` and align Windows wrapper examples where
  appropriate.
- [x] Correct remaining stale E1 paths, task review-state text, and phase status summaries without
  rewriting historical implementation evidence.

## Acceptance Checks

- [x] E3.5, M1, P1, T1-T8, and the source plan form a valid navigation chain with no stale E1 parent.
- [x] Plan status distinguishes accepted automated implementation from the still-pending browser gate.
- [x] No production, benchmark schema, report template, or JavaScript file changes are included.
- [x] `git diff --check` passes for the tracked focused documentation changes, and an explicit
  trailing-whitespace scan passes for the untracked T6-T8 Markdown files.

## Verification Evidence

- A path-limited `git diff --check` passed for the tracked source-plan and E3.5 chain changes.
- A separate PowerShell line scan covered T6, T7, and T8 because those task documents are untracked;
  it found no trailing spaces or tabs.
- Every local Markdown link in E3.5, M1, P1, and T1-T8 resolved to an existing file.
- Focused stale-text checks found no stale E1 parent and no remaining normative button-oriented
  expected-result wording. Negative assertions rejecting `data-trend-id` and `aria-pressed`, plus
  T6's historical description of the superseded wording, remain intentionally preserved.

## Risks

Historical task records contain useful rejected-review and correction evidence. Preserve that
evidence while changing only current status and superseded acceptance wording.
