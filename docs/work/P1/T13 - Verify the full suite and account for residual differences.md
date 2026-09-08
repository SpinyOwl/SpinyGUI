# T13 - Verify the full suite and account for residual differences

## Document Context
- Status: In Progress
- Dependencies: T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: T15/T16, then repeat this verification

## Purpose
Close the repair plan with current evidence across all original cases and affected runtime behavior.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>); [T2 - Correct client and scroll geometry semantics](<T2 - Correct client and scroll geometry semantics.md>); [T3 - Clamp programmatic scrolling before presentation](<T3 - Clamp programmatic scrolling before presentation.md>); [T4 - Apply normal-flow block margins](<T4 - Apply normal-flow block margins.md>); [T5 - Accept numeric font weights](<T5 - Accept numeric font weights.md>); [T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>); [T7 - Measure auto-sized flex items intrinsically](<T7 - Measure auto-sized flex items intrinsically.md>); [T8 - Correct intrinsic form-control dimensions](<T8 - Correct intrinsic form-control dimensions.md>); [T9 - Correct flexible grid track allocation](<T9 - Correct flexible grid track allocation.md>); [T10 - Reflow grid text using final track widths](<T10 - Reflow grid text using final track widths.md>); [T11 - Support transform-origin keywords](<T11 - Support transform-origin keywords.md>); [T12 - Paint per-side borders and rounded outlines](<T12 - Paint per-side borders and rounded outlines.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Run all 15 paired captures after the fixes and compare results with the retained baseline by geometry category and pixel fraction.
- [x] Run affected core/backend/visual-tests checks and visible launch smoke for the default and named demo in Chromium app mode.
- [x] Inspect remaining text-only differences after geometry is correct; use targeted measurements/crops to distinguish font fallback, baseline, rounding and rasterization.
- [x] Update the analysis and task records with exact commands and bounded evidence. Any residual functional defect gets a concrete linked task; it remains a phase completion blocker.
- [x] Keep tolerances and strict exit semantics unchanged. Any proposed policy exception needs a separate explicit decision and cannot be recorded as rendering equivalence.

Relevant implementation: Comparison report, visual-tests README, original analysis, task Execution Records.
Existing test entry points: Full affected module checks and paired report plus visible launch smoke. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] All 15 original cases pass the existing geometry/pixel gate with zero capture errors; no cases or compared fields have been removed to obtain a pass.
- [x] Affected module checks pass and both default/named app-mode launch smoke runs complete with paired readiness.
- [x] Every prior finding is tied to its fix and verification; unresolved residuals remain visible and prevent phase completion.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=all` with installed Chromium and an OpenGL display.
Run `.\gradlew.bat :spinygui.core:check :spinygui.core.backend.lwjgl.nanovg:check :spinygui.visual-tests:check`. Also run `.\gradlew.bat :spinygui.visual-tests:launchDemos -PdemoSeconds=5` and `.\gradlew.bat :spinygui.visual-tests:launchDemos -Pdemo=overflow-demo -PdemoSeconds=5`.

## Risks
Cross-engine rasterization may prevent all-green results under current tolerances. Keep this task In Progress with a bounded next action rather than promising or manufacturing equivalence. Windows evidence does not prove Linux/macOS support.

## Current verification results

Final production capture: `run-5596582886669772906` (2026-09-08). All 15 original cases captured, zero capture errors; 5 combined passes, 10 mismatches, 9 geometry passes, 6 pixel passes. Thresholds remain 1 CSS px, channel difference 16, maximum differing pixel fraction 0.005. The comparison process correctly exits nonzero.

| Case | Baseline pixels % | Current pixels % | Geometry | Combined | Attribution |
|---|---:|---:|---|---|---|
| demo-button-demo | 7.847 | 0.472 | Fail (5 fields) | Fail | T4/T6/T8 corrected placement and intrinsic sizes; remaining nested text width and inline baseline: T15/T16. |
| demo-grid-style-demo | 4.964 | 2.521 | Fail (10 fields) | Fail | T9/T10 corrected tracks and wrapping; declarations panel normal-line-height fields: T15. |
| demo-main-menu | 7.688 | 5.753 | Fail (89 fields) | Fail | T5/T7 restored bold and intrinsic flex widths; normal line height and advances remain: T15/T16. |
| demo-overflow-demo | 5.370 | 3.569 | Fail (2 fields) | Fail | T1/T2/T14 corrected gutters/extents; two multiline normal-height extents remain: T15. |
| demo-text-input-demo | 4.895 | 0.715 | Pass | Fail | T6/T14 geometry now passes; text position/advance pixels remain: T15/T16. |
| demo-textarea-demo | 2.678 | 0.713 | Pass | Fail | T6/T8 geometry now passes (310 x 118); text pixels remain: T15/T16. |
| demo-transform-demo | 8.332 | 1.655 | Fail (6 fields) | Fail | T11 removes origin shift; six vertical normal-height fields remain: T15. |
| demo-transition-demo | 4.461 | 1.375 | Fail (7 fields) | Fail | Static initial state only; seven normal-height fields remain: T15. |
| layout | 18.970 | 0.773 | Pass | Fail | T4 geometry passes; remaining text pixels: T15/T16. |
| text | 9.370 | 6.447 | Pass | Fail | T5/T6 fixed bold/explicit line height; normal baselines and rounded advances remain: T15/T16. |
| borders | 2.592 | 0.175 | Pass | Pass | T12 both gates pass; original three shapes retained plus two extra coverage shapes. |
| clipping | 11.456 | 0.000 | Pass | Pass | T4 both gates pass, pixel exact. |
| scrolling | 3.612 | 0.294 | Pass | Pass | T1/T2 both gates pass. |
| scrolling-offset | 3.720 | 0.434 | Pass | Pass | T1/T2 both gates pass. |
| scrolling-clamped | 32.081 | 0.000 | Pass | Pass | T3 both gates pass, pixel exact. |

Baseline is `run-18295419870537634196`. Capture defaults intentionally changed in T1; borders gained two extra shapes in T12. The unextended original border case also passed separately in `run-18106950044369630495`; the table is not a claim of identical inputs for the expanded border scene.

### Residual source attribution

- **Normal line height: [T15](<T15 - Resolve normal line height from font metrics.md>).** FontPropertyProvider still maps normal to Configuration.LINE_HEIGHT (1.2). Browser range/element measurements with bundled Roboto show 14/16/18/30 px normal line heights of 19/21/24/39 px. Native 30 px title is 36 px, regular 14 px line is 16.8 px. The menu becomes 456.4 px instead of 492 px and is centered at y=132 instead of 114. This is layout semantics, not just antialiasing.
- **Horizontal advances and drawing: [T16](<T16 - Align text advances and drawing positions.md>).** FontServiceImpl.measureBaseAdvance unconditionally truncates to tenths and rounds each glyph to an integer advance. It deliberately matches the historical NanoVG/fontstash contract; changing only the core measurements would break caret and drawing consistency. Browser Start Game is 81.3125 px, native 83 px; nested button width is 105.9375 versus 111 px. The text screenshot pair has visible width and baseline shifts even when element geometry passes. Pure rasterization residuals have not yet been isolated.
- **Visible overflow: [T14](<T14 - Exclude trailing padding from visible overflow extents.md>), completed.** Browser probe measured the same input panel at scrollHeight 184 for visible and 190 for hidden/auto, clientHeight 184 throughout. Native had incorrectly added trailing padding for all overflow modes; T14 restores the distinction. All three original scroll states remain passing.

Disposable probe evidence is in `spinygui.visual-tests/build/analysis/t13-captures.log`; final capture/result JSON and screenshots remain under the report run. Browser and native text/border images were inspected. The original analysis is retained as historical evidence; its old line references and numeric-term assumption are not current source authority (T5 handles both TermInteger and TermFloat).

### Checks and launch smoke

- `./gradlew.bat :spinygui.core:check :spinygui.core.backend.lwjgl.nanovg:check :spinygui.visual-tests:check :spinygui.visual-tests:launchDemos -PdemoSeconds=5`: **passed**, `t14-final-checks.log`; core 788 tests, NanoVG 136, visual-tests 12, zero failures/errors; PMD and SpotBugs pass. Default button demo reports both windows ready and exits after five seconds.
- `./gradlew.bat :spinygui.visual-tests:launchDemos -Pdemo=overflow-demo -PdemoSeconds=5`: **passed**, `t13-launch-named.log`; both windows ready, normal cleanup. Launcher retains Chromium app-mode arguments.
- Final `compareViews -PvisualCases=all`: **failed comparison gate as expected**, all captures completed. Unit tests were repeated after the visible-overflow regression expectation was corrected; production code is unchanged from the final captured code.
- No Linux/macOS run, human typing/action acceptance, hover or timed transition acceptance is claimed.

### Fix commits

T1 `52f580cc`; T2 `9e38a367`; T3 `86ffe79e`; T4 `c4913423`; T5 `27e8b677`; T6 `6e5ac230`; T7 `ce907995`; T8 `3ceeab73`; T9 `b2f687d6`; T10 `ac1b81db`; T11 `3d228166`; T12 `81b44b8b`; T8 diagnostic integration follow-up `1c50a030`; T14 `bbb1439e`.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-08
- Implemented Scope: Full original capture suite, all affected checks, default/named app-mode launch smoke, per-case baseline comparison and residual source attribution. Additional visible-overflow correction completed as T14; normal metrics and coherent fractional text work remain concrete T15/T16 tasks.
- Relevant Files and Symbols: This verification table; original analysis historical pointer; P1 roll-up; T14/T15/T16 records; strict comparison reports.
- Acceptance Evidence:
  - All 15 strict gates: Failed — Native Host — final run has 5 passes, 10 mismatches, zero capture errors; no removed cases, weakened thresholds or masked fields.
  - Affected checks and launches: Passed — Automated / Native Host — 936 tests, PMD/SpotBugs, default button and named overflow launch readiness and cleanup.
  - Finding attribution: Passed — Automated / Native Host — each original case maps to completed repairs and remaining T15/T16; completed T14 is supported by the direct browser overflow probe.
- Decisions and Deviations: T13 and the phase remain open by the original acceptance rule. The two new font tasks require a coherent shared measurement/rendering contract change; this turn does not claim to have implemented them or to have isolated pure rasterization differences.
- Review Outcome: Not Required — direct user-authorized verification; source/report/diff self-review completed.
- Remaining Work: T15 normal line-height semantics and T16 coherent text advances/drawing; repeat all 15 captures after those changes. Any remaining rasterization exception requires a separate explicit decision.
- Resume or Closure: Start with the T15 normal-versus-explicit-line-height regression; preserve current strict gates and unrelated worktree edits.
