# T5 - Accept numeric font weights

## Document Context
- Status: Planned
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
- [ ] Accept valid numeric CSS weight terms in FontPropertyProvider and preserve keyword support and invalid-value handling.
- [ ] Verify resolution of 400, 700 and 800 through style resolution and FontChainResolver using available bundled faces; document fallback when an exact face is absent.
- [ ] Cover inherited values, overrides and style updates so caches do not retain the previous face.

Relevant implementation: FontPropertyProvider.java, CSS numeric terms, FontChainResolver and font resolution caches.
Existing test entry points: FontPropertyProviderTest, TextLayoutFontResolutionTest, font resolution tests. Extend existing behavioral tests where possible.

## Acceptance Checks
- [ ] The focused 700 sample resolves to a bold face rather than weight 400, and menu numeric weights use the documented face fallback.
- [ ] Keyword/numeric cascade and invalid-value regression tests pass.
- [ ] Fresh text/main-menu captures demonstrate weight changes with identical font resources on both sides.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=text,demo-main-menu` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Numeric acceptance and font-face availability are separate; do not add or substitute font assets merely to match a screenshot.

## Execution Record
- Status: Planned
- Last Updated: 2026-09-08
- Implemented Scope: None
- Relevant Files and Symbols: None
- Acceptance Evidence:
  - The focused 700 sample resolves to a bold face rather than weight 400, and menu numeric weights use the documented face fallback.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Keyword/numeric cascade and invalid-value regression tests pass.: Not Run — Automated — planned focused regressions and paired report described in Verification.
  - Fresh text/main-menu captures demonstrate weight changes with identical font resources on both sides.: Not Run — Native Host — planned focused regressions and paired report described in Verification.
- Decisions and Deviations: None
- Review Outcome: Not Reviewed
- Remaining Work: Make numeric weight declarations select the intended supported font face instead of silently falling back to normal.
- Resume or Closure: Resume by verifying the prerequisite records, then reproducing the stated defect in the named source/test entry points before editing production code.
