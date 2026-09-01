# LWJGL application host

`LwjglApplicationHost` is the facade-level production composition for a navigable SpinyGUI
application. It combines the standard frame services, event listeners, frame pipeline, NanoVG
renderer, standalone GLFW window, cbchain callback bridge, and a bounded `FrameNavigator`.

## Standalone owned window

The runnable example demonstrates navigation, Back and Forward, two nested modals, the modal
backdrop, topmost-only interaction, and focus restoration:

```powershell
.\gradlew.bat :spinygui.demo.simple:runNavigationModalHostExample
```

Its entry point is
`com.spinyowl.spinygui.demo.simple.NavigationModalHostExample`. The essential composition is:

```java
Frame initialFrame = new Frame();
try (LwjglApplicationHost host = LwjglApplicationHost.owned(
    LwjglApplicationConfiguration.windowed(760, 520, "My application"),
    initialFrame,
    8,
    lifecycle)) {
  host.run();
}
```

After `owned(...)` returns, the host owns its renderer, standard services, standalone window, and
bridge. `run()` initializes and closes native resources. Calling `close()` before `run()` releases
the already-transferred renderer and services. A failed factory call does not transfer renderer
ownership.

## Navigation and modal semantics

`host.navigator()` is the single navigation boundary. `navigate`, `back`, and `forward` select one
current `Frame`; a new navigation clears forward history. GLFW callbacks capture the frame that is
current when the callback runs, so queued events are not retargeted by later navigation. A change
during the update hook selects the frame prepared and rendered later in that same loop iteration.

Only the current navigation frame is prepared and rendered. Inactive history frames are retained
for navigation but are not rendered underneath the current frame or underneath its modals.

Each frame has its own `TopLayer`. `showModal` promotes an attached element above normal content and
adds the frame-owned backdrop. Normal content stays visible, but is inert for pointer, keyboard,
and focus targeting. Nested modals close in LIFO order; only the topmost modal is interactive, and
closing it restores focus to the previous modal or background target. Modal roots remain in their
normal DOM ownership, so an application should manage their ordinary hidden/visible styling as the
example does.

## Embedded callback chains

An embedded owner keeps its GLFW window and global GLFW lifecycle. It supplies its seven
already-installed cbchain instances and attaches SpinyGUI handlers:

```java
FrameNavigator navigator = new FrameNavigator(initialFrame, 8);
LwjglFrameServices services = new LwjglFrameServices(initialFrame, renderer);
GlfwSystemEventBridge bridge = GlfwSystemEventBridge.attached(
    navigator,
    services.systemEvents(),
    keyPolicy,
    callerOwnedChains);

LwjglCallbackInstaller.Registration registration = bridge.install(existingWindowHandle);
```

`callerOwnedChains` is a `GlfwSystemEventBridge.Chains` containing cursor-position, cursor-enter,
window-size, scroll, mouse-button, character, and key chains. Attached mode adds only the bridge's
handlers. Closing `registration` or `bridge` removes those exact handlers; it does not free the
chains, unset other callbacks, destroy the window, or terminate GLFW.

The embedded window adapter can then be passed to `LwjglApplicationHost.injected(...)` with the
navigator, `services.pipeline()`, renderer, services, and clock. Injected mode initializes and uses
those objects but never closes them. The embedding owner must close the bridge registration,
renderer, services, window/context, and global GLFW lifecycle in the order required by that owner.

## Manual lower-level composition

Applications that need custom services or loop hooks may use `AbstractLwjglApplication` directly:

1. Create a `FrameNavigator`, event processors/listeners, `FramePipeline`, renderer, and
   `LwjglWindow`.
2. Choose `ResourceOwnership.owned()` only when the loop truly owns renderer, window/callbacks, and
   services; otherwise use `ResourceOwnership.injected()` and close them in the embedding owner.
3. Install `GlfwSystemEventBridge.owned(...)` only for a fresh standalone window. Use
   `attached(...)` for caller-owned chains.
4. Resolve the navigator's current frame once per render iteration, after update, and use that same
   frame for sizing, preparation, rendering, and publication.

The lower-level path is intentionally explicit: the application is responsible for callback
installation, Escape/close policy, partial-initialization cleanup, reverse teardown, and keeping
the event processor, pipeline, renderer, and current frame consistent.

## Native smoke checklist

Run the example locally; this check requires a visible native window and is separate from headless
test evidence.

- Navigate to Details, go Back, then Forward; confirm exactly one current frame is visible.
- Return Home, open the first modal, and confirm the backdrop is visible and Home cannot be used.
- Open the nested modal; confirm the first modal is inert. Close it and confirm focus returns to
  `Open nested modal`; close the first and confirm focus returns to `Open modal`.
- Resize the window and confirm current content, backdrop, and modal positioning repaint without
  callback loss.
- Use the dedicated harness below to check representative key-chain coexistence and detachment on a
  real window. Mapping and coexistence for all seven callback-chain types are automated evidence,
  not claims made by this native smoke.
- Close through Escape and the window close control; confirm one orderly teardown with no callback
  after-free or global GLFW termination from an attached bridge.

Run the dedicated embedded-callback smoke separately:

```powershell
.\gradlew.bat :spinygui.demo.simple:runEmbeddedCallbackCoexistenceSmoke
```

The window title is the representative key-chain evidence protocol. Press `C` and confirm both
`caller` and `bridge` become `1`. Press `D`; detachment is deferred until GLFW callback iteration
returns and the title changes to `KEY DETACHED`. Press `C` again and confirm `caller` becomes `2`
while `bridge` remains `1`. Escape or the window close control ends the run. Attached mode requires
a complete chain set, so the demo owns and installs all seven chains even though this native check
observes only the key chain. On exit it closes any still-attached bridge registration, frees the
caller-owned native callbacks, destroys the window, and terminates its own GLFW lifecycle exactly
once.
