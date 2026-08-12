# P2: Skip Unchanged Pointer Refresh

## Document Context

- Document Type: Phase implementation plan
- Status: Proposed
- Created: 2026-08-12
- Parent Milestone: E6/M1.5 - Skip Proven No-Impact Input Frames

## Goal

Classify same-target pointer movement as unchanged only when processing proves that it cannot affect
hover, capture, drag, selection, scrollbars, listeners, or any other presentation state.

## Phase Tasks

### T1: Prove the Same-Hit-Path No-Impact Case

**Purpose:** Establish the minimum safe pointer fast path using existing hit-path and interaction state.
**Depends on:** M1.5/P1. **Enables:** T2. **Parallelizable with:** None.
**Changes:**
- [x] Compare previous and current hit paths without allocating temporary collections on the target
  steady-state path.
- [x] Return unchanged only when the hit path is stable, no enter/exit transition occurs, no button or
  wheel event is present, no capture/drag/selection/scrollbar state is active, and no move listener or
  other consumer can affect presentation.
- [x] Preserve cursor-position state required for later correct click, drag, scroll, and hit testing.
- [x] Route every ambiguous pointer state to full refresh required.
**Acceptance Checks:**
- [x] Repeated motion inside one inert element reports unchanged after warmup.
- [x] Crossing element boundaries, active capture/drag/selection, and listener-bearing targets never
  use the no-impact path without explicit proof.
- [x] Hit testing and stored pointer coordinates remain equivalent to force-full processing.
**Risks:** Target identity alone is insufficient because movement listeners and captured interactions
may mutate presentation without a hover-path change.

### T2: Integrate and Verify Pointer Decisions

**Purpose:** Make the pointer outcome consumable by hosts and prove safe behavior across interaction
boundaries.
**Depends on:** T1. **Enables:** M1.5 validation. **Parallelizable with:** None.
**Changes:**
- [x] Aggregate pointer outcomes into the processing-batch result and structural counters.
- [x] Add tests for inert same-target motion, enter/exit, press/release/click, drag, selection,
  scrollbar interaction, wheel scroll, transformed hit testing, and arbitrary listeners.
- [x] Compare optimized and forced-full state/render results for representative fixtures.
- [x] Record matched pointer-active allocation and refresh-request evidence.
**Acceptance Checks:**
- [x] No-impact motion reduces refresh requests without changing event delivery or pointer state.
- [x] All pointer interactions with actual or unknown effects retain full refresh required.
- [x] Evidence reports both per-frame and per-second cost.
**Risks:** Benchmarks can reward skipped listener delivery; event delivery semantics must remain
unchanged and only the subsequent style/layout decision may be skipped.

## Verification Strategy

- Run focused core system-event and GUI-event tests, followed by the full core test suite.
- Use deterministic hit-test fixtures and force-full state/render comparison.
- Capture both capped and uncapped pointer-active recordings.

## Implemented Boundary

`SystemCursorPosEventListener.processWithImpact` reuses per-frame hit-path buffers and returns
`UNCHANGED` only for stable, inert hit paths. It keeps cursor coordinates updated in every case.
Enter/exit transitions, pressed buttons, focused pressed controls, scrollbar interaction, drag or
selection, and pointer listeners retain the full-refresh path. Existing `process` callers retain
their original event behavior.

## Dependency Graph

```mermaid
flowchart TD
  P1["M1.5/P1 - Contract"] --> T1["T1 - Prove pointer no-impact"]
  T1 --> T2["T2 - Integrate and verify"]
```
