# T2: Add Nested Control Geometry Regressions

Implement exactly M2/P3/T2. T1 is accepted.

**Depends on:** T1. **Enables:** T3. **Parallelizable with:** None.

## Requirements

- Add regressions for transformed input, button, and textarea targets inside nested transforms and scrollable overflow containers.
- Verify pointer target selection follows visual geometry, clips reject hidden points, and layout boxes remain unchanged.

## Limits

- No demo/CSS parsing/animation work; preserve unrelated edits; no commit.

## References

- Parent P3 plan, PresentationCoordinates, input event listener tests, overflow tests.

## Verify

- Run focused input/overflow/transform tests, mark only T2 evidence, and return full handoff/model fallback.
