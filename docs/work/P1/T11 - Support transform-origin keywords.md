# T11 - Support transform-origin keywords

## Document Context
- Status: Planned
- Dependencies: T1
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T12 - Paint per-side borders and rounded outlines](<T12 - Paint per-side borders and rounded outlines.md>)

## Purpose
Resolve CSS origin keywords to the correct axis anchors instead of retaining the center default.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>) must satisfy their acceptance checks before execution.

## Changes
- [ ] Accept supported one/two-value left/right/top/bottom/center forms and mixed keyword/length forms with axis validation.
- [ ] Preserve percentage/length support and reject invalid combinations without corrupting the previous resolved value.
- [ ] Check transformed rendering, bounding geometry and hit-testing share the same resolved origin.

Relevant implementation: TransformPropertyProvider.java, transform resolution and hit-test consumers.
Existing test entry points: TransformStyleManagerTest, TransformTest, NodeUtilitiesTransformHitTest, NvgRendererTransformStateTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] left top on a 220 px box scaled 1.25 no longer introduces the erroneous 27.5 px left shift.
- [ ] Valid keyword orderings, single values, mixed forms and invalid axis pairs have parser/resolution regressions.
- [ ] Transform geometry and hit-testing regressions pass; demo-transform-demo origin-related bounds agree.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-transform-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Origin parsing must not accidentally change matrix composition or default center semantics; third-axis/3D support is outside this task.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - left top on a 220 px box scaled 1.25 no longer introduces the erroneous 27.5 px left shift.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Valid keyword orderings, single values, mixed forms and invalid axis pairs have parser/resolution regressions.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Transform geometry and hit-testing regressions pass; demo-transform-demo origin-related bounds agree.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Resolve CSS origin keywords to the correct axis anchors instead of retaining the center default.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
