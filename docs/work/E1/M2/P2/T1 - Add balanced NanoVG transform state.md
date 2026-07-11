# T1: Add Balanced NanoVG Transform State

Implement exactly M2/P2/T1; M2/P1 is accepted.

**Depends on:** None within P2. **Enables:** T2. **Parallelizable with:** None.

## Requirements

- Add a renderer-local adapter from backend-neutral `AffineTransform` to balanced NanoVG save/transform/restore calls.
- Apply it around each element subtree, including empty-child and exception-safe traversal paths; sibling state must not leak.
- Use `Element.presentationState().transform()` only. Do not alter CSS parsing, layout, clips/scroll composition, input, or animation.
- Add NanoVG recording tests for nested and sibling transform state balance.
- If existing renderer tests cannot observe traversal state, add the smallest package-private injectable transform-state factory/sink in `NvgRenderer` solely for recording tests; do not expose a new public renderer API.
- If traversal still cannot be exercised because leaf renderers are concrete/private, add only the package-private leaf-renderer adapter/factory required to inject recording or throwing test doubles. Keep production constructors and public API unchanged.

## References

- Parent: `docs/work/E1/M2/P2 - Apply transforms in NanoVG rendering.md`
- Contract: `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- `NvgRenderer`, existing NanoVG test sinks, `AffineTransform`, and `PresentationState`.

## Limits and Verification

- Preserve unrelated changes; no commit/stage/push; check only T1 evidence.
- Add and run NanoVG recording tests for nested transforms plus sibling/exception restoration, then provide full handoff/model fallback.
