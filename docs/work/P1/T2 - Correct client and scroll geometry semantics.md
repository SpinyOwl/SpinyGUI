# T2 - Correct client and scroll geometry semantics

## Document Context
- Status: Completed
- Dependencies: T1
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T3 - Clamp programmatic scrolling before presentation](<T3 - Clamp programmatic scrolling before presentation.md>)

## Purpose
Align native metrics with the padding-inclusive contract already documented on Element and improve report attribution.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Correct LayoutServiceImpl and ScrollbarGeometry so client dimensions include padding, exclude borders/gutters, and scroll extents cannot shrink below the applicable visible area.
- [x] Include eligible positioned descendants in scroll-area computation, respecting containing blocks, clipping, transforms and overflow rules; define inline/hidden/root behavior explicitly for the supported subset.
- [x] Audit clipping, scrollbar thumb geometry and hit-testing consumers to avoid double-counting padding or gutters.
- [x] Separate border-box, client/scroll-size and scroll-offset differences in reports while preserving existing failure gates.

Relevant implementation: LayoutServiceImpl.java, ScrollbarGeometry.java, Element.java, Geometry.java, ViewComparison.java.
Existing test entry points: OverflowLayoutTest, ScrollbarGeometryTest, NodeUtilitiesOverflowHitTest, ScrollbarInteractionTest, GeometryTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] Empty and padded boxes, absolute overflow descendants, hidden/inline elements and dual-axis gutters have explicit expected metrics.
- [x] borders has no metric-only failures from empty scroll extents; padded layout/client bounds match the agreed contract.
- [x] Overflow layout, scrollbar geometry, hit-testing and interaction regression tests pass.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=borders,layout,scrolling,scrolling-offset` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Changing public metrics affects rendering and input consumers. Do not alter content-box layout sizes to make client metrics match.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Padding-inclusive client metrics, viewport-minimum scroll extents, layout-owned positioned/visible transformed overflow, and report categories; focused verification running.
- Relevant Files and Symbols: LayoutServiceImpl.scrollBounds/updateScrollAndClientSize; ScrollbarGeometry.compute; BlockLayout.copyWithClientSize; OverflowLayoutTest; ViewComparison.
- Acceptance Evidence:
  - Empty and padded boxes, absolute overflow descendants, hidden/inline elements and dual-axis gutters have explicit expected metrics.: Verified — Automated — full core/backend tests passed; visual-tests:check passed; final OverflowLayoutTest passed including inline/positioned/transformed/clipped cases. Paired run-15709059462439904928: scrolling and scrolling-offset pass; borders geometry passes; layout retains only margin offsets.
  - borders has no metric-only failures from empty scroll extents; padded layout/client bounds match the agreed contract.: Verified — Automated — full core/backend tests passed; visual-tests:check passed; final OverflowLayoutTest passed including inline/positioned/transformed/clipped cases. Paired run-15709059462439904928: scrolling and scrolling-offset pass; borders geometry passes; layout retains only margin offsets.
  - Overflow layout, scrollbar geometry, hit-testing and interaction regression tests pass.: Verified — Automated — full core/backend tests passed; visual-tests:check passed; final OverflowLayoutTest passed including inline/positioned/transformed/clipped cases. Paired run-15709059462439904928: scrolling and scrolling-offset pass; borders geometry passes; layout retains only margin offsets.
- Decisions and Deviations: None
- Review Outcome: Not Required — direct user-authorized implementation; final diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T3 clamping.
