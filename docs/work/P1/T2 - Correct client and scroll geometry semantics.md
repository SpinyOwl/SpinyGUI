# T2 - Correct client and scroll geometry semantics

## Document Context
- Status: Planned
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
- [ ] Correct LayoutServiceImpl and ScrollbarGeometry so client dimensions include padding, exclude borders/gutters, and scroll extents cannot shrink below the applicable visible area.
- [ ] Include eligible positioned descendants in scroll-area computation, respecting containing blocks, clipping, transforms and overflow rules; define inline/hidden/root behavior explicitly for the supported subset.
- [ ] Audit clipping, scrollbar thumb geometry and hit-testing consumers to avoid double-counting padding or gutters.
- [ ] Separate border-box, client/scroll-size and scroll-offset differences in reports while preserving existing failure gates.

Relevant implementation: LayoutServiceImpl.java, ScrollbarGeometry.java, Element.java, Geometry.java, ViewComparison.java.
Existing test entry points: OverflowLayoutTest, ScrollbarGeometryTest, NodeUtilitiesOverflowHitTest, ScrollbarInteractionTest, GeometryTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] Empty and padded boxes, absolute overflow descendants, hidden/inline elements and dual-axis gutters have explicit expected metrics.
- [ ] borders has no metric-only failures from empty scroll extents; padded layout/client bounds match the agreed contract.
- [ ] Overflow layout, scrollbar geometry, hit-testing and interaction regression tests pass.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=borders,layout,scrolling,scrolling-offset` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Changing public metrics affects rendering and input consumers. Do not alter content-box layout sizes to make client metrics match.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - Empty and padded boxes, absolute overflow descendants, hidden/inline elements and dual-axis gutters have explicit expected metrics.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - borders has no metric-only failures from empty scroll extents; padded layout/client bounds match the agreed contract.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Overflow layout, scrollbar geometry, hit-testing and interaction regression tests pass.: Not Run — Automated — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Align native metrics with the padding-inclusive contract already documented on Element and improve report attribution.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
