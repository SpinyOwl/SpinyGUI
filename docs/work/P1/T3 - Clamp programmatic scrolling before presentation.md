# T3 - Clamp programmatic scrolling before presentation

## Document Context
- Status: Completed
- Dependencies: T2
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T4 - Apply normal-flow block margins](<T4 - Apply normal-flow block margins.md>)

## Purpose
Prevent direct scroll setters leaving content thousands of pixels outside a valid scroll range.

## Prerequisites
[T2 - Correct client and scroll geometry semantics](<T2 - Correct client and scroll geometry semantics.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Define clamping for negative/oversized requests against current metrics and for requests made before initial layout or after content-size changes.
- [x] Update Element/FramePipeline/overflow integration so offsets are valid before rendering and hit-testing, without forcing a full layout for every ordinary scroll.
- [x] Keep NativeCaptureMain using the normal public lifecycle; remove any diagnostic-only need to call invalidateLayout.
- [x] Cover content shrink, both axes and repeated in-range scrolling.

Relevant implementation: Element.java, FramePipeline.java, LayoutServiceImpl.java, OverflowUtils.java, NativeCaptureMain.java.
Existing test entry points: OverflowLayoutTest, OverflowUtilsTest, FramePipeline tests, ScrollbarInteractionTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] A 10000/10000 request and negative requests resolve to valid bounds before presentation; browser/native scrolling-clamped offsets agree under shared gutter policy.
- [x] Content shrink reclamps offsets, including after an already prepared frame.
- [x] In-range scrolling does not cause unnecessary full layout and existing scrolling/input tests pass.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=scrolling,scrolling-offset,scrolling-clamped` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Stale metrics before initial layout require a deferred path; do not clamp against zero and lose an otherwise valid pending request.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Transform preparation clamps pending scroll requests using current layout metrics without forcing layout.
- Relevant Files and Symbols: LayoutServiceImpl.resolvePresentationTransforms; Element scroll API documentation; OverflowLayoutTest.
- Acceptance Evidence:
  - A 10000/10000 request and negative requests resolve to valid bounds before presentation; browser/native scrolling-clamped offsets agree under shared gutter policy.: Verified — Automated — OverflowLayoutTest, FramePipelineTest and OverflowUtilsTest passed. Native Host run-16412159985837625584: all three scrolling cases pass both gates; test covers pre-layout, negative/oversized, in-range no-relayout and shrink.
  - Content shrink reclamps offsets, including after an already prepared frame.: Verified — Automated — OverflowLayoutTest, FramePipelineTest and OverflowUtilsTest passed. Native Host run-16412159985837625584: all three scrolling cases pass both gates; test covers pre-layout, negative/oversized, in-range no-relayout and shrink.
  - In-range scrolling does not cause unnecessary full layout and existing scrolling/input tests pass.: Verified — Automated — OverflowLayoutTest, FramePipelineTest and OverflowUtilsTest passed. Native Host run-16412159985837625584: all three scrolling cases pass both gates; test covers pre-layout, negative/oversized, in-range no-relayout and shrink.
- Decisions and Deviations: None
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T4 block margins.
