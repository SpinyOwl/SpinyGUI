# T2: Freeze Coordinate Stack Order

## Assignment

Implement only `P2/T2` from `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`.

## Dependency Status

**Depends on:** T1; accepted by the stepwise manager.
**Enables:** T3.
**Parallelizable with:** None.

## Requirements

- Add a concise, test-backed core contract for parent transform, scroll translation, overflow clipping, child transform, and paint ordering, including the exact inverse input order.
- Make the debug-rendering coordinate decision explicit in documentation/tests; do not add renderer or hit-test implementation.
- Add coordinate-model tests for nested transform/scroll/clip examples using existing core geometry primitives where possible.

## Scope Limits

- No CSS parsing, NanoVG calls, event traversal changes, or animation scheduling.
- Preserve unrelated edits; do not commit/stage/push; mark only T2-supported checkboxes.

## References

- Parent phase: `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Presentation state: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/PresentationState.java`
- Affine API: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/AffineTransform.java`

## Verification

- Run narrow core tests and return the standard handoff packet with model/fallback status.
