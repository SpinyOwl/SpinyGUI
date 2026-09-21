# P2 - Stabilize intrinsic Grid sizing

## Document Context
- Status: Completed
- Dependencies: P1
- Parent: [M7 - Complete predictable Grid panels](../M7%20-%20Complete%20predictable%20Grid%20panels.md)
- Children: None
- Related: [Grid track sizing](../../M3%20-%20Grid%20track%20sizing.md)
- Next: Inventory the existing pre-layout measurements used by auto tracks.

**Depends on:** P1 — final track alignment semantics and resulting content-box geometry.

## Goal

Make intrinsic Grid track dimensions stable for realistic panel contents without unbounded child-layout retries.

## Context

- Auto-track contributions currently use grid item border-box sizes and only count single-track spans.
- Grid reflows children after assigning their final areas, including nested Grid and Flex elements.

## Bounded Contribution Contract

| Content case | Pre-measure contribution | Final reflow |
| --- | --- | --- |
| Definite block | Existing border-box width and height | Only when Grid changes its assigned area |
| Block with auto-width inline text | Existing border box is a provisional candidate | Required once after the final Grid width is known |
| Button, input, textarea | Existing control border box | Not for intrinsic sizing; controls have no flow descendants |
| Flex or nested Grid | Existing border box is a provisional candidate | Required once for the nested layout after Grid assigns its area |

The bounded order is: lay out child nodes once using the parent pre-layout content box; read these
contributions; resolve tracks; assign final Grid areas; then reflow only marked items once. Spans
remain excluded from single-track auto contributions. The contract does not introduce a retry loop.

## Phase Tasks

### T1: Define measured contribution inputs
**Purpose:** Establish which current child box measurements are valid before final Grid area assignment.

**Changes:**
- [x] Classify text, button, input, textarea, block, flex, and nested-grid contribution cases.
- [x] Define a bounded pre-measure order and the conditions that require a final reflow.

**Acceptance Checks:**
- [x] Fixtures identify expected intrinsic width/height contributions and final content boxes.

**Risks:** Width-dependent wrapped text must not silently use stale line layout.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-19
- Implemented Scope: Added the bounded `GridIntrinsicContribution` classifier and documented one pre-layout measurement followed by at most one final reflow for marked Grid items.
- Relevant Files and Symbols: spinygui.core/.../layout/impl/GridIntrinsicContribution.java; spinygui.core/.../layout/impl/GridIntrinsicContributionTest.java; spinygui.core/.../layout/impl/LayoutServiceProviderGridTest.java; this P2 contract.
- Acceptance Evidence:
  - Measured contribution fixtures: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*GridIntrinsicContributionTest" --tests "*LayoutServiceProviderGridTest"` (3 and 34 tests; 0 failures/errors). The Grid fixture marks an auto-width text cell for final reflow and proves its final 70px content box after an 80px track and padding are assigned.
- Decisions and Deviations: Controls use their current leaf border box. Auto-width text, Flex, and nested Grid are marked for one final reflow; no general convergence loop is introduced.
- Review Outcome: Accepted — requested final-geometry fixture now proves the final 70px content box after one reflow; no source-risk surface changed.
- Remaining Work: None.
- Resume or Closure: T1 accepted; proceed to T2 and consume the bounded contribution contract in GridLayout.

### T2: Implement bounded contribution and reflow handling
**Purpose:** Feed stable contributions into auto/minmax/fit-content tracks and retain final child geometry.

**Changes:**
- [x] Update GridLayout sizing and reflow flow for the approved contribution cases.
- [x] Keep fixed, percentage, and `fr` behavior unchanged outside their interaction with intrinsic tracks.

**Acceptance Checks:**
- [x] Core tests cover wrapped text, controls, nested layouts, spans, mixed track types, scroll metrics, and no-convergence regressions.

**Risks:** Stop and record a decision if a required case needs an additional general measurement API.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-19
- Implemented Scope: Grid auto/minmax/fit-content sizing now reads the accepted `GridIntrinsicContribution` candidate instead of reaching into child boxes directly. Grid performs its one final child-content reflow only for marked flow or nested-layout items. Marked Flex items now run their real Yoga pass while preserving the Grid-assigned content area; the assigned-area path bypasses post-Yoga intrinsic auto-height growth.
- Relevant Files and Symbols: spinygui.core/.../layout/impl/GridLayout.java (resolveAutoTrack, intrinsicContribution, reflowItemContents); spinygui.core/.../layout/impl/FlexLayout.java (layoutAssignedArea); spinygui.core/.../layout/impl/IntrinsicFlexLayout.java; spinygui.core/.../layout/impl/LayoutServiceProvider.java; spinygui.core/.../layout/impl/LayoutServiceProviderGridTest.java; spinygui.core/.../layout/impl/OverflowLayoutTest.java.
- Acceptance Evidence:
  - Stable intrinsic track and final child geometry: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*LayoutServiceProviderGridTest"` (36 tests; 0 failures/errors), covering wrapped text, input/textarea auto tracks, nested Grid, spans, mixed tracks, and negative-space overflow.
  - Contribution and overflow regressions: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*GridIntrinsicContributionTest" --tests "*OverflowLayoutTest"` (3 intrinsic-contribution tests and focused overflow suite; 0 failures/errors).
  - Grid-to-Flex final-area resize and overflow: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*LayoutServiceProviderGridTest" --tests "*FlexLayoutTest" --tests "*IntrinsicFlexLayoutTest"` (36 Grid tests and focused Flex suites; 0 failures/errors). The Flex Grid item remains at position 0px,0px with its assigned 100px by 40px content box, then grows to 200px by 40px; its children update from 50px/50px to 100px/100px. A non-shrinking 80px-tall Flex child may overflow, while its Grid-assigned parent remains exactly 100px by 40px.
- Decisions and Deviations: The pre-layout candidate remains the existing border box and does not attempt browser-style min/max-content measurement. The marked final reflow is a single, non-iterative pass; controls remain leaf measurements.
- Review Outcome: Accepted — final independent review verified the assigned-area path bypasses intrinsic auto-height growth and the tall-child fixture retains exact Grid geometry.
- Remaining Work: None.
- Resume or Closure: P2 accepted; proceed to P3/T1 regression fixtures.

## Verification Strategy

- `:spinygui.core:test --tests "*GridLayoutTest" --tests "*BlockLayoutTest" --tests "*FlexLayoutTest" --tests "*Input*Test"`

## Deferred Work

- Full browser intrinsic track sizing and baseline alignment.
