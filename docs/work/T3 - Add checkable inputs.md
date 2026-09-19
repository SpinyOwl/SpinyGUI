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
- [x] Mouse and Space toggle a checkbox; selecting a radio clears only named peers in the same frame; arrow keys cycle the selected radio.
- [x] Disabled controls cannot change; each actual state transition emits one `ChangeEvent` alongside its activation.
- [x] Checkboxes and radios have usable auto geometry and their NanoVG renderer draws the correct checked/unchecked indicator.
- [x] Focused core and NanoVG tests, PMD, and SpotBugs pass.

## Risks
HTML form submission, labels, indeterminate checkboxes, and browser-complete keyboard navigation are deferred. Radio grouping is intentionally `Frame`-local because form ownership is not modeled.

## Execution Record
- Status: In Progress
- Last Updated: 2026-09-19
- Implemented Scope: Added frame-local checkable state transitions, `ChangeEvent` and XML `on-change`, `:checked`, automatic geometry, NanoVG indicators, focused tests, demo resources, and support documentation.
- Relevant Files and Symbols: InputElement, CheckableInputBehavior, SystemMouseClickEventListener, SystemKeyEventListener, CheckedSelector, BlockLayout, NvgInputRenderer, ButtonExample.
- Acceptance Evidence:
  - Checkable model and selector: Passed — Automated — CheckableInputBehaviorTest, CheckedSelectorTest, and DefaultNodeParserTest passed.
  - State-changing input: Passed — Automated — direct `SystemMouseClickEventListenerTest` covers checkbox release, `SystemKeyEventListenerTest` covers checkbox Space and same-name radio Right navigation; `CheckableInputBehaviorTest` covers disabled, grouping, and wrapping semantics.
  - Layout and rendering: Passed — Automated — direct `BlockLayoutTest` confirms 16 px content and 20 px border-box auto geometry with borders; `NvgInputRendererTest` confirms checkbox/radio type, checked state, and disabled state reach the native-indicator rendering path.
  - Focused direct regression: Passed — Automated — `gradlew.bat :spinygui.core:test --tests "*SystemMouseClickEventListenerTest" --tests "*SystemKeyEventListenerTest" --tests "*BlockLayoutTest" :spinygui.core.backend.lwjgl.nanovg:test --tests "*NvgInputRendererTest"` completed successfully (86 core tests and NanoVG renderer tests).
  - Full affected-module regression: Passed — Automated — `gradlew.bat :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui.demo.complex:test --rerun-tasks --console=plain --no-configuration-cache` completed with 100 core, 28 NanoVG, and 3 demo XML result reports; no failures or errors.
  - Static analysis: Passed — Automated — `:spinygui.core:pmdMain` and `:spinygui.core:spotbugsMain` passed.
- Decisions and Deviations: Radio group membership is a matching nonblank `name` among descendants of the owning Frame, as approved.
- Review Outcome: Not Reviewed
- Remaining Work: Perform the native-window manual demo check. `:spinygui.demo.complex:runButtonExample` was launched on 2026-09-19, but this session's computer-use adapter reported no available native app surfaces (`apps: []`), so click and keyboard outcomes could not be observed or claimed.
- Resume or Closure: Direct automated verification is complete. Resume from an environment with a native app surface and exercise checkbox click/Space plus radio click/arrow navigation in ButtonExample.
