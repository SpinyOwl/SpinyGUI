# E2/M4/P1: Compose Navigation and Top-Layer Host

## Document Context

- Status: Planned
- Dependencies: E2/M3
- Parent: [M4 - Add navigable LWJGL application host](../M4%20-%20Add%20navigable%20LWJGL%20application%20host.md)
- Children: None
- Related: [LWJGL application host navigation and top layer](../../../design/lwjgl-application-host-navigation-and-top-layer.md)
- Next: None

## Goal

Implement the approved browser-like navigation and modal model, chain-aware GLFW event bridge, and
high-level LWJGL host as small reviewable changes that preserve the existing single-frame APIs.

## Non-Goals

- Making the high-level host mandatory for custom applications.
- Treating modals as frames or rendering navigation-history frames below the current frame.
- Adding popover, fullscreen, arbitrary portal, multi-window, or retained-rendering systems.
- Creating a new module or changing renderer SPI without a concrete blocker.

## Context

- `FramePipeline.prepareFrame(Frame)` already accepts the frame at the preparation boundary, but
  `AbstractLwjglApplication` stores one fixed frame.
- `DefaultLwjglWindow` currently combines standalone GLFW ownership, callback installation, event
  mapping, and a fixed frame target.
- `LwjglFrameServices.listeners(...)` is the existing standard composition source and must not be
  copied into another host.
- `spinygui` is the existing facade with core, LWJGL/NanoVG, and cbchain dependencies.

## Assumptions and Open Questions

- Assumption: navigation changes occur between input batches; events retain the frame that was
  current when their GLFW callback captured them.
- Assumption: the first `TopLayer` implementation supports modal entries only, with LIFO close and
  topmost interaction semantics.
- Assumption: the standalone window may own GLFW globally; embedded callback bridges do not.

## Phase Tasks

Tasks appear in recommended implementation order. Prerequisites are listed only where a later task
cannot be implemented safely before an earlier contract exists.

### T1: Add frame navigation

**Purpose:** Represent browser-like current-frame navigation without overloading modal semantics.

**Changes:**

- [ ] Add `FrameNavigator` with a non-null current frame and bounded `navigate`, `back`, `forward`,
  `canGoBack`, and `canGoForward` behavior.
- [ ] Clear forward history after a new navigation and prevent no-op navigation from corrupting
  history.
- [ ] Keep services, renderer objects, and GLFW resources out of frames and navigation entries.
- [ ] Add deterministic history and current-frame tests, including navigation during host update.

**Acceptance Checks:**

- [ ] Navigation tests prove current, back, forward, forward-history clearing, and boundary no-ops.
- [ ] Static inspection proves `Frame` has no service, renderer, navigator, or GLFW ownership.

**Risks:** Unbounded history retention. Start with an explicit finite bound or caller-configured
capacity and test eviction; do not retain an unlimited sequence of frames.

### T2: Add the modal top layer

**Purpose:** Provide browser-like modal presentation and interaction within the active frame.

**Prerequisites:** T1 must establish the distinction between navigation frames and modal content.

**Changes:**

- [ ] Add a frame-owned `TopLayer` with ordered modal entries, backdrop presentation, LIFO close,
  and source invalidation on mutations.
- [ ] Integrate top-layer ordering with layout, transform, and rendering so normal content remains
  visible below the backdrop and modal roots.
- [ ] Centralize inert-background, topmost hit-testing, keyboard admission, and focus restoration at
  shared input/focus boundaries.
- [ ] Add nested-modal, pointer, keyboard, focus, close-order, and invalidation tests.

**Acceptance Checks:**

- [ ] Structural rendering tests prove `content -> backdrop -> modal` order and ordered nested
  modals.
- [ ] Input tests prove no pointer, keyboard, or focus event reaches normal content while a modal is
  active.
- [ ] Closing the topmost modal restores the previous modal or normal frame interaction target.
- [ ] Equivalent/no-op top-layer operations do not create spurious source revisions.

**Risks:** Duplicating modal checks across listeners can produce inconsistent behavior. Stop and
revise if the design cannot enforce inertness through shared hit-testing and focus boundaries.

### T3: Extract standard system-event listener composition

**Purpose:** Let both current services and the new host use one authoritative standard listener set.

**Changes:**

- [ ] Extract the private `LwjglFrameServices.listeners(...)` composition into
  `DefaultSystemEventListeners` or an equivalently narrow factory.
- [ ] Preserve shared `MouseServiceImpl`, `ScrollbarInteraction`, text-layout service, keyboard,
  clipboard, time, and GUI-event dependencies across the listener set.
- [ ] Make `LwjglFrameServices` consume the extracted composition without changing existing public
  behavior.
- [ ] Add a composition contract test covering all supported `SystemEvent` types and shared-service
  identity where behavior depends on it.

**Acceptance Checks:**

- [ ] The default provider contains exactly one standard listener for every currently supported
  system-event type.
- [ ] Existing listener and `LwjglFrameServices` tests pass without a duplicated configuration path.

**Risks:** Accidentally constructing separate mouse, scrollbar, or text-layout state for different
listeners can change interaction behavior; identity-sensitive tests must guard the composition.

### T4: Add the cbchain GLFW event bridge

**Purpose:** Reuse one event-mapping implementation while allowing application and engine callbacks
to coexist.

**Prerequisites:** T1 provides the active frame target; T3 provides the destination listener
composition.

**Changes:**

- [ ] Extract GLFW action, button, modifier, and typed-event mapping from `DefaultLwjglWindow` into
  `GlfwSystemEventBridge`.
- [ ] Add a narrow callback-installation strategy in the LWJGL backend, inject it into
  `DefaultLwjglWindow`, and have the facade bridge implement it without introducing a
  backend-to-facade dependency.
