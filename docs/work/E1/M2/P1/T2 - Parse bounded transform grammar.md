# T2: Parse Bounded Transform Grammar

Implement only M2/P1/T2. T1 is accepted.

**Depends on:** T1. **Enables:** T3. **Parallelizable with:** None.

## Requirements

- Extend the transform provider to atomically parse ordered `translate`, `translateX/Y`, `scale`, `scaleX/Y`, and `rotate` values plus valid one/two-value transform origins.
- Preserve function ordering and units in typed `Transform`/`TransformOrigin` values.
- Reject skew, matrix, 3D, invalid arity/units/origin, and any declaration containing an invalid function; no partial prefix application.
- Add parsed CSS regression tests for supported combinations and invalid CSS.

## Limits

- No post-layout presentation resolution, rendering, input, or animation work; preserve unrelated changes; no commit.

## References

- Parent: `docs/work/E1/M2/P1 - Add transform CSS style support.md`
- Existing provider: `TransformPropertyProvider.java`; CSS parser terms/visitor; M1 types.

## Verify

- Focused transform/style-manager tests. Update only T2 checkboxes and report model/fallback.
