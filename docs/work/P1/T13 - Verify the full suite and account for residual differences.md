# T13 - Verify the full suite and account for residual differences

## Document Context
- Status: Planned
- Dependencies: T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: Parent phase closure

## Purpose
Close the repair plan with current evidence across all original cases and affected runtime behavior.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>); [T2 - Correct client and scroll geometry semantics](<T2 - Correct client and scroll geometry semantics.md>); [T3 - Clamp programmatic scrolling before presentation](<T3 - Clamp programmatic scrolling before presentation.md>); [T4 - Apply normal-flow block margins](<T4 - Apply normal-flow block margins.md>); [T5 - Accept numeric font weights](<T5 - Accept numeric font weights.md>); [T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>); [T7 - Measure auto-sized flex items intrinsically](<T7 - Measure auto-sized flex items intrinsically.md>); [T8 - Correct intrinsic form-control dimensions](<T8 - Correct intrinsic form-control dimensions.md>); [T9 - Correct flexible grid track allocation](<T9 - Correct flexible grid track allocation.md>); [T10 - Reflow grid text using final track widths](<T10 - Reflow grid text using final track widths.md>); [T11 - Support transform-origin keywords](<T11 - Support transform-origin keywords.md>); [T12 - Paint per-side borders and rounded outlines](<T12 - Paint per-side borders and rounded outlines.md>) must satisfy their acceptance checks before execution.

## Changes
- [ ] Run all 15 paired captures after the fixes and compare results with the retained baseline by geometry category and pixel fraction.
- [ ] Run affected core/backend/visual-tests checks and visible launch smoke for the default and named demo in Chromium app mode.
- [ ] Inspect remaining text-only differences after geometry is correct; use targeted measurements/crops to distinguish font fallback, baseline, rounding and rasterization.
- [ ] Update the analysis and task records with exact commands and bounded evidence. Any residual functional defect gets a concrete linked task; it remains a phase completion blocker.
- [ ] Keep tolerances and strict exit semantics unchanged. Any proposed policy exception needs a separate explicit decision and cannot be recorded as rendering equivalence.

Relevant implementation: Comparison report, visual-tests README, original analysis, task Execution Records.
Existing test entry points: Full affected module checks and paired report plus visible launch smoke. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] All 15 original cases pass the existing geometry/pixel gate with zero capture errors; no cases or compared fields have been removed to obtain a pass.
- [ ] Affected module checks pass and both default/named app-mode launch smoke runs complete with paired readiness.
- [ ] Every prior finding is tied to its fix and verification; unresolved residuals remain visible and prevent phase completion.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=all` with installed Chromium and an OpenGL display.
Run `.\gradlew.bat :spinygui.core:check :spinygui.core.backend.lwjgl.nanovg:check :spinygui.visual-tests:check`. Also run `.\gradlew.bat :spinygui.visual-tests:launchDemos -PdemoSeconds=5` and `.\gradlew.bat :spinygui.visual-tests:launchDemos -Pdemo=overflow-demo -PdemoSeconds=5`.

## Risks
Cross-engine rasterization may prevent all-green results under current tolerances. Keep this task In Progress with a bounded next action rather than promising or manufacturing equivalence. Windows evidence does not prove Linux/macOS support.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - All 15 original cases pass the existing geometry/pixel gate with zero capture errors; no cases or compared fields have been removed to obtain a pass.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
  - Affected module checks pass and both default/named app-mode launch smoke runs complete with paired readiness.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Every prior finding is tied to its fix and verification; unresolved residuals remain visible and prevent phase completion.: Not Run — Automated — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Close the repair plan with current evidence across all original cases and affected runtime behavior.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
