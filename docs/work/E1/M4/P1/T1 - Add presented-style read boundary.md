# T1 - Add presented-style read boundary

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P1 - Render presented values.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Define one typed renderer-safe accessor that returns a presentation value when available and otherwise the computed CSS value.

## Dependencies
**Depends on:** None.
**Enables:** `P1/T2`.
**Parallelizable with:** None.

## Scope
- In: a core presentation-style read boundary for opacity, color, background color, border colors, and transform.
- Out: renderer migration, transition scheduling changes, layout-property animation, and pseudo-part state ownership.

## Relevant Source Context
- `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/PresentationState.java`
- `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/ResolvedStyle.java`
- `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/transition/TransitionPropertyName.java`

## Requirements
- [ ] Provide one typed current-or-computed accessor; renderers must not read the presentation map directly.
- [ ] Preserve the exact computed value when no presentation override exists.
- [ ] Keep access read-only and independent of NanoVG.

## Acceptance Checks
- [ ] Core tests prove fallback equality and presentation override behavior for every admitted property type.
- [ ] Layout accessors continue to read `ResolvedStyle`, not the paint accessor.

## Verification
- Run focused core presentation and transition tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Do not add box-shadow or scrollbar pseudo-part support.
