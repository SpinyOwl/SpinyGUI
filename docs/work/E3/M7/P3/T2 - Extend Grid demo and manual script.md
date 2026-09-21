# T2 - Extend Grid demo and manual script

## Document Context

- Status: Blocked
- Dependencies: P3/T1
- Parent: [P3 - Prove complex Grid panels](../P3%20-%20Prove%20complex%20Grid%20panels.md)
- Children: None
- Related: spinygui.demo.complex
- Next: Extend the demo after automated fixture coverage exists.

## Purpose

Make delivered Grid behavior observable in a native window.

## Changes

- [x] Add one compact panel covering container alignment, mixed intrinsic/flexible tracks, nested controls, and scrolling.
- [x] Document exact resize, pointer, scroll, and clipping checks.

## Acceptance Checks

- [x] The demo compiles.
- [ ] A native manual pass verifies the documented scenarios.

## Risks

Keep the demo inside the delivered subset; it must not imply support for Level 2 features.

## Supplementary OpenGL Screenshot Test

**Purpose:** Add one separate native OpenGL capture test for the initial M7 Grid demo state.

**Scope:** Reuse the existing spinygui.visual-tests native capture pipeline. The test must render the
Grid demo's XML and CSS, capture the NanoVG/OpenGL framebuffer, and assert a stable, local
structural image property for the M7 panel. It must not weaken existing comparison tolerances, add
a second capture framework, or claim click, wheel, drag, focus, or resize interaction evidence.

**Acceptance Checks:**

- [x] A focused visual-test command produces a native OpenGL capture for the M7 initial state.
- [x] The assertion fails if the M7 panel's rendered extent, clipping, or visible controls disappear.
- [x] The task record distinguishes this screenshot evidence from the remaining manual interaction script.

**Risks:** Native graphics capture needs an OpenGL context. A capture failure is evidence of an
unavailable graphics environment, not a passing skip.

## Native Manual Script

1. Run `./gradlew.bat :spinygui.demo.complex:runGridStyleExample --no-configuration-cache` and open the 720 by 560 `Grid Style Example` window.
2. Keep the window height at 560 px, then drag its left or right edge to resize its width from 640 px to 1,000 px and back to 720 px. Confirm the lower `M7 aligned intrinsic panel` changes width with the window while its outer border and `auto`, fractional, and `minmax` tracks remain inside that border with distributed unused space.
3. Hover and click the checkbox and `Apply` button. Confirm each pointer targets its control; toggle the checkbox and confirm its checked indicator changes without moving neighbouring cells.
4. Place the pointer over the right-most panel cell. Roll the wheel down three notches and then up three notches to move its vertical scrollbar. Drag the horizontal scrollbar thumb to the right edge and then back to the left edge. Confirm content moves only inside that cell and remains clipped by its border.
5. Confirm the long summary text does not paint outside its left cell before or after resizing. Close the window with Escape or the native close control.

## Execution Record

- Status: Blocked
- Last Updated: 2026-09-21
- Implemented Scope: Extended `GridStyleExample` from 720 by 460 to 720 by 560 and added a compact M7 Grid panel with responsive 88% width; `auto`, fractional, and `minmax` columns; `space-evenly` content alignment; nested checkbox and button controls; clipped summary text; and a two-axis scroll cell. Added a separate native OpenGL initial-state capture command that reuses `NativeCaptureMain` and asserts the rendered panel extent, visible controls, and scroll-content clipping.
- Relevant Files and Symbols: `spinygui.demo.complex/src/main/java/com/spinyowl/spinygui/demo/complex/GridStyleExample.java` (`validateGridStyleContract`); `spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/grid-style-demo.xml`; `spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/grid-style-demo.css`; `spinygui.demo.complex/src/test/java/com/spinyowl/spinygui/demo/complex/GridStyleExampleTest.java`; `spinygui.visual-tests/src/main/java/com/spinyowl/spinygui/visual/M7GridPanelScreenshotMain.java`; `spinygui.visual-tests/build.gradle.kts` (`captureM7GridPanel`); this manual script.
- Acceptance Evidence:
  - Complex demo compilation: Passed — Automated — `./gradlew.bat :spinygui.demo.complex:classes --no-configuration-cache` completed successfully.
  - Focused demo resource test: Passed — Automated — `./gradlew.bat :spinygui.demo.complex:test --tests "*GridStyleExampleTest" --no-configuration-cache --console=plain` completed successfully after T4 repaired module-local test-output isolation. The test recalculates the returned GridStyleExample frame with the same property store and parser before asserting the resolved M7 Grid styles, controls, and exact typed column grammar: `AUTO`, `Flexible(0.25fr)`, and `MinMax(Fixed(96px), Flexible(0.25fr))`.
  - Supplementary OpenGL screenshot: Passed — Automated Native OpenGL — `./gradlew.bat :spinygui.visual-tests:captureM7GridPanel --no-configuration-cache` produced `spinygui.visual-tests/build/reports/m7-grid-panel-capture/native.png`, `native-geometry.json`, and `m7-panel-assertion.json`. The capture explicitly recreates GridStyleExample's 720 by 560 initial viewport; the assertion found the full teal panel border, Apply button, checkbox, and 3,060 clipped scroll-content pixels. Capture used NVIDIA GeForce GTX 1060 OpenGL 3.2.
  - Native demo initial state: Passed — Native Host — user-provided screenshot on 2026-09-19 shows the 720 by 560 Grid Style Demo with the M7 panel, clipped summary, checkbox, Apply button, and horizontal scroll affordance visible.
  - Native demo resize and clipping: Passed — Native Host — user-provided screenshots on 2026-09-21 show the M7 panel before and after a wider native-window size. The panel expands horizontally, its tracks redistribute, and the summary and scroll cell remain clipped within their borders.
  - Native wheel scrolling and clipping: Passed — Native Host — user-provided screenshot on 2026-09-21 shows a later text fragment in the right scroll cell than the initial state, while the content remains within the cell border. The visible vertical scrollbar position changes consistently with that movement.
  - Native checkbox state transition and scrollbar drag: Blocked — Native Host — the supplied screenshot shows the pointer over the checkbox, but not a checked-state indicator or before/after transition; it also does not establish a draggable scrollbar outcome. CUA currently reports no native app surfaces for those interactions.
- Decisions and Deviations: The compact panel extends the existing Grid demo instead of introducing a separate launcher. Its 88% width responds to horizontal window resizing within the documented 640–1,000 px range; its fixed height keeps the deliberately overflowing scroll fixture stable. Fractional tracks total less than one, leaving observable free space for `justify-content: space-evenly`; fixed-height scroll content exceeds the final cell in both axes. No Grid Level 2 grammar was used. The focused resource test intentionally asserts the parsed track model directly rather than only its visual result. The supplementary command starts the existing isolated `NativeCaptureMain` process; it adds no comparison path or tolerance. It recreates the 720 by 560 GridStyleExample viewport from the shared XML/CSS input. Its local PNG assertions use exact M7 colors and captured native bounds to prove that the panel frame spans its rendered extent, the controls remain visible, and the unique scroll-content fill remains inside the scroll box.
- Review Outcome: Accepted for automated scope — independent re-review accepted the exact typed Grid track assertions and the 720 by 560 OpenGL capture. Manual interaction evidence remains separately blocked.
- Remaining Work: Record the checkbox state transition and scrollbar drag in a usable Windows UI session, then start P3/T3.
- Resume or Closure: Automated scope, native resize/clipping, and wheel scrolling accepted 2026-09-21. Resume with the remaining checkbox and scrollbar-drag interactions from the recorded GridStyleExample manual script.
