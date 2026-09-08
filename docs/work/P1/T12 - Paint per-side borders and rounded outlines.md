# T12 - Paint per-side borders and rounded outlines

## Document Context
- Status: Planned
- Dependencies: T1, T2
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T13 - Verify the full suite and account for residual differences](<T13 - Verify the full suite and account for residual differences.md>)

## Purpose
Make NanoVG paint supported border sides and radii from their own resolved values.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>); [T2 - Correct client and scroll geometry semantics](<T2 - Correct client and scroll geometry semantics.md>) must satisfy their acceptance checks before execution.

## Changes
- [ ] Use each side's style, width and color rather than borderTop values for the complete perimeter.
- [ ] Implement continuous rounded outer/inner border contours for the current solid-border subset, including unequal sides and radius clamping.
- [ ] Preserve background radius behavior, opacity, transforms, clipping and inline fragment offsets.
- [ ] Add focused fixtures for none/zero-width sides, asymmetric solid borders and rounded uniform/asymmetric outlines.

Relevant implementation: NvgBorderRenderer.java, NvgShapes.java, NvgElementRenderer.java.
Existing test entry points: NvgScrollbarRendererTest, NvgRendererTransformStateTest and new focused border painting tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] borders retains the matching square box, renders the 18 px green rounded outline and the four correctly colored/thick asymmetric sides.
- [ ] Zero/top-none cases do not suppress other visible sides, and rounded joins have no gaps or overlaps.
- [ ] Backend regression checks and real NanoVG paired captures pass for the targeted border shapes.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=borders` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Do not silently expand this to every CSS border style or elliptical syntax. Preserve existing supported styles and identify additional unsupported styles explicitly.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - borders retains the matching square box, renders the 18 px green rounded outline and the four correctly colored/thick asymmetric sides.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
  - Zero/top-none cases do not suppress other visible sides, and rounded joins have no gaps or overlaps.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Backend regression checks and real NanoVG paired captures pass for the targeted border shapes.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Make NanoVG paint supported border sides and radii from their own resolved values.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
