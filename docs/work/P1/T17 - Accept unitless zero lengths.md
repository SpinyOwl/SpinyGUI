# T17 - Accept unitless zero lengths

## Purpose
Restore the main-menu player panel's `margin: 4px 0 10px`, which native rejected because zero was parsed as a number instead of a length.

## Changes
- Retry rejected property values with numeric zeros represented as zero lengths, including shorthand lists.
- Preserve already-valid numeric values and immutable/shared parsed declarations.
- Keep nonzero unitless lengths invalid; do not alter demo CSS to work around the parser.

## Acceptance Checks
- Integer, decimal and signed zeros resolve in margin, padding, width, positioning and border declarations.
- Numeric opacity, z-index and line-height retain their existing semantics; nonzero unitless lengths remain invalid.
- The unchanged main-menu CSS resolves 4 px top and 10 px bottom player-panel margins, restoring the 10 px gap before actions.
- Core checks pass.

## Risks
Global numeric token conversion would break number-valued properties. Normalize only as a validation fallback, then revalidate the converted value before applying it. This does not expand function grammar or fix existing line-height/rasterization differences.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-08
- Implemented Scope: Property validation fallback converts numeric zeros and shorthand lists without mutating parsed terms.
- Relevant Files and Symbols: Property.apply/zeroLengths; UnitlessZeroTest.
- Acceptance Evidence:
  - Zero spellings, numeric/nonzero guards and unknown functions: Passed — Automated — all seven UnitlessZeroTest cases passed in the final core check.
  - Main-menu gap: Passed — Native Host — paired capture run-3250270018513402648 with unchanged demo CSS: browser player bottom 321/actions top 331; native player bottom 322.4/actions top 332.4. Both gaps are 10 px.
  - Full visual parity: Failed — Native Host — 88 geometry differences and 4.59% pixel mismatch remain; text metrics and rendering are separate work.
  - Core check: Passed — Automated — sequential `:spinygui.core:check`, including tests, PMD and SpotBugs. Earlier overlapping Gradle runs corrupted shared test-result files (EOFException / missing in-progress binary); only the final sequential run is acceptance evidence.
- Decisions and Deviations: Additional issue discovered after T13; the missing main-menu gap is independent of typography. User explicitly requested support and previously authorized a separate commit per fix.
- Review Outcome: Not Reviewed
- Remaining Work: Implementation and verification complete; awaiting acceptance review. No additional production changes are required for this bounded fix.
- Resume or Closure: Scoped source/test/doc diff reviewed locally and prepared for the user-authorized separate commit; T15/T16 and final T13 acceptance remain separate.
