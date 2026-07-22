# T1 - Add typed transition descriptors

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P1 - Parse transition declarations.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Add core-only immutable models for bounded transition configuration and resolved per-property descriptors.

## Dependencies
**Depends on:** None.
**Enables:** `P1/T2`.
**Parallelizable with:** None.

## Scope
- In: property-selection values, duration/delay values, named and cubic-bezier timing values, a descriptor list, and explicit CSS initial values.
- Out: parser/provider registration, track execution, renderer code, and keyframe models.

## Relevant Source Context
- Existing typed CSS value examples: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/Transform.java`, `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/TransformOrigin.java`.
- Existing style type test pattern: `spinygui.core/src/test/java/com/spinyowl/spinygui/core/style/types/TransformTest.java`.
- Keep new models under core style types, for example `spinygui.core/src/main/java/com/spinyowl/spinygui/core/style/types/transition/`, unless the existing package conventions make another core-only location clearly better.

## Requirements
- [ ] Model `none`, `all`, and named transition-property selections without retaining unvalidated raw property names after validation.
- [ ] Model `linear`, `ease`, `ease-in`, `ease-out`, `ease-in-out`, and validated `cubic-bezier(x1, y1, x2, y2)` timing functions.
- [ ] Define initial values equivalent to `all 0s ease 0s` and preserve list ordering.
- [ ] Keep models independent of NanoVG, `Animator`, and mutable `ResolvedStyle` storage.

## Acceptance Checks
- [ ] Unit tests construct each supported descriptor and prove equality/default behavior.
- [ ] Invalid time and cubic-bezier inputs have explicit rejected results rather than partial descriptors.
- [ ] No new core animation type imports backend packages.

## Verification
- Run focused new transition value tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Follow `AGENTS_CODE_STYLE.md` and retain typed CSS boundaries.
