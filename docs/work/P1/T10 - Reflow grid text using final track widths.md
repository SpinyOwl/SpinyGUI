# T10 - Reflow grid text using final track widths

## Document Context
- Status: Completed
- Dependencies: T6, T9
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T11 - Support transform-origin keywords](<T11 - Support transform-origin keywords.md>)

## Purpose
Ensure text wraps against the final grid item content box after track sizing.

## Prerequisites
[T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>); [T9 - Correct flexible grid track allocation](<T9 - Correct flexible grid track allocation.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Route grid item content reflow through the existing inline formatting path at final dimensions, preserving nested grid and control handling.
- [x] Invalidate affected text measurements/fragments when a track width changes; avoid repeated whole-frame layout loops.
- [x] Test the reported clipping independently with a deliberately narrow fixed grid cell as well as corrected fractional tracks.

Relevant implementation: GridLayout.reflowItemContents, LayoutServiceImpl.layoutChildNodes, TextLayoutImpl, InlineFormattingContext.
Existing test entry points: LayoutServiceProviderGridTest, inline formatting tests, TextLayoutFontResolutionTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] Text wraps at final cell width without stale clipping or overlap, including after resizing.
- [x] Nested grid, inline content, control and overflow cases retain correct positions and hit bounds.
- [x] Fresh grid demo captures and existing grid/inline regression suites verify the corrected text layout.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-grid-style-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
The original grid-width defect can conceal or amplify reflow defects. Keep an independent narrow-cell regression instead of accepting a wider cell as proof.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Ordinary grid cells reflow through BlockLayout's inline formatting path at final content dimensions. Nested grid and existing flex dispatch remain separate. Reflow rebuilds inline fragments directly without a whole-frame retry loop.
- Relevant Files and Symbols: GridLayout.reflowItemContents; BlockLayout.layoutFlowChildren; LayoutServiceProviderGridTest.gridReflowsInlineFragmentsAtFinalWidthAndAfterResize.
- Acceptance Evidence:
  - Final width and resize: Passed — Automated — real-font narrow fixed 80 px cell (70 px content) wraps into at least four lines, fragments remain within padding bounds; widening to 200 px rebuilds fewer lines within 190 px content.
  - Existing behaviors: Passed — Automated — LayoutServiceProviderGridTest, InlineFormatting*Test, TextLayoutFontResolutionTest and ControlTextLayoutServiceTest in t10-checks.log; refined resize assertion rerun passed in t10-final.log.
  - Native capture: Passed — Native Host — run-6391224980420900562, screenshot inspected: Actions column wraps and all card text is visible. Full case remains mismatch (2.52% pixels, 10 normal-line-height/scroll fields in the declarations panel), independently tracked for T13.
- Decisions and Deviations: The wider corrected T9 tracks alone were not used as proof; a deliberately narrow fixed track and subsequent resize independently cover stale fragment replacement. No hit-test, control-editing or matrix contract changes.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None for this reflow correction; remaining normal typography is outside this task.
- Resume or Closure: Continue with T11.
