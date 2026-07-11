# T1: Inverse Map Pointer Coordinates

Implement exactly M2/P3/T1. M2/P2 is accepted.

**Depends on:** None within P3. **Enables:** T2. **Parallelizable with:** None.

## Requirements

- Apply inverse presentation transforms through the ancestor stack before hit/bounds decisions, in the exact M1 reverse order.
- A missing inverse makes that transformed subtree non-targetable; preserve pointer-events, clipping, scroll, visibility, and traversal rules.
- Add focused tests for translated/rotated/scaled targets at visual vs stale locations.

## Limits

- No demo, CSS parsing, transition scheduling, or unrelated input refactor. Preserve unrelated changes; no commit.

## References

- M1 coordinate contract; AffineTransform.inverse; existing system mouse event and node-intersection code.

## Verify

- Run focused input/transform/overflow tests. Update only T1 evidence and provide full handoff/model fallback.
