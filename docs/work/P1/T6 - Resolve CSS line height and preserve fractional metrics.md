# T6 - Resolve CSS line height and preserve fractional metrics

## Document Context
- Status: Planned
- Dependencies: T5
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T7 - Measure auto-sized flex items intrinsically](<T7 - Measure auto-sized flex items intrinsically.md>)

## Purpose
Respect explicit pixel and unitless line heights instead of forcing every line to intrinsic rounded font height.

## Prerequisites
[T5 - Accept numeric font weights](<T5 - Accept numeric font weights.md>) must satisfy their acceptance checks before execution.

## Changes
- [ ] Represent or resolve length-valued line-height without conflating pixels with unitless multipliers; audit inheritance and font-size changes.
- [ ] Separate line-box advance from glyph ascent/descent so a requested line height may be smaller than glyph ink; preserve fractional metrics where browser geometry requires them.
- [ ] Update text/control measurement consumers and cache keys affected by line-height representation.
- [ ] Document normal line-height behavior and isolate residual glyph rasterization from line-box geometry.

Relevant implementation: FontPropertyProvider.java, ResolvedStyle lineHeight, FontServiceImpl.measureFontMetrics, control text measurement.
Existing test entry points: FontPropertyProviderTest, FontServiceImplMeasurementContractTest, TextLayoutFontResolutionTest, ControlTextLayoutServiceTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] line-height:24px produces 24 px advances; 16px font with line-height:1.25 produces 20 px advances.
- [ ] Pixel and unitless inheritance behave correctly across changed child font sizes, including fractional values and cache invalidation.
- [ ] Text, control measurement and five-row textarea height regressions pass.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=text,demo-textarea-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
This touches shared font contracts and may affect E5 performance work; avoid broad font-service rewrites and preserve existing cache/performance contracts.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - line-height:24px produces 24 px advances; 16px font with line-height:1.25 produces 20 px advances.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Pixel and unitless inheritance behave correctly across changed child font sizes, including fractional values and cache invalidation.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Text, control measurement and five-row textarea height regressions pass.: Not Run — Automated — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Respect explicit pixel and unitless line heights instead of forcing every line to intrinsic rounded font height.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
