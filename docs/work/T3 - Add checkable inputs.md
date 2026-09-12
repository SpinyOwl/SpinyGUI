# T3 - Add checkable inputs

## Document Context
- Status: In Progress
- Dependencies: None
- Parent: None
- Children: None
- Related: [Input Button Support](../features/input-button-support.md)
- Next: Inspect the input node, system event listeners, layout, and NanoVG renderer.

## Purpose
Add first-class checkbox and radio inputs using the existing `InputElement` type-specific model.

## Changes
- [x] Add persisted checked state, `checkboxInput()`/`radioInput()` predicates, and a `:checked` selector.
- [x] Toggle checkboxes and select radios from mouse and keyboard input; group radios with the same nonblank `name` within one `Frame`; emit action and change events.
- [x] Give checkable inputs default geometry, render checkmarks and radio dots through NanoVG, add demo coverage, tests, and public support documentation.

## Acceptance Checks
- [x] XML and `NodeBuilder` inputs preserve an initial `checked` state and CSS `:checked` follows programmatic changes.
- [ ] Mouse and Space toggle a checkbox; selecting a radio clears only named peers in the same frame; arrow keys cycle the selected radio.
- [ ] Disabled controls cannot change; each actual state transition emits one `ChangeEvent` alongside its activation.
- [ ] Checkboxes and radios have usable auto geometry and their NanoVG renderer draws the correct checked/unchecked indicator.
- [x] Focused core and NanoVG tests, PMD, and SpotBugs pass.

## Risks
HTML form submission, labels, indeterminate checkboxes, and browser-complete keyboard navigation are deferred. Radio grouping is intentionally `Frame`-local because form ownership is not modeled.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-12
- Implemented Scope: Added frame-local checkable state transitions, `ChangeEvent` and XML `on-change`, `:checked`, automatic geometry, NanoVG indicators, focused tests, demo resources, and support documentation.
- Relevant Files and Symbols: InputElement, CheckableInputBehavior, SystemMouseClickEventListener, SystemKeyEventListener, CheckedSelector, BlockLayout, NvgInputRenderer, ButtonExample.
- Acceptance Evidence:
  - Checkable model and selector: Passed — Automated — CheckableInputBehaviorTest, CheckedSelectorTest, and DefaultNodeParserTest passed.
  - State-changing input: Partially Passed — Automated — CheckableInputBehaviorTest covers toggle, group clearing, wrapping and disabled state; direct system-listener event assertions remain unrun.
  - Layout and rendering: Partially Passed — Automated — NanoVG input renderer compilation and existing NvgInputRendererTest passed; direct checkable drawing and layout assertions remain unrun.
  - Full affected-module regression: Passed — Automated — `gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:test --rerun-tasks --console=plain --no-configuration-cache` completed with 100 core, 28 NanoVG, and 3 demo XML result reports; no failures or errors.
  - Static analysis: Passed — Automated — `:spinygui.core:pmdMain` and `:spinygui.core:spotbugsMain` passed.
- Decisions and Deviations: Radio group membership is a matching nonblank `name` among descendants of the owning Frame, as approved.
- Review Outcome: Not Reviewed
- Remaining Work: Add direct listener, layout, and checkable-renderer tests; manually exercise the ButtonExample controls in a native window.
- Resume or Closure: Implementation is ready for review. Resume by adding direct system-listener tests for release, Space and radio arrow changes.
