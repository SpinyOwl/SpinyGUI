# T8 - Correct intrinsic form-control dimensions

## Document Context
- Status: Completed
- Dependencies: T1, T4, T6
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T9 - Correct flexible grid track allocation](<T9 - Correct flexible grid track allocation.md>)

## Purpose
Align supported button/input/textarea sizing after presentation defaults and line metrics are explicit.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>); [T4 - Apply normal-flow block margins](<T4 - Apply normal-flow block margins.md>); [T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Use a small browser measurement probe to establish intrinsic sizing for current textarea cols/rows and auto-width button cases; retain fixture/font inputs as evidence.
- [x] Replace the alphanumeric-average textarea heuristic with the measured and documented compatible sizing rule, preserving explicit width/height precedence and box-sizing.
- [x] Fix remaining intrinsic button/input width behavior exposed by the existing demos; keep native control APIs and Java actions intact.
- [x] Add regression cases for missing/invalid cols/rows, explicit dimensions, padding/borders and font changes.

Relevant implementation: BlockLayout.getButtonWidth and textarea sizing, InputElement/TextareaElement measurement.
Existing test entry points: BlockLayoutTest, ControlTextLayoutServiceTest and existing control tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] The current 36-column, five-row textarea matches browser intrinsic dimensions under shared styling; old 348.06 vs 310 width and extra 5 px height discrepancies are resolved.
- [x] Auto-sized controls and explicit dimensions follow their respective rules; property changes invalidate measurements.
- [x] Control layout and editing/input regressions pass; visible launcher smoke confirms editable controls still work.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-button-demo,demo-text-input-demo,demo-textarea-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Chromium intrinsic controls have special sizing rules: derive expectations from the pinned runtime and supported contract, not a guessed character-width formula. Resize-handle appearance belongs to T1.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Intrinsic block buttons; fractional primary-font x advance for textarea columns, final ceiling, and invalid rows/cols defaults. Explicit dimensions retain precedence.
- Relevant Files and Symbols: BlockLayout.getButtonWidth/getTextareaWidth/intAttribute; TextMeasurer.averageCharacterWidth; FontServiceImpl.averageCharacterWidth; BlockLayoutTest; FontServiceImplTest.
- Acceptance Evidence:
  - Textarea dimensions: Passed — Native Host — run-8613258406717080086 has textarea geometry gate green, including 310 x 118. Chromium 145 probe with bundled Roboto, hidden overflow, 16 px horizontal padding and 4 px border measured widths for cols 1/2/10/20/36: at 12 px 27/33/81/141/238; at 16 px 29/37/101/181/310; at 20 px 31/41/121/222/383. These match ceil(cols * unrounded x advance) + 20.
  - Auto and explicit sizes: Passed — Automated — BlockLayoutTest covers both display modes, nested button text, default/invalid/changed rows and columns, and explicit dimensions; font test covers 12/16/20 px fractional advances even with pixel rounding enabled.
  - Control regression and launch: Passed — Automated / Native Host — focused BlockLayoutTest, ControlTextLayoutServiceTest, FontServiceImplTest, InputElementTest and TextareaElementTest completed in t8-checks.log; follow-up BlockLayoutTest and five-second app-mode textarea launcher passed in t8-final.log. Both windows reported ready and cleaned up. Human typing was not performed; editing behavior is covered by existing automated controls tests.
- Decisions and Deviations: Column proxy is the measured supported bundled-font contract, not full platform font average-metric emulation. Current input demo explicitly sizes its inputs and needed no intrinsic width change. Textarea geometry now passes; button pixel gate passes, but nested text width/line metrics and panel scrollHeight still fail geometry. Input/textarea text pixels remain above tolerance. These residuals remain for T13; no thresholds changed.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Integration verification: T13 full checks exposed the mandatory TextMeasurer entry-counter inventory omission. Added a distinct average-character-width counter to both the default and native implementation, and accounted for its native horizontal-metrics call. DiagnosticSessionTest now verifies the actual default delegation counters and complete API inventory. Full core/backend/visual-tests check passes in t13-checks-2.log after this follow-up correction.
- Remaining Work: None for this bounded correction; residual typography and scroll extents belong to T13.
- Resume or Closure: Continue with T9.