- [ ] Register bridge handlers through cbchain and support caller-provided chains for embedded hosts.
- [ ] Resolve `FrameNavigator.currentFrame()` when capturing each event and push it to the existing
  `SystemEventProcessor` queue.
- [ ] Separate Escape-to-close behavior into explicit host policy.
- [ ] Track callback ownership so close detaches or frees only bridge-owned registrations and never
  terminates GLFW.
- [ ] Declare the facade's cbchain JPMS dependency explicitly while keeping cbchain out of the
  lower-level LWJGL backend module.
- [ ] Add fake/injected callback tests for value mapping, active-frame targeting, coexistence,
  unsupported values, and teardown.

**Acceptance Checks:**

- [ ] Every currently supported GLFW callback maps to the same `SystemEvent` values as the existing
  standalone implementation.
- [ ] Switching navigation frames between callback captures targets subsequent events to the new
  current frame without retargeting already queued events.
- [ ] Closing an embedded bridge leaves caller-owned callback chains usable and does not call global
  GLFW termination.
- [ ] Module compilation proves the backend does not depend on the facade or cbchain.

**Risks:** cbchain and JPMS access requirements may leak into lower-level backend consumers. Keep
the cbchain-aware implementation in the existing facade and the injected installer contract in the
LWJGL backend.

### T5: Compose the navigable application host

**Purpose:** Provide the requested production convenience component without removing lower-level
integration paths.

**Prerequisites:** T1, T3, and T4 define the navigation, listener, and callback boundaries; T2
defines active-frame modal behavior.

**Changes:**

- [ ] Add `LwjglApplicationHost` in the `spinygui` facade module to compose the window, event bridge,
  navigator, standard services, pipeline, and renderer.
- [ ] Update the reusable loop to read the current frame for size synchronization, preparation,
  rendering, and rendered-publication checks on every iteration.
- [ ] Preserve existing single-frame application constructors through a one-frame navigator or
  compatibility composition.
- [ ] Keep `DefaultLwjglWindow` as the concrete standalone `LwjglWindow` owner while delegating event
  registration/mapping to the bridge where compatible with callback ownership.
- [ ] Prove reverse teardown and partial-initialization failure behavior for window, callbacks,
  renderer, services, and navigator-owned frames.

**Acceptance Checks:**

- [ ] A deterministic host test proves `poll -> input -> update -> current-frame size sync -> prepare
  -> render -> publish -> swap` across navigation.
- [ ] Navigation during update renders the new current frame and never publishes the previous frame
  as current.
- [ ] Existing `NvgLwjglApplication` examples compile and their single-frame lifecycle tests remain
  green.
- [ ] Partial initialization and callback-install failures close each owned resource exactly once in
  reverse order without closing injected resources.

**Risks:** Retrofitting current constructors can create ambiguous ownership. Preserve explicit
owned-versus-injected paths and reject configurations whose ownership cannot be determined.

### T6: Publish examples and verification evidence

**Purpose:** Make standalone and embedded adoption understandable and verify user-visible native
behavior separately from headless contracts.

**Prerequisites:** T5 provides the complete host path.

**Changes:**

- [ ] Add or adapt a small example demonstrating navigation, back/forward, opening nested modals,
  backdrop behavior, and focus restoration through the production host.
- [ ] Document standalone window ownership, embedded callback-chain injection, manual composition,
  navigation semantics, modal semantics, and teardown responsibilities.
- [ ] Run focused suites, affected module suites, static analysis, and the full build.
- [ ] Perform a native smoke check for callback coexistence, navigation, modal focus/inertness,
  resizing, and close behavior; record native evidence separately from automated evidence.

**Acceptance Checks:**

- [ ] Documentation contains runnable standalone and embedded integration paths without claiming
  that inactive frames render beneath modals.
- [ ] `:spinygui.core:test`, `:spinygui.core.backend.lwjgl.nanovg:test`, `:spinygui:test`, simple and
  complex demo checks, PMD, and SpotBugs pass.
- [ ] The full Gradle build passes or an environmental blocker is recorded with exact scope.
- [ ] The opt-in `:spinygui.benchmark:test` suite passes or an unrelated benchmark blocker is
  recorded separately.
- [ ] Native smoke evidence explicitly reports navigation, modal input/focus behavior, callback
  coexistence, and cleanup observations.

**Risks:** Headless tests cannot prove native callback interoperability or visible modal stacking;
do not promote automated structural evidence to native acceptance.

## Verification Strategy

- Run focused navigation, top-layer, listener-composition, callback-bridge, and host-lifecycle tests
  after their owning tasks.
- Run `./gradlew :spinygui.core:test :spinygui.core.backend.lwjgl.nanovg:test :spinygui:test`.
- Run simple and complex demo compilation/tests, `staticAnalysis`, and the full `build`.
- Run the opt-in `./gradlew :spinygui.benchmark:test` suite explicitly because the root build does
  not execute it.
- Perform the native smoke check only after the automated ownership and failure-path suites pass.
- Request independent review for modal input/focus correctness and callback/resource ownership.

## Review Boundaries

- Commit core `TopLayer` behavior separately from LWJGL/cbchain integration.
- Keep `FrameNavigator` and standard listener extraction reviewable before the callback bridge.
- Keep callback ownership/event mapping separate from final host composition and examples.
- Do not absorb unrelated renderer SPI, multi-window, or E7 backend work.

## Deferred Work

- Popovers, fullscreen promotion, arbitrary top-layer portals, and non-modal overlays.
- Multi-window navigation and cross-window frame transfer.
- Rendering navigation-history frames or retained background surfaces.
- A new runtime/host module, pending a second concrete consumer that cannot use the facade module.
