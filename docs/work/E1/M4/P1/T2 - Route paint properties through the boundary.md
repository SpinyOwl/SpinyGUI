# T2 - Route paint properties through the boundary

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P1 - Render presented values.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Use the presented-style boundary consistently across the supported NanoVG element paint surfaces.

## Dependencies
**Depends on:** `P1/T1`.
**Enables:** `P1/T3`.
**Parallelizable with:** None.

## Scope
- In: element background, border, text, input, textarea, opacity composition, and transform composition after layout sizing.
- Out: scrollbar pseudo-part transitions, box-shadow transitions, and any layout metric change.

## Relevant Source Context
- `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgRenderer.java`
- `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgElementRenderer.java`
- `spinygui.core/src/main/java/com/spinyowl/spinygui/core/layout/impl/LayoutServiceImpl.java`

## Requirements
- [x] Route every admitted paint color and opacity read through the boundary.
- [x] Resolve the presented `Transform` through `TransformComposition` after border-box sizing, preserving the M3 style → tick → layout → render order.
- [x] Keep caret and selection colors as fixed control UI colors and leave scrollbar pseudo-parts on computed fallback.

## Acceptance Checks
- [ ] NanoVG recording tests show intermediate opacity, color, and transform values.
- [ ] A no-animation recording remains equivalent to the computed-style baseline.
- [ ] No layout geometry or text measurement changes solely because a paint transition is active.

## Verification
- Run focused NanoVG renderer, input, textarea, and transform-state tests.

## Constraints
- Do not commit unless explicitly requested.
- Do not scatter direct presentation-map lookups across renderers.
