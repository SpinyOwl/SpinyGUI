# T2 - Add flex shorthands

## Purpose
Allow stylesheets to express flex sizing and flow with `flex` and `flex-flow`.

## Changes
Expand the shorthands into existing typed longhands, preserving declaration order,
resetting omitted components, and supporting explicit initial values and inheritance.
Keep existing layout algorithms and absent longhand defaults.

## Acceptance Checks
- Numeric, auto, none, length and percentage flex forms resolve correctly.
- Flow accepts direction and wrapping in either order and resets omitted components.
- Invalid declarations preserve earlier values, including ambiguous numeric zeros.
- Longhands can override shorthand components and explicit inheritance copies them.
- Parsed declarations produce proportional widths and wrapping through Yoga.
- Focused tests, core PMD and core SpotBugs pass.

## Risks
Intrinsic basis keywords and functions remain unsupported. SpinyGUI's existing absent
`flex-shrink: 0` default remains unchanged; explicit `flex: initial` uses `0 1 auto`.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-09
- Implemented Scope: Registered both shorthands, validated complete expansions before mutation, handled numeric zeros and explicit inheritance, documented supported forms.
- Relevant Files and Symbols: FlexPropertyProvider, Properties.FLEX, Properties.FLEX_FLOW, FlexStyleManagerTest, FlexInlineBlockLayoutTest.parsedShorthandsDriveYogaSizingAndWrapping, docs/features/css-properties-support.md.
- Acceptance Evidence: Passed — Automated — `gradlew.bat :spinygui.core:test --tests '*FlexStyleManagerTest' --tests '*FlexInlineBlockLayoutTest' :spinygui.core:pmdMain :spinygui.core:spotbugsMain --console=plain --no-configuration-cache`: 55 tests, no failures/errors/skips; PMD and SpotBugs passed. `git diff --check` passed. The full core test attempt was interrupted after detecting the integer-longhand failure; final verification is focused, not an aggregate gate. Native/manual visual acceptance was not run; geometry is covered by the Yoga integration regression.
- Decisions and Deviations: Existing grow/shrink longhands accepted only TermFloat; added TermInteger support and nonnegative finite validation so integer longhands can override shorthand components. Omitted basis uses specification-defined 0px. No layout algorithm changes.
- Review Outcome: Not Reviewed
- Remaining Work: No implementation blockers; independent review not requested or performed.
- Resume or Closure: Implementation and focused verification complete. Self-review found no scope-alignment issues. User requested committing this scoped change.
