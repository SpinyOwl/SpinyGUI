# T5 - Accept numeric font weights

## Document Context
- Status: Completed
- Dependencies: T1
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T6 - Resolve CSS line height and preserve fractional metrics](<T6 - Resolve CSS line height and preserve fractional metrics.md>)

## Purpose
Make numeric weight declarations select the intended supported font face instead of silently falling back to normal.

## Prerequisites
[T1 - Normalize paired capture defaults](<T1 - Normalize paired capture defaults.md>) must satisfy their acceptance checks before execution.

## Changes
- [x] Accept valid numeric CSS weight terms in FontPropertyProvider and preserve keyword support and invalid-value handling.
- [x] Verify resolution of 400, 700 and 800 through style resolution and FontChainResolver using available bundled faces; document fallback when an exact face is absent.
- [x] Cover inherited values, overrides and style updates so caches do not retain the previous face.

Relevant implementation: FontPropertyProvider.java, CSS numeric terms, FontChainResolver and font resolution caches.
Existing test entry points: FontPropertyProviderTest, TextLayoutFontResolutionTest, font resolution tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] The focused 700 sample resolves to a bold face rather than weight 400, and menu numeric weights use the documented face fallback.
- [x] Keyword/numeric cascade and invalid-value regression tests pass.
- [x] Fresh text/main-menu captures demonstrate weight changes with identical font resources on both sides.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=text,demo-main-menu` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Numeric acceptance and font-face availability are separate; do not add or substitute font assets merely to match a screenshot.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Integer and fractional numeric CSS weight validation, static weight mapping and inheritance/update coverage.
- Relevant Files and Symbols: FontPropertyProvider; FontWeight.find(int); FontPropertyProviderTest; FontChainResolverTest.
- Acceptance Evidence:
  - The focused 700 sample resolves to a bold face rather than weight 400, and menu numeric weights use the documented face fallback.: Verified — Automated — 12 property/resolver/layout-font tests pass. Native Host run-5356681403752146316: bold sample renders bold, text enclosing geometry passes; remaining pixels include line-height differences (T6).
  - Keyword/numeric cascade and invalid-value regression tests pass.: Verified — Automated — 12 property/resolver/layout-font tests pass. Native Host run-5356681403752146316: bold sample renders bold, text enclosing geometry passes; remaining pixels include line-height differences (T6).
  - Fresh text/main-menu captures demonstrate weight changes with identical font resources on both sides.: Verified — Automated — 12 property/resolver/layout-font tests pass. Native Host run-5356681403752146316: bold sample renders bold, text enclosing geometry passes; remaining pixels include line-height differences (T6).
- Decisions and Deviations: Existing static weight model rounds numeric 1..1000 requests to supported 100..900 weights; available-face resolver selects nearest bundled face (Roboto 800 uses bold 700), without new font assets.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T6 line-height.
