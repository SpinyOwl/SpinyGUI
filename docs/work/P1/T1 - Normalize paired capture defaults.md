# T1 - Normalize paired capture defaults

## Document Context
- Status: Completed
- Dependencies: None
- Parent: [P1 - Repair browser-native view parity](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [Original analysis](<../T1 - Browser visual comparison analysis.md>); [Runner documentation](../../../spinygui.visual-tests/README.md)
- Next: [T2 - Correct client and scroll geometry semantics](<T2 - Correct client and scroll geometry semantics.md>)

## Purpose
Make browser and native captures comparable before using pixel deltas to accept renderer fixes.

## Changes
- [x] Record the comparison contract in the visual-tests README: visible classic scrollbars with explicit shared sizes/colors; frame root fixed at viewport origin; explicit span display and form-control presentation in shared CSS. Treat these as harness defaults, not a global change of native element defaults.
- [x] Remove Playwright's headless --hide-scrollbars default for comparison and set deterministic scrollbar styling shared by both engines; retain demo overrides such as 13 px bars.
- [x] Prevent browser first-child margins escaping winframe using a verified browser root adapter; keep nested margin behavior observable. Normalize only UA presentation (appearance, border, alignment, resize), never replace declared layout dimensions, margins, weights, or scroll offsets.
- [x] Record effective viewport/DPR/browser arguments and shared default policy; recapture the 15-case baseline without overwriting earlier runs.

Relevant implementation: CompareViewsMain.java, ViewCase.java, comparison.css, comparison.js; LaunchDemosMain.java for consistency only.
Existing test entry points: RunnerFailureTest, ViewCaseTest; paired scrolling/layout/demo-overflow-demo captures. Extend existing behavioral tests where possible.

## Acceptance Checks
- [x] Both captures expose the same scrollbar policy and client-area reduction for 12 px focused and 13 px demo bars.
- [x] Root origin is stable at (0,0), and explicit child margins remain present in saved input.
- [x] All 15 cases capture with no errors; documented default changes and before/after results remain inspectable.

## Verification
Use Java 25 and the repository wrapper. Begin with relevant test-class filters from the entry points above, using `--tests` on the affected module's `test` task; broaden to affected module checks after behavior is stable.
Run `.\gradlew.bat :spinygui.visual-tests:compareViews -PvisualCases=all` with installed Chromium and an OpenGL display.
Shared unresolved defects may keep a selected case red: acceptance must identify the corrected measurements/regions and separately name residual failing fields. A nonzero comparison exit is not an aggregate pass.

## Risks
Browser root isolation and scrollbar styling can change layout; distinguish intentional normalization from engine repairs. Do not emulate a complete Chromium UA stylesheet.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Shared presentation defaults, visible headless scrollbars, rooted browser Frame adapter and effective launch metadata.
- Relevant Files and Symbols: comparison.css, comparison.js, CompareViewsMain.captureBrowser, visual-tests README.
- Acceptance Evidence:
  - Both captures expose the same scrollbar policy and client-area reduction for 12 px focused and 13 px demo bars.: Verified — Native Host — run-10308263144155971837: 15 mismatches, zero capture errors; both focused scrolling clients 242x182, roots (0,0), styled demo bars visible. visual-tests:check passed.
  - Root origin is stable at (0,0), and explicit child margins remain present in saved input.: Verified — Native Host — run-10308263144155971837: 15 mismatches, zero capture errors; both focused scrolling clients 242x182, roots (0,0), styled demo bars visible. visual-tests:check passed.
  - All 15 cases capture with no errors; documented default changes and before/after results remain inspectable.: Verified — Native Host — run-10308263144155971837: 15 mismatches, zero capture errors; both focused scrolling clients 242x182, roots (0,0), styled demo bars visible. visual-tests:check passed.
- Decisions and Deviations: None
- Review Outcome: Not Required — direct user-authorized implementation; final source/diff self-review completed.
- Remaining Work: None
- Resume or Closure: Closed; continue with T2 native metric semantics.
