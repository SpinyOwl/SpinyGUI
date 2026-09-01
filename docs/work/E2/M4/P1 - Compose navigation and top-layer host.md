# E2/M4/P1: Compose Navigation and Top-Layer Host

## Document Context

- Status: Complete with verification caveat
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

- [x] Add `FrameNavigator` with a non-null current frame and bounded `navigate`, `back`, `forward`,
  `canGoBack`, and `canGoForward` behavior.
- [x] Clear forward history after a new navigation and prevent no-op navigation from corrupting
  history.
- [x] Keep services, renderer objects, and GLFW resources out of frames and navigation entries.
- [x] Add deterministic history and current-frame tests, including navigation during host update.

**Acceptance Checks:**

- [x] Navigation tests prove current, back, forward, forward-history clearing, and boundary no-ops.
- [x] Static inspection proves `Frame` has no service, renderer, navigator, or GLFW ownership.

**Risks:** Unbounded history retention. Start with an explicit finite bound or caller-configured
capacity and test eviction; do not retain an unlimited sequence of frames.

#### Execution Record

- Status: Completed
- Last Updated: 2026-08-31
- Implemented Scope: Added a core-only bounded browser-like frame navigator with non-null current-frame targeting, identity-based no-op navigation, back/forward traversal, forward-history clearing, oldest-entry eviction, and deterministic coverage including navigation from a simulated host update callback
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/FrameNavigator.java` (`FrameNavigator`); `spinygui.core/src/test/java/com/spinyowl/spinygui/core/FrameNavigatorTest.java` (`FrameNavigatorTest`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Frame.java` (ownership boundary inspected)
- Acceptance Evidence:
  - Navigation behavior: Verified — Automated — `./gradlew --no-daemon :spinygui.core:test --tests com.spinyowl.spinygui.core.FrameNavigatorTest --rerun-tasks --console=plain` passed all 6 focused tests
  - Frame ownership boundary: Verified — Documentation — direct inspection of `Frame` found no service, renderer, navigator, or GLFW fields or references; navigation entries retain only `Frame` instances
- Decisions and Deviations: Required an explicit positive caller-configured capacity; treated navigation to the identical current `Frame` instance as a no-op; synchronized navigator operations so current-frame lookup and history mutation have one coherent boundary; covered navigation during update with a simulated host-update callback without expanding into T5 host code
- Review Outcome: Accepted — independent review of base `6d02a25d` and the recorded T1 source/test identities found no findings; focused artifact evidence and `git diff --check` were consistent
- Remaining Work: None
- Resume or Closure: T1 accepted; proceed to T2 while retaining actual loop navigation coverage for T5

### T2: Add the modal top layer

**Purpose:** Provide browser-like modal presentation and interaction within the active frame.

**Prerequisites:** T1 must establish the distinction between navigation frames and modal content.

**Changes:**

- [x] Add a frame-owned `TopLayer` with ordered modal entries, backdrop presentation, LIFO close,
  and source invalidation on mutations.
- [x] Integrate top-layer ordering with layout, transform, and rendering so normal content remains
  visible below the backdrop and modal roots.
- [x] Centralize inert-background, topmost hit-testing, keyboard admission, and focus restoration at
  shared input/focus boundaries.
- [x] Add nested-modal, pointer, keyboard, focus, close-order, and invalidation tests.

**Acceptance Checks:**

- [x] Structural rendering tests prove `content -> backdrop -> modal` order and ordered nested
  modals.
- [x] Input tests prove no pointer, keyboard, or focus event reaches normal content while a modal is
  active.
- [x] Closing the topmost modal restores the previous modal or normal frame interaction target.
- [x] Equivalent/no-op top-layer operations do not create spurious source revisions.

**Risks:** Duplicating modal checks across listeners can produce inconsistent behavior. Stop and
revise if the design cannot enforce inertness through shared hit-testing and focus boundaries.

#### Execution Record

