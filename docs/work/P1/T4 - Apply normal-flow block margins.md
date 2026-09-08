# T4 - Apply normal-flow block margins

## Document Context
- Status: Completed
- Dependencies: T1
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T5 - Accept numeric font weights](<T5 - Accept numeric font weights.md>)

## Purpose
Restore declared outer spacing and remove accumulated displacement across demos.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Resolve margin lengths into block boxes and account for horizontal margins in auto width; retain separate flex-item and positioned-element rules.
- [x] Correct sibling flow advancement and parent padding interaction; define and test supported adjacent vertical margin collapsing rather than replacing addition with an unconditional max.
- [x] Cover positive/negative margins, auto horizontal margins, nested blocks, frame root isolation, flex container outer margins and explicit-height overflow.
- [x] Add targeted regressions reproducing the 20 px layout, 35 px clipping and 12 px control margins.

Relevant implementation: BlockLayout.java, Box/Edges usage, InlineFormattingContext.java, FlexLayout.java.
Existing test entry points: BlockLayoutTest, FlexLayoutTest, FlexInlineBlockLayoutTest, AbsoluteContainingBlockLayoutTest, OverflowLayoutTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] layout container and clipping inner box receive their declared offsets; the independent positioned box and flex-item gap remain correct.
- [x] Button/input/transition vertical spacing no longer accumulates missing margins.
- [x] Block, inline/block, flex, absolute-containing-block and overflow regression tests pass, including negative/auto margin cases.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=layout,clipping,demo-button-demo,demo-text-input-demo,demo-transition-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Margin collapse can extend scope quickly; document the supported rules and track any newly found rule explicitly rather than silently treating all margins as independent.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Normal-flow margins, adjacent sibling collapse, auto-width subtraction, horizontal auto centering and margin-inclusive child height.
- Relevant Files and Symbols: BlockLayout; LayoutUtils.getChildNodesHeight; BlockLayoutTest.
- Acceptance Evidence:
  - layout container and clipping inner box receive their declared offsets; the independent positioned box and flex-item gap remain correct.: Verified — Automated — 66 affected block/flex/absolute/overflow tests passed. Native Host run-3081878281881833121: clipping pixel-exact; layout/transition geometry passes; residual button widths and line metrics belong to T6/T8.
  - Button/input/transition vertical spacing no longer accumulates missing margins.: Verified — Automated — 66 affected block/flex/absolute/overflow tests passed. Native Host run-3081878281881833121: clipping pixel-exact; layout/transition geometry passes; residual button widths and line metrics belong to T6/T8.
  - Block, inline/block, flex, absolute-containing-block and overflow regression tests pass, including negative/auto margin cases.: Verified — Automated — 66 affected block/flex/absolute/overflow tests passed. Native Host run-3081878281881833121: clipping pixel-exact; layout/transition geometry passes; residual button widths and line metrics belong to T6/T8.
- Decisions and Deviations: Supports adjacent sibling vertical collapse; arbitrary parent-child/empty-chain CSS collapsing is not claimed. Current paired frame root is isolated and relevant demo containers establish padding/borders.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T5 numeric weights.
