# T3 - Update support documentation

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M4 - CSS transitions.md`
- Phase: `docs/work/E1/M4/P2 - Prove and document transitions.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Mark only verified M4 transition support and record remaining immediate/deferred behavior.

## Dependencies
**Depends on:** `P2/T2`.
**Enables:** E1/M5, E1/M6.
**Parallelizable with:** None.

## Scope
- In: `docs/features/css-properties-support.md`, the E1 roadmap, supported timing functions, target subset, and explicit deferrals.
- Out: optimistic browser-compatibility claims or implementation changes.

## Relevant Source Context
- `docs/features/css-properties-support.md`
- `docs/work/E1 - CSS animation support.md`
- `docs/work/E1/M4 - CSS transitions.md`

## Requirements
- [ ] Mark transition declarations and static transform support only after the associated tests pass.
- [x] Name supported timing functions and paint-only target subset.
- [x] Document immediate behavior for layout, discrete, incompatible, box-shadow, and scrollbar pseudo-part changes.

## Acceptance Checks
- [x] No unsupported property is marked supported.
- [x] Documentation matches the accepted M4 tests and public behavior.

## Verification
- Review changed checklists against implementation and test evidence.

## Verification Status
Focused core and NanoVG recording tests are present but not executed in this environment: `java`
is absent from `PATH` and `JAVA_HOME` is unset. The support documentation records this explicitly;
the test-pass-dependent requirement remains unchecked.

## Constraints
- Do not commit unless explicitly requested.
