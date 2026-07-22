# T1 - Capture completed style targets

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3 - Detect changes and interpolate.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Expose old and new computed targets to the transition coordinator only after a complete successful style cascade.

## Dependencies
**Depends on:** `P2/T3`.
**Enables:** `P3/T2`.
**Parallelizable with:** None.

## Scope
- In: snapshots/notifications at the style manager–coordinator boundary and tests for stylesheet, inline-style, and pseudo-class-driven recalculation.
- Out: interpolation algorithms and renderer presentation reads.

## Requirements
- [ ] Capture the previous computed target before `StyleManagerImpl` clears/reapplies a valid element style, then compare it with the fully defaulted new target.
- [ ] Notify once per element after all matching declarations and absent-property defaults are applied.
- [ ] Preserve existing behavior for failed inline parsing: retain the prior valid ruleset/target and do not create a transition event.
- [ ] Do not call `presentationState().reset()` in a way that destroys an active track before the coordinator can retarget it.

## Acceptance Checks
- [ ] Tests prove one comparison for stylesheet, inline-style, and pseudo-class style changes.
- [ ] Tests prove no comparison/track for a failed parse or an unchanged target.
- [ ] Snapshot storage does not expose a partially applied `ResolvedStyle`.

## Verification
- Run `./gradlew.bat :spinygui.core:test --tests *Transition* --tests *StyleManager*`.

## Constraints
- Do not commit unless explicitly requested.
- Preserve normal CSS cascade ordering and typed `ResolvedStyle` accessors.
