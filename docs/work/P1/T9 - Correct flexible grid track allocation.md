# T9 - Correct flexible grid track allocation

## Document Context
- Status: Completed
- Dependencies: T1
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T10 - Reflow grid text using final track widths](<T10 - Reflow grid text using final track widths.md>)

## Purpose
Treat minmax minima as constraints during fraction resolution rather than an extra base added to a fractional remainder.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Implement bounded fraction resolution with minimum-constrained tracks frozen and remaining space redistributed to unfrozen tracks.
- [x] Cover both axes, gaps, fixed tracks, multiple minima, zero free space and container overflow without negative sizes.
- [x] Reuse E3 grid tests and document any overlap with existing uncompleted E3 intrinsic-sizing work.

Relevant implementation: GridLayout.resolveTracks, flexibleBase/flexibleFactor; existing E3 grid plans.
Existing test entry points: LayoutServiceProviderGridTest, GridStyleManagerTest, GridTrackValueTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] 392 px content with 18 px gap and 2fr minmax(112px,1fr) yields approximately 249.33/124.67 px.
- [x] Minimum-binding cases redistribute correctly; fixed/gap/overflow cases preserve expected occupied size.
- [x] Grid style and layout regression tests pass; featured/actions bounds match in a fresh grid demo capture.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-grid-style-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
A complete CSS Grid intrinsic-sizing implementation is broader than this defect. Keep this task to the existing definite-size fractional subset and preserve explicit overflow behavior.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Freeze minimum-bound fractional tracks, then redistribute remaining definite space. Fraction totals below one preserve unallocated space. Fixed tracks and gaps are deducted once; negative remainder never creates negative tracks.
- Relevant Files and Symbols: GridLayout.resolveTracks; LayoutServiceProviderGridTest.
- Acceptance Evidence:
  - 392 px / 18 px gap: Passed — Automated / Native Host — both-axis regression yields 249.333/124.667; run-17215724940105713997 removes featured/actions width and x differences.
  - Minimum/fixed/overflow cases: Passed — Automated — seven parameter sets on both axes cover binding/multiple minima, zero/negative remainder, fixed tracks and fractional totals below one.
  - Grid regression and capture: Passed — Automated — LayoutServiceProviderGridTest, GridStyleManagerTest, GridTrackValueTest, t9-final-2.log, BUILD SUCCESSFUL. Native Host — demo retains 10 typography-related geometry fields and 2.54% differing pixels, addressed next by T10/T13.
- Decisions and Deviations: Updated old E3 tests that encoded additive-minimum sizing, including all dependent action positions. This repairs the definite fractional subset and does not close E3 intrinsic/indefinite sizing milestones. Leading-dot fraction syntax is unsupported by the current parser; regression uses 0.25fr.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None for fractional track allocation.
- Resume or Closure: Continue with T10.
