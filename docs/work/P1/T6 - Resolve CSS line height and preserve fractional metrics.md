# T6 - Resolve CSS line height and preserve fractional metrics

## Document Context
- Status: Completed
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
- [x] Represent or resolve length-valued line-height without conflating pixels with unitless multipliers; audit inheritance and font-size changes.
- [x] Separate line-box advance from glyph ascent/descent so a requested line height may be smaller than glyph ink; preserve fractional metrics where browser geometry requires them.
- [x] Update text/control measurement consumers and cache keys affected by line-height representation.
- [x] Document normal line-height behavior and isolate residual glyph rasterization from line-box geometry.

Relevant implementation: FontPropertyProvider.java, ResolvedStyle lineHeight, FontServiceImpl.measureFontMetrics, control text measurement.
Existing test entry points: FontPropertyProviderTest, FontServiceImplMeasurementContractTest, TextLayoutFontResolutionTest, ControlTextLayoutServiceTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] line-height:24px produces 24 px advances; 16px font with line-height:1.25 produces 20 px advances.
- [x] Pixel and unitless inheritance behave correctly across changed child font sizes, including fractional values and cache invalidation.
- [x] Text, control measurement and five-row textarea height regressions pass.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=text,demo-textarea-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
This touches shared font contracts and may affect E5 performance work; avoid broad font-service rewrites and preserve existing cache/performance contracts.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Positive pixel/integer/fractional line-height resolution, absolute inheritance, fractional line advances and half-leading baselines.
- Relevant Files and Symbols: FontPropertyProvider; ResolvedStyle.lineHeight; FontServiceImpl.measureFontMetrics; property/font measurement regression tests; visual-tests README.
- Acceptance Evidence:
  - line-height:24px produces 24 px advances; 16px font with line-height:1.25 produces 20 px advances.: Verified — Automated — full core and NanoVG test tasks passed after updating baseline assertions to the new contract. Native Host run-5821293692940044070: textarea height matches 118px; only intrinsic width remains in geometry. Text enclosing geometry passes.
  - Pixel and unitless inheritance behave correctly across changed child font sizes, including fractional values and cache invalidation.: Verified — Automated — full core and NanoVG test tasks passed after updating baseline assertions to the new contract. Native Host run-5821293692940044070: textarea height matches 118px; only intrinsic width remains in geometry. Text enclosing geometry passes.
  - Text, control measurement and five-row textarea height regressions pass.: Verified — Automated — full core and NanoVG test tasks passed after updating baseline assertions to the new contract. Native Host run-5821293692940044070: textarea height matches 118px; only intrinsic width remains in geometry. Text enclosing geometry passes.
- Decisions and Deviations: Existing measurement keys already include resolved multiplier and font size. Normal retains configured 1.2 multiplier; font-dependent browser normal and glyph rasterization remain explicit T13 analysis items.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T7 flex intrinsic sizing.
