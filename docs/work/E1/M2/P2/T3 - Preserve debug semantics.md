# T3: Preserve Debug Semantics

Implement exactly M2/P2/T3. T2 is accepted after automated review.

**Depends on:** T2. **Enables:** None. **Parallelizable with:** None.

## Requirements

- Keep NvgDebugRenderer viewport/layout-space after the transformed layout-tree traversal, exactly as M1 defines.
- Add regression proof that debug drawing does not inherit transformed node state, child scroll translation, or subtree clip state.
- Confirm existing renderer tests remain green.

## Limits

- No input traversal, CSS parsing, or animation changes. Preserve unrelated work; no commit.

## References

- M1 coordinate contract, parent P2 plan, NvgRenderer/NvgDebugRenderer and current transform state tests.

## Verify

- Run focused debug/renderer tests, update only T3 evidence, and return full handoff/model fallback.
