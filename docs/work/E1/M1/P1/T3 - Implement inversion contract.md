# T3: Implement Inversion Contract

## Assignment

Implement only `P1/T3` from `docs/work/E1/M1/P1 - Define transform values and composition.md`.

## Dependency Status

**Depends on:** T2; accepted by the stepwise manager.
**Enables:** None.
**Parallelizable with:** None.

## Requirements

- Extend the accepted backend-neutral affine API with inverse coordinate mapping or inversion that has an explicit failure result for singular matrices.
- Use one clearly documented determinant/singularity policy; do not silently return identity or layout-space coordinates.
- Add round-trip tests for an invertible composed transform and a zero-scale/singular test proving the failure behavior.
- Keep this node independent of CSS parsing, renderer, hit-test traversal, and animation scheduling.

## Scope Limits

- Preserve unrelated worktree changes and do not commit, stage, or push.
- Update only this node's task/phase checkboxes when acceptance evidence is complete.

## References

- Parent phase: `docs/work/E1/M1/P1 - Define transform values and composition.md`
- Accepted T2: `docs/work/E1/M1/P1/T2 - Implement composition semantics.md`
- Source: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/AffineTransform.java`
- Future consumer contract: `docs/work/E1/M2/P3 - Add transform-aware input and visible proof.md`

## Verification

- Run focused transform/affine tests in `spinygui.core`.
- Return the required stepwise-implementer handoff including model and fallback status.
