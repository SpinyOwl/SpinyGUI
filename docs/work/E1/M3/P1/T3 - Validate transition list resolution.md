# T3 - Validate transition list resolution

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P1 - Parse transition declarations.md`

## Goal
Resolve per-property transition descriptors deterministically from parsed transition lists.

## Dependencies
**Depends on:** `P1/T2`.
**Enables:** `P2/T1`, `P3/T3`.
**Parallelizable with:** None.

## Scope
- In: one documented descriptor-resolution utility and its unit tests.
- Out: discovering style changes or creating runtime tracks.

## Requirements
- [ ] Repeat duration, delay, and timing-function lists to the number of `transition-property` entries.
- [ ] Treat `none` as disabling all transition selection.
- [ ] Give a later explicit property entry precedence over `all` and an earlier duplicate entry.
- [ ] Define zero-duration and delay-only outcomes so runtime code can select immediate versus delayed presentation consistently.

## Acceptance Checks
- [ ] Tests name and prove each list-repeat, `all`, `none`, duplicate, and zero-duration rule.
- [ ] Every supported selected property resolves to at most one descriptor.
- [ ] Resolution has no dependency on a renderer, clock, or mutable element state.

## Verification
- Run focused transition descriptor-resolution tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Keep this bounded CSS behavior documented in code/tests rather than relying on unspecified browser compatibility.
