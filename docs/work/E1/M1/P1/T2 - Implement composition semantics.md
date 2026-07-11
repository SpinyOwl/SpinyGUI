# T2: Implement Composition Semantics

## Assignment

Implement only `P1/T2` from `docs/work/E1/M1/P1 - Define transform values and composition.md`.

## Dependency Status

**Depends on:** T1; accepted by the stepwise manager.
**Enables:** T3.
**Parallelizable with:** None.

## Requirements

- Build on the accepted `Transform` and `TransformOrigin` types.
- Add a backend-neutral affine transform/matrix API and one authoritative transform-list composition method.
- Decide and encode operation multiplication order. Use tests with rotation plus translation so an accidental reversal fails.
- Resolve percentage translations and transform origin against final border-box dimensions, applying origin translation, operation list, and inverse origin translation.
- Cover zero-sized boxes and exact point outcomes with focused unit tests.

## Scope Limits

- Do not add CSS property parsing/providers, NanoVG rendering, hit testing, animation scheduling, or 3D/skew/matrix CSS syntax.
- Preserve unrelated user changes and do not commit/stage/push.
- Mark only T2 task/acceptance checkboxes supported by implementation and verification.

## References

- Parent phase: `docs/work/E1/M1/P1 - Define transform values and composition.md`
- Previous task: `docs/work/E1/M1/P1/T1 - Define typed transform values.md`
- Coordinate boundary: `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Existing geometry types: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/layout/`
- Existing accepted types: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/Transform.java` and `TransformOrigin.java`.

## Acceptance and Verification

- Composition tests prove the selected order, origin behavior, percent resolution, and zero-size behavior.
- Run focused `spinygui.core` transform tests.
- Return the standard implementer handoff including model/fallback status and manual-test requirement.
