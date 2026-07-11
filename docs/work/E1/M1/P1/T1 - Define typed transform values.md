# T1: Define Typed Transform Values

## Assignment

Implement only `P1/T1` from `docs/work/E1/M1/P1 - Define transform values and composition.md`.

## Dependency Status

**Depends on:** None; cleared by the stepwise manager.
**Enables:** T2.
**Parallelizable with:** None.

## Requirements

- Add immutable, backend-neutral core types for the first-release 2D transform operations: `none`, translate, scale, rotate, plus a two-value transform origin.
- Reject non-finite numeric values at construction and keep the API free of NanoVG/LWJGL dependencies.
- Add focused construction/validation tests covering every supported operation, default representation, and invalid numeric input.
- Do not add CSS parsing/property providers, matrix composition/inversion, rendering, hit testing, or animation scheduling; those belong to later nodes.

## References

- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M1 - Transform contract.md`
- Phase: `docs/work/E1/M1/P1 - Define transform values and composition.md`
- High-level design: `docs/features/css-animation-support-plan.md`
- Existing style types: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/`
- Repository instructions: user-provided `AGENTS.md` directions and `AGENTS_CODE_STYLE.md`.

## Scope Limits

- Preserve unrelated working-tree changes, especially the main-menu demo files.
- Do not commit, stage, push, or edit another graph node's checkboxes.
- Update only this task document and the selected phase's T1 checkboxes when implementation and verification evidence supports it.

## Acceptance and Verification

- `Transform*` construction/validation tests pass in `spinygui.core`.
- Run the narrowest relevant Gradle test command and report its output.
- Inspect the final diff and provide the required implementer handoff: node, files, behavior, checked boxes, tests run/not run, risks, model, and fallback status.
