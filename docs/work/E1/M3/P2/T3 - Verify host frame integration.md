# T3 - Verify host frame integration

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P2 - Build scheduler and tracks.md`

## Goal
Prove and document manual host integration of the coordinator without coupling core animation to a renderer or introducing E2's runtime abstraction.

## Dependencies
**Depends on:** `P2/T2`.
**Enables:** `P3/T1`.
**Parallelizable with:** None.

## Scope
- In: host integration documentation, an updated demo/harness call ordering, and a no-NanoVG core integration test where possible.
- Out: standardizing service construction, event dispatch, layout, or rendering in a shared runtime.

## Requirements
- [ ] Document the host order: process input/style invalidation, recalculate styles as needed, tick the transition coordinator, then perform layout/render according to the host's existing responsibilities.
- [ ] Move the complex demo's animation advancement from after rendering to the documented pre-render boundary once a transition track can be exercised.
- [ ] Make manual composition the supported M3 integration path; identify E2 as the future optional standard runtime.

## Acceptance Checks
- [ ] A harness/deterministic test advances a coordinator track through the host boundary without `NvgRenderer` involvement.
- [ ] The demo classes compile with the integration call at the documented boundary.
- [ ] Public API/docs identify host ownership and cleanup responsibility.

## Verification
- Run focused core coordinator tests and `./gradlew.bat :spinygui.demo.complex:classes`.

## Constraints
- Do not commit unless explicitly requested.
- Stop if this requires changing every host to a new runtime; defer that work to E2.
