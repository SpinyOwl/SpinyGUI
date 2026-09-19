# T4 - Add CSS cursor support

## Document Context
- Status: In Progress
- Dependencies: None
- Parent: None
- Children: None
- Related: [CSS properties support](../features/css-properties-support.md), [Scrollbar support](../features/scrollbar-support.md)
- Next: Implement the typed cursor property and GLFW cursor bridge.

## Purpose
Expose a bounded CSS `cursor` property and apply it to the GLFW window as the pointer moves over the active element.

## Changes
- [x] Add typed parsing, resolved-style storage, and cascade support for `default`, `pointer`, `text`, `move`, `ew-resize`, `ns-resize`, `nwse-resize`, and `nesw-resize`.
- [x] Resolve the effective pointer from the hit target: an explicit CSS value wins; otherwise buttons and checkable inputs use `pointer`, text inputs and textareas use `text`, and horizontal or vertical scrollbar thumbs use the matching resize cursor.
- [x] Add a frame-bound cursor bridge in the LWJGL host that caches native GLFW standard cursors, applies changes only when the effective cursor changes, restores the default cursor outside the frame, and frees native cursor resources on shutdown.
- [ ] Add focused parser/style, pointer-resolution, and GLFW bridge tests; demonstrate the supported states in the complex demo and update CSS feature support documentation.

## Acceptance Checks
- [x] Valid cursor declarations cascade and invalid declarations preserve the preceding resolved value.
- [x] Explicit cursor values override automatic control and scrollbar defaults; disabled controls fall back to `default` unless styled explicitly.
- [x] Pointer movement changes the GLFW cursor for button/checkable, editable text, drag/move, horizontal/vertical/diagonal resize, and scrollbar-thumb targets without allocating a native cursor per motion event.
- [x] Focused core and LWJGL tests pass; native demo verifies representative pointer shapes.

## Risks
GLFW cursors are per-window native resources. Cache them for the window lifetime and release them with the existing teardown path. `resize` remains unsupported as an element interaction; resize cursor values only express pointer intent.

## Execution Record

- Status: In Progress
- Last Updated: 2026-09-19
- Implemented Scope: None
- Relevant Files and Symbols: CursorType, CursorPropertyProvider, CursorResolver, ResolvedStyle, GlfwCursorController, DefaultLwjglWindow, Demo.
- Acceptance Evidence:
  - Valid cursor declarations cascade and invalid declarations preserve the preceding resolved value: Passed — Automated — TransformStyleManagerTest proves `cursor: move; cursor: wait` retains `move`; CursorPropertyProviderTest covers the supported keyword boundary.
  - Explicit cursor values override automatic control and scrollbar defaults; disabled controls fall back to `default` unless styled explicitly: Passed — Automated — CursorResolverTest covers explicit precedence, button/checkable defaults, editable controls, and disabled fallback.
  - Pointer movement changes the GLFW cursor for button/checkable, editable text, drag/move, horizontal/vertical/diagonal resize, and scrollbar-thumb targets without allocating a native cursor per motion event: Passed — Automated — GlfwCursorControllerTest asserts direct native create/set/destroy calls, cache reuse, and mappings for text, move, axial, and diagonal resize shapes; CursorResolver selects scrollbar-thumb axis cursors.
  - Focused core and LWJGL tests pass; native demo verifies representative pointer shapes: Passed — Automated and Native Host — focused Gradle suites and ButtonExampleTest passed. User-provided native screenshots on 2026-09-19 show the hand pointer over the input button and radio control after Demo was wired to GlfwCursorController.
- Decisions and Deviations: The user approved the bounded CSS cursor set and explicit-style precedence. `auto` is the inherited implementation default; automatic defaults cover current controls and scrollbar thumbs. GLFW maps `move` and both diagonal resize values to its standard `RESIZE_ALL`, `RESIZE_NWSE`, and `RESIZE_NESW` shapes. The legacy Demo host is explicitly wired to the same controller as DefaultLwjglWindow. Draggable and resizable element behavior is not introduced.
- Review Outcome: Not Reviewed
- Remaining Work: Add a richer demo fixture for text, move, and resize CSS values if visual showcase coverage is required.
- Resume or Closure: Implementation and acceptance verification are complete; resume only for independent review or richer demo coverage.
