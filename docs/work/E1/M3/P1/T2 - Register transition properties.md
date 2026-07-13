# T2 - Register transition properties

## Parent Documents
- Epic: `docs/work/E1 - CSS animation support.md`
- Milestone: `docs/work/E1/M3 - Transition runtime.md`
- Phase: `docs/work/E1/M3/P1 - Parse transition declarations.md`
- Guidance: `AGENTS_CODE_STYLE.md`

## Goal
Expose transition longhands and shorthand through the standard stylesheet and inline-style property path.

## Dependencies
**Depends on:** `P1/T1`.
**Enables:** `P1/T3`.
**Parallelizable with:** None.

## Scope
- In: `transition-property`, `transition-duration`, `transition-delay`, `transition-timing-function`, `transition`, property constants/providers/defaults, and typed resolved-style access.
- Out: animation execution and any grammar changes not required by the existing declaration parser.

## Requirements
- [ ] Register every supported longhand and the shorthand in the property store/provider flow used by stylesheets and inline styles.
- [ ] Expand valid shorthand into the same typed configuration as equivalent longhands.
- [ ] Reject negative durations, malformed cubic-bezier forms, unknown property names, and malformed/partial lists atomically.
- [ ] Preserve the prior valid inline ruleset when an updated inline declaration fails to parse, following existing `StyleManagerImpl` behavior.

## Acceptance Checks
- [ ] Parsed CSS tests prove longhand/shorthand equivalence and CSS initial values.
- [ ] Stylesheet and inline-style tests cover comma-separated declarations and invalid declarations.
- [ ] An invalid declaration never applies a valid prefix or produces a partial transition configuration.

## Verification
- Run `./gradlew.bat :spinygui.core:test --tests *Transition* --tests *StyleManager*`.

## Constraints
- Do not commit unless explicitly requested.
- Change semantic parser/provider code together; do not edit generated ANTLR output directly.
