# T11 - Support transform-origin keywords

## Document Context
- Status: Completed
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
- [x] Accept supported one/two-value left/right/top/bottom/center forms and mixed keyword/length forms with axis validation.
- [x] Preserve percentage/length support and reject invalid combinations without corrupting the previous resolved value.
- [x] Check transformed rendering, bounding geometry and hit-testing share the same resolved origin.

Relevant implementation: TransformPropertyProvider.java, transform resolution and hit-test consumers.
Existing test entry points: TransformStyleManagerTest, TransformTest, NodeUtilitiesTransformHitTest, NvgRendererTransformStateTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] left top on a 220 px box scaled 1.25 no longer introduces the erroneous 27.5 px left shift.
- [x] Valid keyword orderings, single values, mixed forms and invalid axis pairs have parser/resolution regressions.
- [x] Transform geometry and hit-testing regressions pass; demo-transform-demo origin-related bounds agree.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-transform-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Origin parsing must not accidentally change matrix composition or default center semantics; third-axis/3D support is outside this task.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Parse one/two-value origin keywords with axis validation, keyword pair reversal and mixed lengths. Invalid declarations leave the earlier valid declaration effective.
- Relevant Files and Symbols: TransformPropertyProvider.parseOrigin/originAxis; TransformStyleManagerTest.
- Acceptance Evidence:
  - Left/top scale origin: Passed — Native Host — run-7180520739343119625 removes scale x/width differences (including the original 27.5 px shift); remaining six geometry fields are vertical line-height/scroll extents.
  - Grammar: Passed — Automated — 15 valid forms and six invalid forms through parsed CSS, including reversed keywords, one value, percentages and mixed lengths.
  - Transform and hit testing: Passed — Automated — TransformStyleManagerTest, TransformTest, NodeUtilitiesTransformHitTest and backend NvgRendererTransformStateTest completed in t11-checks.log. Paired capture remains mismatch at 1.66% pixels due residual typography; it is not an aggregate pass.
- Decisions and Deviations: Only keyword pairs reverse order. Third-axis/3D syntax remains outside the supported contract. Matrix composition and hit-test consumers are unchanged.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None for origin keywords.
- Resume or Closure: Continue with T12.