- Status: Completed
- Last Updated: 2026-08-31
- Implemented Scope: Added frame-owned modal state with a styled backdrop, ordered attached modal roots, LIFO close, focus restoration, source invalidation, shared topmost pointer and focused-element admission, NanoVG paint promotion, active background-scrollbar capture cancellation, automatic reconciliation when an open modal is detached or transferred, and promoted hit testing that escapes the same ancestor clipping removed by rendering
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/TopLayer.java` (`TopLayer`, detached-root reconciliation); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/node/Frame.java` (`topLayer`, `getFocusedElement`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/util/NodeUtilities.java` (modal-aware hit-test root and clipping boundary); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/system/input/ScrollbarInteraction.java` (`draggedElement`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/SystemCursorPosEventListener.java` (modal drag gate); `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/nanovg/NvgRenderer.java` (`renderLayoutTree`); focused core and NanoVG tests
- Acceptance Evidence:
  - Structural modal rendering order: Verified — Automated — final-diff focused NanoVG transform-state suite passed 7 tests, including exact `content -> backdrop -> first modal -> second modal` paint order
  - Background input inertness: Verified — Automated — final-diff focused core suites passed 60 tests, including explicit pointer, keyboard, character, focus, and captured-background-scrollbar cases against the topmost modal
  - Modal close and focus restoration: Verified — Automated — `TopLayerTest` passed nested LIFO restoration plus detached/transferred modal reconciliation and background-focus restoration
  - Top-layer revision stability: Verified — Automated — `TopLayerTest` passed effective open/close invalidation and repeated-show/empty-close no-op revision checks; reconciliation mutations also invalidate paint
- Decisions and Deviations: Modal roots must initially belong to the owning frame and retain their DOM parent; later detach or cross-frame transfer is reconciled on the next top-layer/render/input/focus boundary, bridging saved focus and removing the backdrop when the stack empties. NanoVG skips promoted roots during normal traversal and paints backdrop plus modal roots outside ancestor content state; topmost-modal hit testing now ignores clipping ancestors above that promoted root while retaining clipping inside it. Active scrollbar capture is admitted through the same `TopLayer.allowsInteraction` boundary and cancelled before drag mutation when it belongs to inert background content. The first review-fix test invocation exposed missing imports in the new keyboard test; after correction, the forced focused run and a final-diff focused rerun both passed all 67 selected tests with existing Windows permission and deprecation warnings.
- Review Outcome: Accepted — independent re-review confirmed all four findings resolved on the final source identities; 60 core and 7 NanoVG focused tests passed with current-diff artifacts
- Remaining Work: None
- Resume or Closure: T2 accepted; proceed to T3 while retaining broader affected-module and native verification for T6

### T3: Extract standard system-event listener composition

**Purpose:** Let both current services and the new host use one authoritative standard listener set.

**Changes:**

- [x] Extract the private `LwjglFrameServices.listeners(...)` composition into
  `DefaultSystemEventListeners` or an equivalently narrow factory.
- [x] Preserve shared `MouseServiceImpl`, `ScrollbarInteraction`, text-layout service, keyboard,
  clipboard, time, and GUI-event dependencies across the listener set.
- [x] Make `LwjglFrameServices` consume the extracted composition without changing existing public
  behavior.
- [x] Add a composition contract test covering all supported `SystemEvent` types and shared-service
  identity where behavior depends on it.

**Acceptance Checks:**

- [x] The default provider contains exactly one standard listener for every currently supported
  system-event type.
- [x] Existing listener and `LwjglFrameServices` tests pass without a duplicated configuration path.

**Risks:** Accidentally constructing separate mouse, scrollbar, or text-layout state for different
listeners can change interaction behavior; identity-sensitive tests must guard the composition.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-01
- Implemented Scope: Extracted the standard seven-event window listener set into a reusable core factory, removed the private LWJGL composition, and shared the caller-owned GUI event, time, mouse, clipboard, keyboard-layout, and text-measurement dependencies together with one scrollbar interaction, one keyboard, and one control-text-layout service
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/system/event/listener/DefaultSystemEventListeners.java` (`create`); `spinygui.core/src/test/java/com/spinyowl/spinygui/core/system/event/listener/DefaultSystemEventListenersTest.java`; `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/LwjglFrameServices.java` (factory consumption)
- Acceptance Evidence:
  - Complete standard listener set: Verified — Automated — composition contract tests prove exactly one typed listener for each of the seven system-event types emitted by `DefaultLwjglWindow`, no listener for the eight other current concrete system-event types, and identity sharing for behavior-sensitive dependencies
  - Single authoritative composition path: Verified — Automated — all 16 system-event-listener suites passed 125 tests and `LwjglFrameServicesTest` passed 2 tests; source inspection finds listener builders only in `DefaultSystemEventListeners` and one factory call in `LwjglFrameServices`
- Decisions and Deviations: Defined the standard set by the seven callback event types currently emitted by `DefaultLwjglWindow`; existing non-window-emitted system events remain unregistered. The extracted path now explicitly shares one `ControlTextLayoutService` across all text-aware listeners instead of allowing each builder to derive an equivalent wrapper from the same measurer, matching the T3 identity requirement without changing public `LwjglFrameServices` behavior. Focused Gradle verification passed with existing Windows permission and deprecation warnings.
- Review Outcome: Accepted — independent review found no functional T3 findings; the concurrent `config/pmd/ruleset.xml` edit is preserved but explicitly excluded from E2/T3 scope and evidence
- Remaining Work: None
- Resume or Closure: T3 accepted; proceed to T4 while preserving the unrelated PMD ruleset change separately

### T4: Add the cbchain GLFW event bridge

**Purpose:** Reuse one event-mapping implementation while allowing application and engine callbacks
to coexist.

**Prerequisites:** T1 provides the active frame target; T3 provides the destination listener
composition.

**Changes:**

- [x] Extract GLFW action, button, modifier, and typed-event mapping from `DefaultLwjglWindow` into
  `GlfwSystemEventBridge`.
- [x] Add a narrow callback-installation strategy in the LWJGL backend, inject it into
  `DefaultLwjglWindow`, and have the facade bridge implement it without introducing a
  backend-to-facade dependency.
- [x] Register bridge handlers through cbchain and support caller-provided chains for embedded hosts.
- [x] Resolve `FrameNavigator.currentFrame()` when capturing each event and push it to the existing
  `SystemEventProcessor` queue.
- [x] Separate Escape-to-close behavior into explicit host policy.
- [x] Track callback ownership so close detaches or frees only bridge-owned registrations and never
  terminates GLFW.
- [x] Declare the facade's cbchain JPMS dependency explicitly while keeping cbchain out of the
  lower-level LWJGL backend module.
- [x] Add fake/injected callback tests for value mapping, active-frame targeting, coexistence,
  unsupported values, and teardown.

**Acceptance Checks:**

- [x] Every currently supported GLFW callback maps to the same `SystemEvent` values as the existing
  standalone implementation.
- [x] Switching navigation frames between callback captures targets subsequent events to the new
  current frame without retargeting already queued events.
- [x] Closing an embedded bridge leaves caller-owned callback chains usable and does not call global
  GLFW termination.
- [x] Module compilation proves the backend does not depend on the facade or cbchain.

**Risks:** cbchain and JPMS access requirements may leak into lower-level backend consumers. Keep
the cbchain-aware implementation in the existing facade and the injected installer contract in the
LWJGL backend.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-01
- Implemented Scope: Added one authoritative GLFW-to-system-event mapper with capture-time navigator targeting and explicit key policy, introduced the backend callback-installer ownership handle, made `DefaultLwjglWindow` consume the injected contract while preserving its existing fixed-frame constructor, and added a facade cbchain bridge with exclusive owned-window and attach-only embedded modes; review fixes now make per-chain attachment transactional, capture key targets before policy execution, expose package-private fake native ownership seams, and continue standalone teardown after individual cleanup failures with suppression
- Relevant Files and Symbols: `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/GlfwSystemEventMapper.java` (`key` capture order, `KeyPolicy`, `Callbacks`); `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/LwjglCallbackInstaller.java`; `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/DefaultLwjglWindow.java` (`close`, `NativeCleanup`, injected installer and compatibility composition); `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/DefaultLwjglWindowTest.java`; `spinygui/src/main/java/com/spinyowl/spinygui/GlfwSystemEventBridge.java` (`owned`, `attached`, transactional `addHandlers`, `NativeRegistrar`, failure-safe `close`, `Chains`); `spinygui/src/main/java/module-info.java`; `spinygui/src/test/java/com/spinyowl/spinygui/GlfwSystemEventBridgeTest.java`
- Acceptance Evidence:
  - GLFW event mapping compatibility: Verified — Automated — `GlfwSystemEventBridgeTest` passed callback mapping for cursor position/enter, window size, scroll, mouse, character, and key values, all six modifiers, and unsupported button/action filtering; the mapping was removed from `DefaultLwjglWindow`
  - Capture-time active-frame targeting: Verified — Automated — final forced bridge suite proves both ordinary between-callback navigation and navigation inside `KeyPolicy`; the key event retains the frame captured before policy execution
  - Embedded callback ownership: Verified — Automated — final forced 8-test bridge suite proves attach-only idempotent teardown and coexistence, transactional rollback after a throwing chain, fake owned native install/close exactly once, and complete handler rollback after fake native-install failure; a post-review rerun against the final window source passed `DefaultLwjglWindowTest` (1 test) and `AbstractLwjglApplicationTest` (3 tests), proving registration-close failure is retained while later callback, context, window, and GLFW cleanup continues and subsequent close is a no-op
  - Backend dependency direction: Verified — Automated — a post-review `--rerun-tasks` invocation passed `:spinygui.core.backend.lwjgl.nanovg:compileJava` and `:spinygui:compileJava` against the final source; source/module inspection confirms cbchain is required explicitly only by the facade and absent from the backend module
- Decisions and Deviations: Used the approved option 1 contract. Exclusive owned mode is documented for a fresh host-owned window and unsets/frees its seven native chain callbacks in reverse order; attach-only mode assumes caller-owned chains are already installed and never mutates native callbacks or GLFW lifecycle. Kept raw GLFW mapping in the lower-level mapper so both the existing standalone constructor and facade bridge use exactly one mapping implementation without a backend-to-facade dependency. Escape close behavior remains only an explicit injected `KeyPolicy`; the compatibility constructor supplies its prior Escape behavior. Review fixes keep the production static GLFW registrar private while exposing only package-private fake seams, rollback successful chain additions in reverse order with suppressed cleanup failures, and use a package-private `NativeCleanup` adapter to prove `DefaultLwjglWindow` continues callback, context, window, and GLFW teardown after registration close fails.
- Review Outcome: Accepted — independent re-review found no remaining functional defect after the requested transactional attachment, failure-safe teardown, capture-order, and fake ownership repairs; its sole evidence-freshness concern was resolved by rerunning the 1-test window suite, 3-test lifecycle suite, and both module compilation tasks against the final source
- Remaining Work: None; native callback interoperability remains intentionally deferred to T6 smoke verification
- Resume or Closure: T4 accepted; proceed to T5 without changing the approved owned-versus-attached invariants

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

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-01
- Implemented Scope: Added the facade production host with explicit owned and injected compositions, made the reusable application loop navigator-aware at every frame-sensitive phase, preserved the legacy single-frame path through a capacity-one navigator, and added failure-safe ownership-aware cleanup for fully initialized and partial-initialization paths
- Relevant Files and Symbols: `spinygui/src/main/java/com/spinyowl/spinygui/LwjglApplicationHost.java` (owned/injected compositions and lifecycle); `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/AbstractLwjglApplication.java` (`ResourceOwnership`, current-frame loop targeting, cleanup); `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/LwjglFrameServices.java` (initial-frame convenience composition); `spinygui/src/test/java/com/spinyowl/spinygui/LwjglApplicationHostTest.java`; `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/AbstractLwjglApplicationTest.java`
- Acceptance Evidence:
  - Dynamic-frame host lifecycle order: Verified — Automated — final forced `LwjglApplicationHostTest` passed 4 tests and `AbstractLwjglApplicationTest` passed 6 tests across the composed host and reusable loop
  - Navigation during update: Verified — Automated — the focused loop suite proves a frame selected during update is used for the later size synchronization, preparation, render, and publication phases in that same iteration
  - Single-frame source compatibility: Verified — Automated — the legacy constructor delegates through a capacity-one navigator, its lifecycle suite passed, and both `:spinygui.demo.simple:compileJava` and `:spinygui.demo.complex:compileJava` passed after the final production changes
  - Ownership-aware failure cleanup: Verified — Automated — the final focused host, loop, and window suites passed 4, 6, and 1 tests respectively, covering owned reverse cleanup, pre-run close, invalid-capacity construction, partial initialization, and injected-resource non-ownership
- Decisions and Deviations: The host exposes explicit owned and injected construction paths. Owned composition validates the navigator before services can mutate the renderer, then creates standard services, exclusive callback bridge, standalone window, pipeline, and renderer; ownership transfers only after successful factory return. Pre-run close destroys the transferred renderer before services, while ordinary loop teardown remains reverse and failure-safe. Injected composition initializes and uses caller resources without closing them. The compatibility frame constructor retains its historical owned cleanup semantics through a one-frame navigator.
- Review Outcome: Accepted — independent re-review found no remaining defect after pre-run renderer cleanup and validation-order repairs; it independently passed the final 4-test host suite and confirmed the current 6-test loop, 1-test window, demo compilation, module accessibility, same-iteration navigation, and injected non-ownership evidence
- Remaining Work: None; the reviewer noted only that simultaneous renderer-and-service pre-run failure is source-inspected rather than separately tested, while native smoke remains T6
- Resume or Closure: T5 accepted; proceed to T6 documentation, examples, aggregate checks, benchmark suite, and native smoke

### T6: Publish examples and verification evidence

**Purpose:** Make standalone and embedded adoption understandable and verify user-visible native
behavior separately from headless contracts.

**Prerequisites:** T5 provides the complete host path.

**Changes:**

- [x] Add or adapt a small example demonstrating navigation, back/forward, opening nested modals,
  backdrop behavior, and focus restoration through the production host.
- [x] Document standalone window ownership, embedded callback-chain injection, manual composition,
  navigation semantics, modal semantics, and teardown responsibilities.
- [x] Run focused suites, affected module suites, static analysis, and the full build.
- [x] Perform a native smoke check for callback coexistence, navigation, modal focus/inertness,
  resizing, and close behavior; record native evidence separately from automated evidence.

**Acceptance Checks:**

- [x] Documentation contains runnable standalone and embedded integration paths without claiming
  that inactive frames render beneath modals.
- [ ] `:spinygui.core:test`, `:spinygui.core.backend.lwjgl.nanovg:test`, `:spinygui:test`, simple and
  complex demo checks, PMD, and SpotBugs pass.
- [x] The full Gradle build passes or an environmental blocker is recorded with exact scope.
- [x] The opt-in `:spinygui.benchmark:test` suite passes or an unrelated benchmark blocker is
  recorded separately.
- [x] Native smoke evidence explicitly reports navigation, modal input/focus behavior, callback
  coexistence, and cleanup observations.

**Risks:** Headless tests cannot prove native callback interoperability or visible modal stacking;
do not promote automated structural evidence to native acceptance.

#### Execution Record

- Status: Completed
- Last Updated: 2026-09-01
- Implemented Scope: Retained the previously accepted T6 example, documentation, and verification scope; repaired the reopened initial-frame presentation path so the LWJGL loop never renders or publishes an unusable zero-sized framebuffer, forces first valid presentation, repaints after framebuffer-size-only changes, and retries after framebuffer unavailability; added a trace-backed focused regression suite
- Relevant Files and Symbols: `docs/features/lwjgl-application-host.md`; `README.md` (guide link); `spinygui.demo.simple/build.gradle.kts` (`runNavigationModalHostExample`); `spinygui.demo.simple/src/main/java/com/spinyowl/spinygui/demo/simple/NavigationModalHostExample.java`; `spinygui.demo.simple/src/test/java/com/spinyowl/spinygui/demo/simple/NavigationModalHostExampleTest.java`; `spinygui.core.backend.lwjgl.nanovg/src/main/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/AbstractLwjglApplication.java` (`run`, usable-framebuffer presentation tracking); `spinygui.core.backend.lwjgl.nanovg/src/test/java/com/spinyowl/spinygui/core/backend/renderer/lwjgl/AbstractLwjglApplicationFirstFrameTraceTest.java`; `FlexLayoutTest`, `OverflowLayoutTest`, and `ParsedInlineWhitespaceLayoutTest` (current two-argument constructor migration)
- Acceptance Evidence:
  - Runnable integration documentation: Verified — Documentation and Automated — the guide contains standalone, embedded, and lower-level paths plus explicit active-frame, modal, callback ownership, and teardown semantics; `NavigationModalHostExampleTest` passed 1 test and both demo main modules compile
  - Affected automated suites and static analysis: Partially Verified — Automated — core passed 696 tests, LWJGL/NanoVG backend 128, facade 12, and the simple example 1; T6-owned simple PMD/SpotBugs checks passed. Aggregate `staticAnalysis` is blocked by the unrelated dirty `config/pmd/ruleset.xml` enabling `UnusedPrivateField` broadly (`:spinygui.core:pmdMain` 98 findings and `:spinygui.core.backend.lwjgl.nanovg:pmdMain` 1) plus concurrent complex-demo environment failures at `:spinygui.demo.complex:pmdMain`, `:spinygui.demo.complex:compileTestJava` (cannot create parent directories for `MainMenuExampleTest`), and `:spinygui.demo.complex:spotbugsMain` (cannot write the HTML report)
  - Full Gradle build: Blocked — Automated — the root build reaches the same unrelated PMD-rule findings and complex-demo output-directory/report failures; independent E2 suites and demo main compilation remain green
  - Opt-in benchmark suite: Verified — Automated — `:spinygui.benchmark:test` passed 122 tests
  - Native smoke behavior: Verified — Native Host — the owned production demo visibly passed Home-to-Details navigation, Back, Forward, modal backdrop/background inertness, nested LIFO input and focus restoration, resize repaint, Escape close, and window-close-control teardown across two launches. The reviewed embedded harness then proved representative native key-chain coexistence: attached `C` changed the title to `caller=1 bridge=1`; deferred `D` detached the bridge outside callback iteration; detached `C` changed it to `caller=2 bridge=1`; Escape closed the caller-owned window cleanly. All-seven mapping and attach/remove parity remains automated evidence from the 8-test bridge suite, not a native claim.
  - Fresh automated re-verification (2026-09-01): Partially Verified — Automated — forced affected suites passed 696 core, 128 LWJGL/NanoVG backend, 12 facade, and 2 simple-demo tests with zero failures, errors, or skips; the opt-in benchmark suite passed 122 tests. Complex-demo production compilation passed, but test compilation reproduced the existing output-directory error for `MainMenuExampleTest`. Fresh `staticAnalysis` and root `build --continue` reproduced the unrelated dirty-ruleset PMD totals (98 core and 1 LWJGL/NanoVG) plus the complex-demo test/report output failures. `git diff --check` reported only existing line-ending warnings.
  - Fresh owned-host native re-verification (2026-09-01): Partially Verified — Native Host — the first launched window remained visually blank for at least three seconds and rendered Home only after maximize triggered resize/repaint. After repaint, Home-to-Details navigation, Back, Forward, modal backdrop, background inertness, nested-modal LIFO/topmost admission, and process cleanup on Escape were observed. Fresh native focus restoration and embedded callback coexistence were not independently repeated; they retain the prior native and current automated evidence above.
  - Initial-frame presentation correction (2026-09-01): Verified — Automated and Native Host — the focused host lifecycle plus first-frame trace run passed 8 tests, and the full LWJGL/NanoVG backend passed 130 tests with zero failures, errors, or skips. The trace proves `0x0` is neither rendered nor published, the next valid framebuffer renders without a logical resize, and a framebuffer-size-only change requests paint. A fresh production-demo launch immediately displayed `Home frame` at its original window size without maximize or resize; Escape closed the native window and the Gradle process exited successfully. `spotbugsMain` passed. `pmdMain` reproduced only the pre-existing unrelated dirty-ruleset `UnusedPrivateField` finding for `NvgTextRenderer.diagnostics`; the changed host is absent from the report.
- Decisions and Deviations: The example lives in the simple demo module and uses the production owned host rather than introducing another module. Inactive navigation-history frames are documented as retained state only and are not claimed to render beneath the current frame or its modal stack. The presentation repair stays in the polling host boundary rather than adding another GLFW callback: zero-sized framebuffers clear the last-presented size and defer preparation/publication, while the first valid or changed framebuffer invalidates paint before preparation. Window-show timing remains unchanged, so eliminating any brief pre-first-swap background flash is outside this repair. Concurrent `graphify-out`, complex-demo source/test, and PMD ruleset changes were preserved and excluded from E2 evidence.
- Review Outcome: Accepted — independent review of commit `ae556fb5` found no first-frame defects, confirmed that non-blocking event polling renders the first valid framebuffer in the same iteration, verified zero-sized and framebuffer-only resize handling plus post-swap presentation tracking, and passed the focused 8-test lifecycle/trace suite
- Remaining Work: None for the initial-frame presentation repair. Separately rerun the aggregate static-analysis/build gates after the unrelated PMD ruleset and complex-demo output failures are resolved; do not absorb those repairs into E2.
- Resume or Closure: T6 and E2/M4/P1 are complete with the unrelated aggregate verification caveat retained. Residual low-risk test gaps are explicit render/swap failure injection and a direct `valid -> 0x0 -> same valid` case; both paths were source-inspected during independent review.

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
