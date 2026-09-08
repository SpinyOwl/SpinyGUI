# T9 - Correct flexible grid track allocation

## Document Context
- Status: Planned
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
- [ ] Implement bounded fraction resolution with minimum-constrained tracks frozen and remaining space redistributed to unfrozen tracks.
- [ ] Cover both axes, gaps, fixed tracks, multiple minima, zero free space and container overflow without negative sizes.
- [ ] Reuse E3 grid tests and document any overlap with existing uncompleted E3 intrinsic-sizing work.

Relevant implementation: GridLayout.resolveTracks, flexibleBase/flexibleFactor; existing E3 grid plans.
Existing test entry points: LayoutServiceProviderGridTest, GridStyleManagerTest, GridTrackValueTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] 392 px content with 18 px gap and 2fr minmax(112px,1fr) yields approximately 249.33/124.67 px.
- [ ] Minimum-binding cases redistribute correctly; fixed/gap/overflow cases preserve expected occupied size.
- [ ] Grid style and layout regression tests pass; featured/actions bounds match in a fresh grid demo capture.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-grid-style-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
A complete CSS Grid intrinsic-sizing implementation is broader than this defect. Keep this task to the existing definite-size fractional subset and preserve explicit overflow behavior.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - 392 px content with 18 px gap and 2fr minmax(112px,1fr) yields approximately 249.33/124.67 px.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Minimum-binding cases redistribute correctly; fixed/gap/overflow cases preserve expected occupied size.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Grid style and layout regression tests pass; featured/actions bounds match in a fresh grid demo capture.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Treat minmax minima as constraints during fraction resolution rather than an extra base added to a fractional remainder.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
