# T10 - Reflow grid text using final track widths

## Document Context
- Status: Planned
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
- [ ] Route grid item content reflow through the existing inline formatting path at final dimensions, preserving nested grid and control handling.
- [ ] Invalidate affected text measurements/fragments when a track width changes; avoid repeated whole-frame layout loops.
- [ ] Test the reported clipping independently with a deliberately narrow fixed grid cell as well as corrected fractional tracks.

Relevant implementation: GridLayout.reflowItemContents, LayoutServiceImpl.layoutChildNodes, TextLayoutImpl, InlineFormattingContext.
Existing test entry points: LayoutServiceProviderGridTest, inline formatting tests, TextLayoutFontResolutionTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] Text wraps at final cell width without stale clipping or overlap, including after resizing.
- [ ] Nested grid, inline content, control and overflow cases retain correct positions and hit bounds.
- [ ] Fresh grid demo captures and existing grid/inline regression suites verify the corrected text layout.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-grid-style-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
The original grid-width defect can conceal or amplify reflow defects. Keep an independent narrow-cell regression instead of accepting a wider cell as proof.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - Text wraps at final cell width without stale clipping or overlap, including after resizing.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Nested grid, inline content, control and overflow cases retain correct positions and hit bounds.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Fresh grid demo captures and existing grid/inline regression suites verify the corrected text layout.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Ensure text wraps against the final grid item content box after track sizing.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
