# T3 - Protect clip and nested behavior

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P1 - Render presented values.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Prove that visual-only presentation values remain correct inside existing transform, clip, scroll, and control paths.

## Dependencies
**Depends on:** `P1/T2`.
**Enables:** `P2/T1`.
**Parallelizable with:** None.

## Scope
- In: nested transforms, overflow clipping, scroll containers, input/textarea rendering, and completion fallback regressions.
- Out: new transitionable properties and changes to scrolling or hit-test semantics.

## Relevant Source Context
- `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgRendererTransformStateTest.java`
- `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/util/NvgClipStackTest.java`
- `spinygui.core/src/test/java/com/spinyowl/spinygui/core/util/NodeUtilitiesTransformHitTest.java`

## Requirements
- [x] Add deterministic intermediate and completed presentation tests under nested transformed and overflow-clipped elements.
- [x] Prove scroll and layout metrics remain stable while paint values change.
- [x] Retain existing input, textarea, clip, and save/restore behavior.

## Acceptance Checks
- [ ] Clip and transform recording tests pass at intermediate progress.
- [ ] No layout test changes solely from a paint transition.

## Verification
- Run focused NanoVG clip, transform, input, textarea, and core layout tests.

## Constraints
- Do not commit unless explicitly requested.
