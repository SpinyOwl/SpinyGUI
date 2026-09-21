# P1 - Align Grid tracks

## Document Context
- Status: Completed
- Dependencies: M6/P2
- Parent: [M7 - Complete predictable Grid panels](../M7%20-%20Complete%20predictable%20Grid%20panels.md)
- Children: None
- Related: [Grid integration](../../M5%20-%20Grid%20integration.md)
- Next: Define exact axis alignment cases before changing GridLayout.

**Depends on:** M6/P2 — the current typed Grid layout and its established proof baseline.

## Goal

Honor Grid container `justify-content` and `align-content` without changing track-sizing or grid-item self-alignment semantics.

## Context

- `GridLayout.applyItemBoxes` currently calculates positions from content origin, track widths/heights, and fixed gaps.
- Resolved styles already expose alignment values used by flex and Grid item alignment.

## Phase Tasks

### T1: Specify bounded container-alignment semantics
**Purpose:** Define which existing alignment keywords apply to Grid axes and their behavior under positive, zero, and negative free space.

**Changes:**
- [x] Map supported `justify-content`/`align-content` values to track-start offsets and adjusted gaps.
- [x] Define the fallback for unsupported or baseline values and preserve current behavior when free space is non-positive.

**Acceptance Checks:**
- [x] Unit tests state expected coordinates for start, end, center, space-between, space-around, and space-evenly.

**Risks:** Do not reuse flex placement code if it changes Grid track or overflow semantics.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-19
- Implemented Scope: Added the bounded post-sizing `GridTrackAlignment` contract and typed parsing for `align-content: space-evenly`; no Grid item geometry consumes the contract until T2.
- Relevant Files and Symbols: spinygui.core/.../layout/impl/GridTrackAlignment.java; spinygui.core/.../layout/impl/GridTrackAlignmentTest.java; spinygui.core/.../style/types/flex/AlignContent.java; spinygui.core/.../style/manager/GridStyleManagerTest.java.
- Acceptance Evidence:
  - Expected coordinates for supported values: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*GridTrackAlignmentTest" --tests "*GridStyleManagerTest"` (13 GridTrackAlignment and 26 GridStyleManager tests; 0 failures/errors).
- Decisions and Deviations: The contract uses existing `flex-start`/`flex-end` identifiers for start/end. `stretch`, baseline, and unrecognized values retain current start geometry; non-positive free space never redistributes gaps.
- Review Outcome: Accepted — independent read-only review found no blocking findings; the focused Gradle evidence and current diff match the record.
- Remaining Work: None.
- Resume or Closure: T1 accepted; proceed to T2, which consumes the contract for final item geometry.

### T2: Apply final axis geometry in GridLayout
**Purpose:** Use the bounded alignment result for item positions, scroll extents, and clipping geometry.

**Changes:**
- [x] Apply aligned track offset/gap geometry after sizing and before item-box placement.
- [x] Preserve item `justify-self`, `align-self`, and stretch inside the aligned area.

**Acceptance Checks:**
- [x] Core Grid layout tests pass for both axes, implicit tracks, nested grids, and negative free space.
- [x] Overflow metrics remain based on actual final item geometry.

**Risks:** Alignment must not be applied twice by nested grids.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-19
- Implemented Scope: Applied accepted track offset and distributed-gap geometry to both Grid axes before item-box placement, while keeping self-alignment and stretch inside each final aligned area.
- Relevant Files and Symbols: spinygui.core/.../layout/impl/GridLayout.java (layoutGridContents, applyItemBoxes); spinygui.core/.../layout/impl/LayoutServiceProviderGridTest.java.
- Acceptance Evidence:
  - Final item geometry and overflow behavior: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*LayoutServiceProviderGridTest"` (33 tests; 0 failures/errors), covering both axes, implicit/nested Grid placement, and overflow under negative free space.
  - Alignment contract and typed style values: Passed — Automated — `./gradlew.bat :spinygui.core:test --tests "*GridTrackAlignmentTest" --tests "*GridStyleManagerTest"` (13 and 26 tests; 0 failures/errors).
- Decisions and Deviations: Alignment is resolved once per parent axis from final track sizes. Nested grids get a separate resolution only during their own content reflow; scroll-overflow geometry stays at start when free space is negative.
- Review Outcome: Accepted — independent read-only review found no findings; 72 focused tests and the current diff match the record.
- Remaining Work: None.
- Resume or Closure: P1 accepted; proceed to P2/T1 after re-checking the current GridLayout geometry flow.

## Verification Strategy

- `:spinygui.core:test --tests "*GridLayoutTest" --tests "*GridStyleManagerTest" --tests "*Overflow*Test"`

## Deferred Work

- Baseline content alignment remains outside this phase.
