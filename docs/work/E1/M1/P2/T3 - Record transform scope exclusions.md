# T3: Record Transform Scope Exclusions

## Assignment

Implement only `P2/T3` from `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`.

## Dependency Status

**Depends on:** T2; accepted by the stepwise manager.
**Enables:** None.
**Parallelizable with:** None.

## Requirements

- Record the static-transform release exclusions in the phase and relevant M2 acceptance boundaries: layout boxes, normal flow, client/scroll metrics, containing blocks, and z-index sorting remain unchanged.
- Specify that a transformed parent visually affects its descendant paint subtree but does not establish a new layout containing block or stacking context.
- Add focused tests or an executable contract assertion proving presentation transforms do not mutate layout geometry or computed style.
- Ensure M2 task documents explicitly reference the presentation-state and visual-coordinate contract, with no renderer special-case escape hatch.

## Scope Limits

- Documentation/contract/test work only. Do not add CSS parsing, NanoVG rendering, hit-testing traversal, or transition scheduling.
- Preserve unrelated changes; do not commit, stage, or push.
- Mark only T3 checkboxes when their evidence is complete.

## References

- Parent phase: `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Accepted contract test: `spinygui.core/src/test/java/com/spinyowl/spinygui/core/style/types/VisualCoordinateContractTest.java`
- Presentation state: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/PresentationState.java`
- M2 phase docs under `docs/work/E1/M2/`.

## Verification

- Run narrow core contract/presentation/transform tests.
- Return the standard implementer handoff with model and fallback status.
