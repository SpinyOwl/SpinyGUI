# T1: Define Presentation-State Owner

## Assignment

Implement only `P2/T1` from `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`.

## Dependency Status

**Depends on:** None within P2; P1 is accepted by the stepwise manager.
**Enables:** T2.
**Parallelizable with:** None.

## Requirements

- Add the smallest node-owned or node-associated presentation state needed to hold a current visual transform separately from computed `ResolvedStyle`.
- Define and implement reset/lifecycle behavior appropriate to the current node model, including style recalculation, hidden nodes, and node removal where an existing lifecycle hook exists.
- Add focused tests proving presentation state cannot overwrite or clear computed style values and identify its ownership/frame access boundary in code documentation.
- Keep the state limited to the transform/presentation boundary; do not add transition tracks, CSS providers, renderer calls, or hit-test traversal.

## References

- Parent phase: `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Completed transform API: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/AffineTransform.java`
- Node/style surfaces: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/` and `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/ResolvedStyle.java`
- Future renderer/input consumers: M2 P2 and P3 documents.

## Scope Limits

- Preserve unrelated worktree changes; no commits/staging/pushes.
- Do not implement CSS transform parsing, NanoVG matrix state, transition scheduling, or input mapping.
- Mark only P2/T1 checkboxes supported by evidence.

## Verification

- Run narrow core tests for the chosen node/presentation-state surface plus transform tests when relevant.
- Return the standard handoff packet and model/fallback information.
