# T2 - Add end-to-end regressions

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P2 - Prove and document transitions.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Prove deterministic style-change, coordinator, layout, and NanoVG output behavior at known timestamps.

## Dependencies
**Depends on:** `P2/T1`.
**Enables:** `P2/T3`.
**Parallelizable with:** None.

## Scope
- In: the minimum M3 compatibility repair needed to interpolate compatible `Transform.Operations` lists, plus initial, delayed, midpoint, retargeted, completed, hidden, clipped, nested-transform, input, and textarea cases for the admitted subset.
- Out: wall-clock tests, scrollbar pseudo-part animation, box-shadow, and layout-property transitions.

## Relevant Source Context
- `spinygui.core/src/main/java/com/spinyowl/spinygui/core/animation/TransitionCoordinator.java`
- Existing NanoVG recording/sink tests under `spinygui.core.backend.lwjgl.nanovg/src/test/java`.

## Requirements
- [x] Extend the M3 transform interpolator only for compatible operation lists produced by the CSS transform provider; incompatible lists remain immediate.
- [x] Use fake `TimeService` values; do not use wall-clock timing.
- [x] Assert rendered values at initial, midpoint, retargeted, and completed states.
- [x] Cover no-animation fallback and unchanged existing renderer behavior.

## Acceptance Checks
- [ ] Focused tests prove expected output at each known timestamp.
- [ ] Existing block, flex, overflow, input, and transform suites remain green.

## Verification
- Run focused core transition tests and NanoVG renderer tests, then the relevant full module suites.

## Constraints
- Do not commit unless explicitly requested.
