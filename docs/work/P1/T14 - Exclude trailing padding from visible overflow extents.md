# T14 - Exclude trailing padding from visible overflow extents

## Document Context
- Status: Completed
- Dependencies: T2
- Parent: [P1](<../P1 - Repair browser-native view parity.md>)
- Children: None
- Related: [T13](<T13 - Verify the full suite and account for residual differences.md>)
- Next: T13 verification

## Purpose
Correct the remaining scroll extent overstatement found by T13 in visible-overflow panels.

## Prerequisites
T2 is complete. Chromium probe on the current text-input panel measures scrollHeight 184 with visible overflow and 190 with hidden/auto overflow; all three have clientHeight 184.

## Changes
- [x] Add trailing padding only for a clipped scroll container, per axis; retain the client-size floor.
- [x] Cover both axes and overflow changes with a regression independent of fonts.
- [x] Repeat original captures and affected checks.

## Acceptance Checks
- [x] A child entering trailing padding does not enlarge visible overflow beyond the client area; hidden overflow includes the trailing padding.
- [x] The text-input demo panel scrollHeight matches 184; all prior passing scroll cases remain passing.

## Verification
Run OverflowLayoutTest, affected module checks, and all original paired cases without changing tolerances.

## Risks
Do not remove trailing padding from actual scroll containers or replace visible descendant overflow propagation.

## Execution Record
- Status: Completed
- Last Updated: 2026-09-08
- Implemented Scope: Trailing padding expands only clipped scroll-container contents, independently per axis. Client-size floor and visible descendant propagation remain intact.
- Relevant Files and Symbols: LayoutServiceImpl.updateScrollAndClientSize; OverflowLayoutTest.trailingPaddingExpandsOnlyClippedScrollContainers.
- Acceptance Evidence:
  - Visible versus hidden extents: Passed — Automated — both-axis regression measures 94/94 visible versus 108/108 hidden, while client sizes remain 94/94. Updated the prior positioned-child regression from 180 to 170 for visible overflow and retained 180 for hidden.
  - Native demo and scroll cases: Passed — Native Host — run-5596582886669772906: text-input panel now measures 184 and its entire geometry gate passes; all three scrolling states retain both passing gates.
  - Integration: Passed — Automated — core/backend/visual-tests check in t14-final-checks.log, 788/136/12 tests, zero failures/errors, PMD and SpotBugs pass.
- Decisions and Deviations: Browser probe in t13-captures.log confirms the visible=184 versus hidden/auto=190 panel distinction. This is a bounded follow-up to T2 discovered during T13.
- Review Outcome: Not Required — direct user-authorized implementation; source/diff self-review completed.
- Remaining Work: None for visible overflow extents.
- Resume or Closure: Return to T13 residual attribution.
