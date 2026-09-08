# T19 - Size auto flex containers from measured children

## Purpose
Normal text metrics expose a flex sizing defect: a 54 px provisional button minimum was passed to Yoga as definite height even when measured children require 57 px. Sibling spacing and ancestor heights then use stale dimensions.

## Changes
Leave normal-flow auto-height flex roots automatic during Yoga layout, propagate their measured border-box height back to the box and block-flow cursor, and retain definite/frame/absolute sizing.

## Acceptance Checks
- Nested auto columns grow from 54 to 57 px and preserve the 6 px sibling margin.
- Main-menu matches browser vertical geometry after T15 and retains the 10 px player-panel gap.
- Focused flex regressions and core check pass; rerun original visual cases without weaker thresholds.

## Risks
Only normal-flow automatic heights change. Definite height, frame viewport sizing and absolute positioning remain on the established path. Other rasterization and horizontal advance differences remain T16 work.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-08
- Implemented Scope: FlexLayout leaves auto root height unconstrained and publishes Yoga's measured height before the containing layout places siblings.
- Relevant Files and Symbols: FlexLayout.layout; IntrinsicFlexLayoutTest.nestedAutoColumnsGrowBeyondTheirMinimumBeforePlacingSiblings.
- Acceptance Evidence:
  - Nested sizing: Passed — Automated — focused flex tests, including 57 px height, 6 px margin and 126 px two-item container regression.
  - Main-menu positions: Passed — Native Host — run-16902693949945211376: menu y=114/height=492, player y=229/height=92, Start Game y=331/height=57 match Chromium; player/action gap remains 10 px.
  - Full parity: Failed — Native Host — 1.088% main-menu pixel mismatch remains, primarily text; actions container height is 252 vs 246 px and text advances still differ. All 15 cases captured, 5 combined/12 geometry/6 pixel passes; no threshold changes.
  - Core check: Passed — Automated — 798 tests, PMD and SpotBugs; the combined command exits nonzero only because compareViews correctly reports residual mismatches. Backend 137 and visual-tests 12 tests passed in the preceding aggregate check.
- Decisions and Deviations: Discovered by the required T15 capture; this fix is necessary to use the corrected line heights consistently in main-menu.
- Review Outcome: Not Reviewed
- Remaining Work: Implementation and verification complete; acceptance review. Residual action-container extent, glyph advances and edge coverage remain explicitly open under T16/T13.
- Resume or Closure: Scoped diff reviewed for the user-authorized separate commit; retain final capture run-16902693949945211376 as evidence.
