# T3: Resolve Transforms After Layout

Implement only M2/P1/T3. T1 and T2 are accepted.

**Depends on:** T2. **Enables:** None. **Parallelizable with:** None.

## Requirements

- After final border-box dimensions are known, resolve computed `Transform` and `TransformOrigin` into the owning `PresentationState` using `TransformComposition`.
- Identify the existing layout completion boundary; ensure percentages use final box dimensions and stale presentation state resets when no transform applies.
- Do not alter layout dimensions, normal flow, scroll/client metrics, or computed style values.
- Add focused layout tests for percentage transforms/origins and unchanged geometry.

## Limits

- No NanoVG transform state or hit-test traversal yet. Preserve unrelated changes; no commit.

## References

- `docs/work/E1/M2/P1 - Add transform CSS style support.md`
- `LayoutServiceImpl`, node layout boxes, `PresentationState`, `TransformComposition`.

## Verify

- Add and run focused core layout regression tests for percentage translation/origin and unchanged layout/scroll geometry; update only T3 checkbox evidence after they pass.
- Provide full handoff/model fallback.
