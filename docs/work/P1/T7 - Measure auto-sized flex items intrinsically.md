# T7 - Measure auto-sized flex items intrinsically

## Document Context
- Status: Completed
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
- [x] Replace the previous block border-box width as the intrinsic auto-width input in FlexLayout.applyAutoAxisSizes where shrink sizing is required.
- [x] Reuse existing intrinsic measurement helpers and correctly include padding/border/margins and min/max constraints.
- [x] Exercise nested explicitly inline spans and mixed text under the T1 shared display rules; isolate any separate inline-layout defect before expanding this task.

Relevant implementation: FlexLayout.java, IntrinsicFlexLayout.java, InlineFormattingContext.java.
Existing test entry points: FlexLayoutTest, IntrinsicFlexLayoutTest, FlexInlineBlockLayoutTest. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] Main-menu non-stretched labels size to their contents while stretch items still fill the cross axis.
- [x] Row/column, align-self overrides, nested content and min/max constraints have focused regression assertions.
- [x] Existing flex and inline/block tests pass; fresh main-menu/button captures show corrected element bounds.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=demo-main-menu,demo-button-demo` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Do not change the global default display of every Element to inline; preserve native custom-element semantics and explicit CSS overrides.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Reused intrinsic width measurement for auto flex items; inline flex/grid item blockification; inline runs summed; exact stretch/min-max regressions.
- Relevant Files and Symbols: FlexLayout intrinsic helpers; IntrinsicFlexLayout; LayoutServiceImpl.blockifiedItem; BlockLayout; FlexLayoutTest; FlexInlineBlockLayoutTest.
- Acceptance Evidence:
  - Main-menu non-stretched labels size to their contents while stretch items still fill the cross axis.: Verified — Automated — 43 affected flex/inline/overflow tests pass. Native Host run-3557315443711249059: menu labels now content-sized (83px instead of full container); remaining roughly 1-2px font advance and normal-line-height differences are retained for T13, button intrinsic widths for T8.
  - Row/column, align-self overrides, nested content and min/max constraints have focused regression assertions.: Verified — Automated — 43 affected flex/inline/overflow tests pass. Native Host run-3557315443711249059: menu labels now content-sized (83px instead of full container); remaining roughly 1-2px font advance and normal-line-height differences are retained for T13, button intrinsic widths for T8.
  - Existing flex and inline/block tests pass; fresh main-menu/button captures show corrected element bounds.: Verified — Automated — 43 affected flex/inline/overflow tests pass. Native Host run-3557315443711249059: menu labels now content-sized (83px instead of full container); remaining roughly 1-2px font advance and normal-line-height differences are retained for T13, button intrinsic widths for T8.
- Decisions and Deviations: Inline flex/grid items are blockified only during layout/metric computation. Fixed test isolation by installing its semantic font owner; production font ownership unchanged.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T8 form-control intrinsic sizing.
