# T8 - Correct intrinsic form-control dimensions

## Document Context
- Status: Planned
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
- [ ] Use a small browser measurement probe to establish intrinsic sizing for current textarea cols/rows and auto-width button cases; retain fixture/font inputs as evidence.
- [ ] Replace the alphanumeric-average textarea heuristic with the measured and documented compatible sizing rule, preserving explicit width/height precedence and box-sizing.
- [ ] Fix remaining intrinsic button/input width behavior exposed by the existing demos; keep native control APIs and Java actions intact.
- [ ] Add regression cases for missing/invalid cols/rows, explicit dimensions, padding/borders and font changes.

Relevant implementation: BlockLayout.getButtonWidth and textarea sizing, InputElement/TextareaElement measurement.
Existing test entry points: BlockLayoutTest, ControlTextLayoutServiceTest and existing control tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] The current 36-column, five-row textarea matches browser intrinsic dimensions under shared styling; old 348.06 vs 310 width and extra 5 px height discrepancies are resolved.
- [ ] Auto-sized controls and explicit dimensions follow their respective rules; property changes invalidate measurements.
- [ ] Control layout and editing/input regressions pass; visible launcher smoke confirms editable controls still work.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-button-demo,demo-text-input-demo,demo-textarea-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Chromium intrinsic controls have special sizing rules: derive expectations from the pinned runtime and supported contract, not a guessed character-width formula. Resize-handle appearance belongs to T1.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - The current 36-column, five-row textarea matches browser intrinsic dimensions under shared styling; old 348.06 vs 310 width and extra 5 px height discrepancies are resolved.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Auto-sized controls and explicit dimensions follow their respective rules; property changes invalidate measurements.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Control layout and editing/input regressions pass; visible launcher smoke confirms editable controls still work.: Not Run — Automated — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Align supported button/input/textarea sizing after presentation defaults and line metrics are explicit.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
