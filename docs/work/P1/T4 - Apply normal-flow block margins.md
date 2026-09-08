# T4 - Apply normal-flow block margins

## Document Context
- Status: Planned
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
- [ ] Resolve margin lengths into block boxes and account for horizontal margins in auto width; retain separate flex-item and positioned-element rules.
- [ ] Correct sibling flow advancement and parent padding interaction; define and test supported adjacent vertical margin collapsing rather than replacing addition with an unconditional max.
- [ ] Cover positive/negative margins, auto horizontal margins, nested blocks, frame root isolation, flex container outer margins and explicit-height overflow.
- [ ] Add targeted regressions reproducing the 20 px layout, 35 px clipping and 12 px control margins.

Relevant implementation: BlockLayout.java, Box/Edges usage, InlineFormattingContext.java, FlexLayout.java.
Existing test entry points: BlockLayoutTest, FlexLayoutTest, FlexInlineBlockLayoutTest, AbsoluteContainingBlockLayoutTest, OverflowLayoutTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] layout container and clipping inner box receive their declared offsets; the independent positioned box and flex-item gap remain correct.
- [ ] Button/input/transition vertical spacing no longer accumulates missing margins.
- [ ] Block, inline/block, flex, absolute-containing-block and overflow regression tests pass, including negative/auto margin cases.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=layout,clipping,demo-button-demo,demo-text-input-demo,demo-transition-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Margin collapse can extend scope quickly; document the supported rules and track any newly found rule explicitly rather than silently treating all margins as independent.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - layout container and clipping inner box receive their declared offsets; the independent positioned box and flex-item gap remain correct.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Button/input/transition vertical spacing no longer accumulates missing margins.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Block, inline/block, flex, absolute-containing-block and overflow regression tests pass, including negative/auto margin cases.: Not Run — Automated — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Restore declared outer spacing and remove accumulated displacement across demos.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
