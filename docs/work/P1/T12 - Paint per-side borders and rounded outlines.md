# T12 - Paint per-side borders and rounded outlines

## Document Context
- Status: Completed
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
- [x] Use each side's style, width and color rather than borderTop values for the complete perimeter.
- [x] Implement continuous rounded outer/inner border contours for the current solid-border subset, including unequal sides and radius clamping.
- [x] Preserve background radius behavior, opacity, transforms, clipping and inline fragment offsets.
- [x] Add focused fixtures for none/zero-width sides, asymmetric solid borders and rounded uniform/asymmetric outlines.

Relevant implementation: NvgBorderRenderer.java, NvgShapes.java, NvgElementRenderer.java.
Existing test entry points: NvgScrollbarRendererTest, NvgRendererTransformStateTest and new focused border painting tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] borders retains the matching square box, renders the 18 px green rounded outline and the four correctly colored/thick asymmetric sides.
- [x] Zero/top-none cases do not suppress other visible sides, and rounded joins have no gaps or overlaps.
- [x] Backend regression checks and real NanoVG paired captures pass for the targeted border shapes.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=borders` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Do not silently expand this to every CSS border style or elliptical syntax. Preserve existing supported styles and identify additional unsupported styles explicitly.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Individual side widths/styles/presented colors; none/hidden/zero suppression per side; inset rounded uniform strokes; joined outer/inner contours for unequal rounded solid borders; proportional radius clamping. Existing inline offsets and presented opacity remain in use.
- Relevant Files and Symbols: NvgBorderRenderer; BorderContour; NvgBorderRendererTest; fixtures/borders.xml and borders.css.
- Acceptance Evidence:
  - Original shapes: Passed — Native Host — run-18106950044369630495, both geometry and pixel gates pass.
  - Missing/zero/asymmetric rounded sides: Passed — Automated / Native Host — new recording tests cover side visibility, shared contour endpoints, radius clamping, inset radius and opacity. Two additional shapes were added without removing/moving any original shape. run-14538511329276847815 passes both gates: no geometry mismatches and 269 differing pixels (0.175%). Both screenshots visually inspected; contours continuous.
  - Backend checks: Passed — Automated — full :spinygui.core.backend.lwjgl.nanovg:check in t12-backend-check.log, including tests, PMD and SpotBugs. Earlier focused run also covers inline offsets, renderer transforms and scrollbars.
- Decisions and Deviations: Supported solid contours are the scope. Previously non-solid visible styles were painted as solid; that fallback is retained, not represented as new dashed/dotted/double support. Rounded color transitions and rasterization can differ slightly from Chromium, within the unchanged gate. No background radius, transform or clipping policy changes.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff and native screenshot self-review completed.
- Remaining Work: None for the supported solid-border correction.
- Resume or Closure: Continue with T13 full-suite verification.
