# T2 - Add retargetable property tracks

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P2 - Build scheduler and tracks.md`

## Goal
Represent generic transition track state that can delay, ease, complete, cancel, and retarget from the currently presented value.

## Dependencies
**Depends on:** `P2/T1`.
**Enables:** `P2/T3`, `P3/T2`.
**Parallelizable with:** None.

## Scope
- In: typed source/target/current state, timing progress, timing-function evaluation, replacement, and coordinator registration.
- Out: choosing which CSS properties interpolate and renderer reads.

## Requirements
- [ ] Separate current presented value from the computed CSS target captured by later style-change work.
- [ ] Honor delay before progress, selected timing functions during progress, and final target at completion.
- [ ] On retargeting, capture the value presented at the replacement instant as the next track source.
- [ ] Make cancel/replace paths remove or supersede prior tracks per element/property without double completion.

## Acceptance Checks
- [ ] Fake-clock tests prove delay, linear/eased progress, endpoint, cancellation, and replacement behavior.
- [ ] An interrupted track has no visual jump at the replacement frame.
- [ ] Track state is generic and does not hard-code a NanoVG value type.

## Verification
- Run focused track tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Do not add a second scheduler for keyframes; M5 must be able to reuse this track foundation.
