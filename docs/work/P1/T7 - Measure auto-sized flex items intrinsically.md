# T7 - Measure auto-sized flex items intrinsically

## Document Context
- Status: Planned
- Dependencies: T1, T4, T6
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T8 - Correct intrinsic form-control dimensions](<T8 - Correct intrinsic form-control dimensions.md>)

## Purpose
Stop non-stretched column flex items inheriting a previously computed full block width.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>); [T4 - Apply normal-flow block margins](<T4 - Apply normal-flow block margins.md>); [T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>) must satisfy their acceptance checks before execution.

## Changes
- [ ] Replace the previous block border-box width as the intrinsic auto-width input in FlexLayout.applyAutoAxisSizes where shrink sizing is required.
- [ ] Reuse existing intrinsic measurement helpers and correctly include padding/border/margins and min/max constraints.
- [ ] Exercise nested explicitly inline spans and mixed text under the T1 shared display rules; isolate any separate inline-layout defect before expanding this task.

Relevant implementation: FlexLayout.java, IntrinsicFlexLayout.java, InlineFormattingContext.java.
Existing test entry points: FlexLayoutTest, IntrinsicFlexLayoutTest, FlexInlineBlockLayoutTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] Main-menu non-stretched labels size to their contents while stretch items still fill the cross axis.
- [ ] Row/column, align-self overrides, nested content and min/max constraints have focused regression assertions.
- [ ] Existing flex and inline/block tests pass; fresh main-menu/button captures show corrected element bounds.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-main-menu,demo-button-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Do not change the global default display of every Element to inline; preserve native custom-element semantics and explicit CSS overrides.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - Main-menu non-stretched labels size to their contents while stretch items still fill the cross axis.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Row/column, align-self overrides, nested content and min/max constraints have focused regression assertions.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Existing flex and inline/block tests pass; fresh main-menu/button captures show corrected element bounds.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Stop non-stretched column flex items inheriting a previously computed full block width.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
