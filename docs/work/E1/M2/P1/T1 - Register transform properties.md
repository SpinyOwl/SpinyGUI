# T1: Register Transform Properties

## Assignment

Implement only M2/P1/T1 from the parent phase document.

**Depends on:** None; M1 accepted.
**Enables:** T2.
**Parallelizable with:** None.

## Requirements

- Register `transform` and `transform-origin` constants, property providers, defaults, and typed `ResolvedStyle` accessors.
- Both stylesheet and inline-style paths must use the standard property store/provider path.
- Default values must be `Transform.NONE` and `TransformOrigin.CENTER`; do not parse transform functions yet.
- Add parsed-CSS StyleManager tests for defaults and property discovery.

## Scope Limits

- No transform grammar parsing, post-layout resolution, NanoVG rendering, hit testing, or animation work.
- Preserve unrelated changes; do not commit/stage/push.
- Mark only T1 phase checkboxes supported by evidence.

## References

- `docs/work/E1/M2/P1 - Add transform CSS style support.md`
- `docs/work/E1/M1/P2 - Define visual-coordinate boundary.md`
- Existing providers in `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/stylesheet/property/`
- `ResolvedStyle.java`, `Properties.java`, and `DefaultPropertyStoreProvider.java`.

## Verification

- Run focused core style/transform tests and provide standard handoff, including model/fallback.
