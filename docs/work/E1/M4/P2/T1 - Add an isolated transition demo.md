# T1 - Add an isolated transition demo

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P2 - Prove and document transitions.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Add a compact complex-demo example that visibly exercises the delivered transition subset using real CSS declarations.

## Dependencies
**Depends on:** `P1/T3`.
**Enables:** `P2/T2`.
**Parallelizable with:** None.

## Scope
- In: opacity, color/background color, and transform transitions triggered by an isolated hover, focus, or programmatic style change.
- Out: test-only clocks, main-menu work, box-shadow, scrollbar, layout, and keyframe demos.

## Relevant Source Context
- `spinygui.demo.complex/src/main/java/com/spinyowl/spinygui/demo/complex/Demo.java`
- Existing complex-demo example classes and CSS setup.

## Requirements
- [x] Use actual `transition` shorthand or longhand declarations.
- [x] Keep the example independent of unrelated demo features.
- [x] Demonstrate a visible intermediate state with the normal host transition tick.

## Acceptance Checks
- [ ] The complex demo compiles and the example can be manually observed at intermediate progress.
- [ ] The demo changes only the admitted M4 property subset.

## Verification
- Run `:spinygui.demo.complex:classes` and perform a manual visual check.

## Constraints
- Do not commit unless explicitly requested.
