# T1 - Define transition coordinator lifecycle

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P2 - Build scheduler and tracks.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Publish a small core coordinator with a deterministic host-facing update boundary and explicit lifecycle ownership.

## Dependencies
**Depends on:** `P1/T3`.
**Enables:** `P2/T2`.
**Parallelizable with:** None.

## Scope
- In: coordinator ownership, `TimeService` use, first-tick behavior, cancellation/completion cleanup, and node-removal cleanup hooks.
- Out: a shared application runtime, renderer ownership, and property-specific interpolation.

## Requirements
- [ ] Add or document one explicit public host call that advances transition state once per frame after style change detection and before layout/render.
- [ ] Wrap or narrowly amend `AnimatorImpl` only where needed to guarantee a zero-motion first tick and exactly-once completion/destruction.
- [ ] Define idempotent cancellation and removal cleanup, including tracks created but not yet advanced.
- [ ] Keep coordinator state in core services and presented values in `Element.presentationState()`; never write animation results to `ResolvedStyle`.

## Acceptance Checks
- [ ] Fake-time tests prove zero first-frame delta, monotonic updates, cancellation, and exactly-once completion cleanup.
- [ ] Removing an element clears all associated coordinator state without retaining its subtree.
- [ ] Core code has no dependency on NanoVG or demo packages.

## Verification
- Run focused animation/coordinator tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Stop and revise the boundary if it requires a renderer callback; renderer-owned scheduling is outside M3.
