# T2: Compose Transform Clip and Scroll

Implement exactly M2/P2/T2. P2/T1 automated evidence is accepted.

**Depends on:** T1. **Enables:** T3. **Parallelizable with:** None.

## Requirements

- Implement the M1 coordinate contract: transformed parent overflow clips, child-content scroll translation, child layout offset, and child presentation transform compose in the documented order.
- Ensure background/border/text/input/textarea/scrollbars follow the correct subtree state; scrollbars use parent transform but not child scroll translation.
- Add NanoVG recording regressions for nested transforms in overflowing scroll containers.

## Limits

- Do not implement pointer traversal, new CSS parsing, or animation. Keep debug behavior for T3.
- Preserve unrelated changes; no commit; update only T2 evidence.

## References

- `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Parent phase and NvgRenderer/NvgClipStack/overflow code.

## Verify

- Run focused NanoVG renderer and relevant overflow tests; full handoff/model fallback.
