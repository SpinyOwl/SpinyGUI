# T3 - Wire transition change handling

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3 - Detect changes and interpolate.md`

## Goal
Apply resolved transition descriptors to computed-style changes and maintain correct track state through retargeting, visibility changes, and removal.

## Dependencies
**Depends on:** `P3/T2`, `P1/T3`.
**Enables:** E1/M4, E1/M5.
**Parallelizable with:** None.

## Scope
- In: descriptor lookup, create/replace/cancel decisions, immediate fallback, `display: none`, and node-removal cleanup.
- Out: NanoVG presented-value reads, visible CSS transition demos, and keyframe precedence.

## Requirements
- [ ] Create one track for each changed, selected, compatible property with an effective non-immediate descriptor.
- [ ] Retarget an active property from its current presentation value and cancel a prior track exactly once.
- [ ] Apply zero-duration/non-interpolable/unselected changes immediately to the computed-target presentation fallback.
- [ ] Cancel and clear tracks when the element or ancestor becomes `display: none` or is removed; do not retain stale presentation state across reuse.

## Acceptance Checks
- [ ] Deterministic tests show one style change creates one track and a second change starts at the current presented value.
- [ ] Tests cover zero duration, delay-only configurations, `none`, `all`, explicit-property precedence, hide, removal, and incompatible pairs.
- [ ] No stale coordinator entry or presented value remains after cancellation/completion.

## Verification
- Run `./gradlew.bat :spinygui.core:test --tests *Transition* --tests *Animation* --tests *StyleManager*`.

## Constraints
- Do not commit unless explicitly requested.
- Do not change layout geometry or mutate CSS cascade targets.
