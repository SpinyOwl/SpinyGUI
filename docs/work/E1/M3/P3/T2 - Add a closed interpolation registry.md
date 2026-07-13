# T2 - Add a closed interpolation registry

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3 - Detect changes and interpolate.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Allow tracks only for compatible values in the approved paint/transform subset and give all other changes an explicit immediate fallback.

## Dependencies
**Depends on:** `P3/T1`.
**Enables:** `P3/T3`.
**Parallelizable with:** None.

## Scope
- In: a closed core registry for `opacity`, `color`, `background-color`, supported border colors, `transform`, and `box-shadow` only if its current typed model supports compatible interpolation.
- Out: layout-property animation, arbitrary custom properties, and renderer-specific interpolation.

## Requirements
- [ ] Define midpoint/endpoint interpolation for each admitted value type, including compatible transform operations under the M1 contract.
- [ ] Admit `box-shadow` only after its existing value type can prove compatible shape; otherwise classify it as immediate and record that limit.
- [ ] Classify missing values, incompatible pairs, discrete values, and all layout-affecting properties as immediate.
- [ ] Ensure every interpolation result is written through the presentation overlay, not computed style.

## Acceptance Checks
- [ ] Each supported type has deterministic midpoint and endpoint unit tests.
- [ ] Incompatible or unsupported pairs create no track and resolve to their computed target.
- [ ] Tests prove width/height/margin/padding/position values remain non-transitionable.

## Verification
- Run focused transition interpolation tests in `:spinygui.core:test`.

## Constraints
- Do not commit unless explicitly requested.
- Do not widen the registry merely because the parser recognizes a property.
