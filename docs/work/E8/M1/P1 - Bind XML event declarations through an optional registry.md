# E8/M1/P1: Bind XML Event Declarations Through an Optional Registry

## Document Context

- Status: Completed
- Dependencies: None
- Parent: [M1 - Add optional handler registry binding](../M1%20-%20Add%20optional%20handler%20registry%20binding.md)
- Children: None
- Related: [E8 - Declarative XML event binding](../../E8%20-%20Declarative%20XML%20event%20binding.md), [Button element support](../../../features/button-element-support.md)
- Next: None

## Goal

Implement the first complete optional XML-to-Java event-binding slice: typed registry construction,
explicit `on-action` and `on-click` declarations, default resolving proxies, configurable runtime
resolution diagnostics, compatibility coverage, and one documented demo adoption.

## Non-Goals

- Controller reflection, annotations, dependency injection, or Java method names in XML.
- Property/data binding, expressions, repeat/include constructs, or template composition.
- Arbitrary event-class names or automatic discovery of every `Event` subtype.
- A global registry or mutation callbacks inside general `Element.setAttribute(...)`.
- ID-index performance work or duplicate-ID policy changes.

## Context

- `DefaultNodeParser.createNodeFromElement(...)` copies all parsed attributes to `Node` instances.
- `Element` stores listeners by exact event class, and `DefaultEventProcessor` requests listeners using
  `event.getClass()`.
- `ActionEvent` is the semantic activation event for buttons and button inputs; `MouseClickEvent` is
  the lower-level pointer event.
- Existing complex demos manually call `getElementById(...)` and `addListener(...)`, providing a direct
  behavior comparison for the new path.

## Assumptions and Open Questions

- Assumption: the proxy-listener is installed by default for every supported `on-*` declaration. The
  registry is optional and may be unavailable when the tree is loaded or an event is dispatched.
- Assumption: loader/binder initialization receives immutable binding options. Unavailable registries
  and missing handlers follow `ERROR`, `WARNING`, or `SILENT`; the default is `ERROR`.
- Assumption: on every event the proxy reads the current attribute and current optional registry.
  Missing registry and missing exact-type handler follow the same configured policy: `ERROR` fails the
  dispatch, `WARNING` skips it and sends a structured diagnostic, and `SILENT` skips it quietly.
- Assumption: repeated `WARNING` events for the same element, attribute, handler value, and registry
  state are deduplicated; a changed attribute or registry revision permits a new warning.
- Assumption: blank or malformed declarations and event-type mismatches are hard errors in every
  policy. Initial values may be validated during loading; values introduced later are validated when
  the proxy next dispatches.
- Assumption: one nonblank name resolves to one exact event type within a registry and may be referenced
  by more than one XML element. Controlled registration replacement is visible on the next event.
- Assumption: registry mutation occurs on the owner UI thread between event batches. Concurrent or
  mid-dispatch replacement is not supported.
- Assumption: direct `setAttribute(...)` does not run binding logic; the already installed proxy simply
  reads the new value on the next event.

## Phase Tasks

Tasks appear in recommended execution order. Prerequisites identify only hard implementation
dependencies.

### T1: Add the dynamic handler registry and binding options

**Purpose:** Give callers one explicit, backend-neutral owner for named Java event listeners without
reflection or global mutable state.

**Changes:**

- [x] Add `HandlerRegistry` in a dedicated exported core binding package, with controlled registration,
  replacement, and event-time lookup based on a nonblank name and exact `Class<T extends Event>`.
- [x] Add `replace(name, eventClass, listener)` or an equivalently typed operation that rejects
  event-type changes and makes the new listener visible to the next proxy dispatch without tree
  traversal or listener reattachment.
- [x] Add immutable loader/binder initialization options with a `MissingHandlerPolicy` enum containing
  `ERROR`, `WARNING`, and `SILENT`; default omitted configuration to `ERROR`.
- [x] Add an injectable structured diagnostic sink for `WARNING`, with a documented default sink and no
  warning emission in `SILENT` mode.
- [x] Reject null/blank names and duplicate names deterministically; define controlled replacement and
  removal semantics using a monotonically changing registry revision for diagnostic deduplication.
- [x] Add focused registry tests for typed lookup, missing lookup, duplicate registration, invalid
  names, controlled mutation, and revision changes.

**Acceptance Checks:**

- [x] Registry tests prove exact event type, controlled replacement/removal, revision changes, and
  event-time visibility across multiple references without reflection or backend dependencies.
- [x] Options tests prove the `ERROR` default, explicit `WARNING`/`SILENT` selection, injected warning
  delivery, and immutable initialization state.
- [x] The new public package is exported by `spinygui.core` and compiles on the module path.

**Risks:** A generic lookup API can hide unsafe casts. Keep unchecked conversion inside one reviewed
exact-type boundary, use structured diagnostics, and reject event-type mutation explicitly.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Added the backend-neutral T1 binding contract: an exact-type caller-owned handler registry with deterministic registration, lookup, replacement, removal, and revision behavior; immutable missing-handler options; structured warning diagnostics; a documented default SLF4J sink; focused contract tests; and the JPMS package export. Corrected `replace(...)` to validate all nullable arguments before consulting name presence, matching its documented exception contract.
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/HandlerRegistry.java` (`HandlerRegistry`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/MissingHandlerPolicy.java` (`MissingHandlerPolicy`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/BindingDiagnostic.java` (`BindingDiagnostic`, `BindingDiagnostic.Reason`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/BindingDiagnosticSink.java` (`BindingDiagnosticSink`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/DefaultBindingDiagnosticSink.java` (`DefaultBindingDiagnosticSink`); `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/XmlEventBindingOptions.java` (`XmlEventBindingOptions`); `spinygui.core/src/main/java/module-info.java`; `spinygui.core/src/test/java/com/spinyowl/spinygui/core/binding/HandlerRegistryTest.java`; `spinygui.core/src/test/java/com/spinyowl/spinygui/core/binding/XmlEventBindingOptionsTest.java`
- Acceptance Evidence:
  - Registry tests prove exact event type, controlled replacement/removal, revision changes, and event-time visibility across multiple references without reflection or backend dependencies.: Verified — Automated — fresh `./gradlew.bat --no-daemon :spinygui.core:test --tests "*HandlerRegistry*" --tests "*XmlEventBindingOptions*" :spinygui.core:compileJava --rerun-tasks --console=plain` passed all 6 `HandlerRegistryTest` cases and all 4 options cases; the added precedence case proves null type/listener arguments fail before an absent-name lookup; an earlier forced full `:spinygui.core:test` rerun passed 705 tests
  - Options tests prove the `ERROR` default, explicit `WARNING`/`SILENT` selection, injected warning delivery, and immutable initialization state.: Verified — Automated — the fresh focused command passed all 4 `XmlEventBindingOptionsTest` cases, including structured sink delivery and retained initialization values
  - The new public package is exported by `spinygui.core` and compiles on the module path.: Verified — Automated — the fresh focused command compiled `module-info.java` with the exported `com.spinyowl.spinygui.core.binding` package; a fresh combined run also passed `:spinygui.core:pmdMain`, `pmdTest`, `spotbugsMain`, and `spotbugsTest`
- Decisions and Deviations: Kept the single unchecked generic conversion private to exact-type lookup after a runtime class-identity check. Successful register, replace, and remove operations advance the revision; rejected mutations and removal of an absent name leave it unchanged. The default diagnostic sink logs structured fields through the module's existing SLF4J API. Resolved the independent review finding by validating the replacement registration tuple before reading registry state, preserving the documented null-failure precedence without changing the absent-name or type-mismatch contracts. No plan deviations.
- Review Outcome: Accepted — independent public-contract review found one low-severity validation-precedence issue; the same implementer fixed it, focused verification passed, and manager inspection confirmed the reviewed T1 change set
- Remaining Work: None for T1. T2 is newly unlocked.
- Resume or Closure: Closed after manager acceptance; continue with T2 using this accepted registry contract.

### T2: Add explicit XML event declaration mappings

**Purpose:** Define a small stable XML vocabulary whose event semantics already exist in SpinyGUI.

**Prerequisites:** T1 provides the typed registry entry contract.

**Changes:**

- [x] Define lowercase kebab-case `on-action` to `ActionEvent` and `on-click` to `MouseClickEvent`
  mappings without arbitrary class-name loading.
- [x] Validate declaration values as nonblank handler names and retain ordinary attribute
  parse/serialization behavior.
- [x] Add parser/binding fixtures proving Jsoup normalization and XML round-trip behavior for the
  reserved attributes.

**Acceptance Checks:**

- [x] Focused tests prove both supported attributes map to their exact GUI event classes after parsing.
- [x] Existing XML without handler attributes and XML with unrelated custom attributes parse exactly as
  before.

**Risks:** Treating every `on-*` attribute as reserved would block future application metadata. Match
only the explicitly supported vocabulary.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Added the explicit XML declaration vocabulary in the exported binding package. `XmlEventDeclaration.fromAttribute(...)` recognizes only normalized lowercase `on-action` and `on-click`, maps them to exact `ActionEvent` and `MouseClickEvent` classes, validates supported values as nonblank registry keys, and ignores unsupported/custom attributes without interpreting their values. Added focused parser-backed normalization, round-trip, validation, and compatibility fixtures without changing `DefaultNodeParser`.
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/XmlEventDeclaration.java` (`XmlEventDeclaration`, `fromAttribute`, `attributeName`, `handlerName`, `eventClass`); `spinygui.core/src/test/java/com/spinyowl/spinygui/core/binding/XmlEventDeclarationTest.java`
- Acceptance Evidence:
  - Focused tests prove both supported attributes map to their exact GUI event classes after parsing.: Verified — Automated — `./gradlew.bat --no-daemon :spinygui.core:test --tests "*XmlEventDeclarationTest" --tests "*DefaultNodeParserTest" :spinygui.core:compileJava --rerun-tasks --console=plain` passed all 5 declaration cases and compiled the public mapping; coverage proves Jsoup normalization, exact event classes, nonblank validation, and supported-attribute serialization/reparse
  - Existing XML without handler attributes and XML with unrelated custom attributes parse exactly as before.: Verified — Automated — the same focused run passed all 11 existing `DefaultNodeParserTest` cases; declaration fixtures additionally preserve and round-trip handler-free templates plus unrelated `data-*` and unsupported `on-hover` attributes
- Decisions and Deviations: Used one immutable explicit attribute-to-class map and returned an empty declaration for every unsupported attribute before inspecting its value, preventing accidental reservation of arbitrary `on-*` or application metadata. Direct uppercase lookup remains unsupported by contract; parser-backed fixtures prove Jsoup normalizes markup names before mapping. Fresh core PMD and SpotBugs main/test tasks passed. No plan deviations.
- Review Outcome: Accepted — localized explicit mapping was manager-reviewed against the plan; the fresh parser-backed focused suite and module compilation passed with no scope drift
- Remaining Work: None for T2. T3 is newly unlocked.
- Resume or Closure: Closed after manager acceptance; continue with T3 using the accepted explicit vocabulary.

### T3: Install event-time resolving proxy listeners

**Purpose:** Make supported XML declarations active by default through stable proxies while keeping the
registry optional and resolving current handler state only when an event is dispatched.

**Prerequisites:** T1 and T2 define registry and declaration contracts.

**Changes:**

- [x] Add a loader composition initialized with an injected `NodeParser`, an optional registry source,
  and immutable binding options; retain the existing `NodeParser.fromHtml(String)` API and current call
  sites unchanged.
- [x] Traverse the parsed tree once and automatically attach exactly one stable resolving proxy for
  each supported `on-*` declaration, whether or not a registry is currently available.
- [x] Make the proxy read the current attribute value on every event, then read the current optional
  registry and perform an exact event-class lookup for that handler name.
- [x] When no registry is available or no exact-type handler exists, apply `ERROR` by failing the
  dispatch, `WARNING` by skipping invocation and emitting a structured diagnostic, or `SILENT` by
  skipping invocation without diagnostics.
- [x] Deduplicate `WARNING` diagnostics for the same element, attribute, current value, and registry
  revision/state; emit again after any of those resolution inputs changes.
- [x] Treat blank/malformed current declarations and event-type mismatches as hard errors in all modes;
  validate initial syntax during loading and dynamically introduced values at dispatch.
- [x] Include event attribute, handler name, tag, and element `id` or deterministic tree path in errors
  and warnings.
- [x] Make the proxy delegate both `process(...)` and `processWithImpact(...)` to the currently resolved
  handler; when `WARNING` or `SILENT` skips invocation, call `batch.markUnknownFallback()` so the
  installed proxy cannot incorrectly turn an unresolved application path into proven unchanged input.
- [x] Prevent duplicate proxy attachment through all public loader paths without introducing a
  separate binding lifecycle or requiring teardown/rebind calls.

**Acceptance Checks:**

- [x] Loading attaches one proxy per supported declaration even when the optional registry is absent;
  templates without supported declarations receive no proxy listeners.
- [x] One handler registration serves multiple elements, and each event invokes its resolved handler
  exactly once.
- [x] With no registry and with a missing exact-type handler, `ERROR` fails dispatch, `WARNING` skips and
  reports once per unresolved state, and `SILENT` skips without warning emission.
- [x] Directly changing an existing declaration attribute changes the handler used on the next event
  without traversal, rebind, or listener reattachment.
- [x] Replacing or removing a registry entry changes subsequent dispatch for every referencing element
  without traversal or listener reattachment.
- [x] Blank/malformed declarations and event-type mismatches remain hard errors in all three modes.
- [x] The proxy preserves resolved handlers' custom `processWithImpact(...)` behavior, and skipped
  `WARNING`/`SILENT` resolution records `FULL_UNKNOWN` through `markUnknownFallback()`.
- [x] Existing direct parser and manual `addListener(...)` tests remain green.

**Risks:** Event-time lookup adds work and runtime failure modes to dispatch. Keep proxy state small,
limit the initial vocabulary to action/click, deduplicate warnings, and test the exact error boundary
and input-impact result rather than allowing exceptions or classification to emerge accidentally.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Added `XmlEventBindingLoader`, an opt-in `NodeParser` composition that delegates existing parsing/serialization, performs one post-parse tree traversal, and attaches stable private resolving proxies for declarations present at load time. Proxies read the current declaration and current optional registry on every call, enforce exact handler types and contextual hard errors, apply all missing-handler policies, deduplicate structured warnings by per-element declaration plus registry identity/revision, preserve resolved listeners' ordinary and impact-aware dispatch methods, and mark skipped permissive resolution as `FULL_UNKNOWN`. Duplicate proxy detection prevents repeated attachment even when multiple loader instances receive the same tree.
- Relevant Files and Symbols: `spinygui.core/src/main/java/com/spinyowl/spinygui/core/binding/XmlEventBindingLoader.java` (`XmlEventBindingLoader`, `ResolvingEventListener`, `WarningState`); `spinygui.core/src/test/java/com/spinyowl/spinygui/core/binding/XmlEventBindingLoaderTest.java`
- Acceptance Evidence:
  - Loading attaches one proxy per supported declaration even when the optional registry is absent; templates without supported declarations receive no proxy listeners.: Verified — Automated — focused `XmlEventBindingLoaderTest` passed attachment for both supported declarations, no-proxy behavior for unrelated attributes, and stable single-listener identity across repeated and cross-loader same-tree parsing
  - One handler registration serves multiple elements, and each event invokes its resolved handler exactly once.: Verified — Automated — focused shared-registration coverage invoked one registry listener exactly twice across two distinct bound buttons
  - With no registry and with a missing exact-type handler, `ERROR` fails dispatch, `WARNING` skips and reports once per unresolved state, and `SILENT` skips without warning emission.: Verified — Automated — focused policy coverage passed all three policies for both unavailable-registry and available-but-missing-handler states, including contextual error fields and one warning per unchanged state
  - Directly changing an existing declaration attribute changes the handler used on the next event without traversal, rebind, or listener reattachment.: Verified — Automated — focused dynamic-state coverage switched from `first` to `second` on the same proxy identity and listener count
  - Replacing or removing a registry entry changes subsequent dispatch for every referencing element without traversal or listener reattachment.: Verified — Automated — final focused dynamic-state coverage observed replacement on the next call for both referencing elements and permissive skips for both after removal, retaining both proxy identities and one-listener counts
  - Blank/malformed declarations and event-type mismatches remain hard errors in all three modes.: Verified — Automated — focused coverage passed initial blank, dynamic blank, removed declaration, and exact-type mismatch failures under `ERROR`, `WARNING`, and `SILENT`, with attribute, handler, tag, and id/path context
  - The proxy preserves resolved handlers' custom `processWithImpact(...)` behavior, and skipped `WARNING`/`SILENT` resolution records `FULL_UNKNOWN` through `markUnknownFallback()`.: Verified — Automated — focused impact coverage preserved a custom `FULL_REFRESH` result without invoking ordinary `process(...)` and produced `FULL_UNKNOWN` for both permissive skip policies
  - Existing direct parser and manual `addListener(...)` tests remain green.: Verified — Automated — `./gradlew.bat --no-daemon :spinygui.core:test --rerun-tasks --console=plain` passed all 719 core tests with zero failures, errors, or skips; the focused pre-run also passed declaration, parser, and input-processing compatibility suites
- Decisions and Deviations: Implemented the opt-in loader as `NodeParser` rather than changing `NodeParser` or `DefaultNodeParser`. The dynamic registry boundary is `Supplier<Optional<HandlerRegistry>>`; a fixed-registry convenience constructor validates immediately. Duplicate prevention recognizes the private proxy type plus attribute on the exact event listener list. Warning state includes handler name, registry object identity, revision, and failure reason; successful resolution clears it so a later return to the same unresolved state can warn again. Fresh core PMD and SpotBugs main/test tasks passed. No plan deviations.
- Review Outcome: Accepted — independent review found no T3 defects or scope drift; fresh focused, full core, PMD, and SpotBugs verification passed against the reviewed loader/proxy change set
- Remaining Work: None for T3. T4 is newly unlocked.
- Resume or Closure: Closed after manager acceptance; continue with T4 end-to-end dispatcher compatibility evidence.

### T4: Prove dispatch semantics and compatibility

**Purpose:** Demonstrate that declarative attachment is only composition over the existing event
system and does not alter dispatch, input, or invalidation semantics.

**Prerequisites:** T3 provides bound views.

**Changes:**

- [x] Add focused tests that dispatch `ActionEvent` and `MouseClickEvent` through
  `DefaultEventProcessor` to XML-bound elements and assert the correct handler invocation.
- [x] Cover multiple declarations, shared handlers, no-handler events, disabled controls where
  applicable, exact-class mismatch behavior, and dispatch after `WARNING`/`SILENT` skips.
- [x] Prove a custom listener override of `processWithImpact(...)` survives registry binding unchanged.
- [x] Prove direct attribute mutation and registry replacement/removal affect all relevant elements on
  their next event without changing proxy listener identity.
- [x] Prove missing registry and missing-handler dispatch follow each configured policy without
  interfering with manually attached listeners.
- [x] Run the existing button mouse/keyboard activation suites to confirm that `on-action` continues to
  receive semantic activation from both supported input paths.

**Acceptance Checks:**

- [x] Binding-focused dispatch tests pass without adding a second queue, event bus, bubbling rule, or
  native callback path.
- [x] Dynamic attribute lookup, registry replacement/removal, warning deduplication, and missing-registry
  policy tests pass without repeating the initial full-tree traversal.
- [x] Existing event processor, system mouse, system key, and disabled-control focused suites pass.

**Risks:** Tests that invoke listeners directly would miss dispatcher compatibility. At least one test
per supported declaration must pass through `DefaultEventProcessor`.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Added dispatcher-level tests proving XML-bound action/click declarations, shared registrations, live attribute and registry mutation, missing-registry/handler policy behavior, warning deduplication, exact-class mismatch handling, manual-listener coexistence, and custom impact propagation through `DefaultEventProcessor`; reran the existing event/system/disabled-control compatibility suites.
- Relevant Files and Symbols: `spinygui.core/src/test/java/com/spinyowl/spinygui/core/binding/XmlEventBindingDispatchTest.java` (`dispatchesBothSupportedDeclarationsThroughDefaultEventProcessor`, `dispatchesOneSharedRegistrationOnceForEachTarget`, `dynamicAttributeAndRegistryMutationsAffectEveryTargetWithoutReattachment`, `missingPoliciesPreserveManualListenersAndWarningDedupeAcrossBatches`, `preservesCustomImpactOverrideThroughDefaultEventProcessor`, `preservesExactClassDispatchAndHardMismatchFailure`)
- Acceptance Evidence:
  - Binding-focused dispatch tests pass without adding a second queue, event bus, bubbling rule, or native callback path.: Verified — Automated — `.\gradlew.bat --no-daemon :spinygui.core:test --tests "*XmlEventBindingDispatchTest" --rerun-tasks --console=plain` passed 6/6 tests through `DefaultEventProcessor`.
  - Dynamic attribute lookup, registry replacement/removal, warning deduplication, and missing-registry policy tests pass without repeating the initial full-tree traversal.: Verified — Automated — the combined focused command passed `XmlEventBindingDispatchTest` 6/6 and `XmlEventBindingLoaderTest` 8/8; proxy identity and listener counts remained unchanged across mutations.
  - Existing event processor, system mouse, system key, and disabled-control focused suites pass.: Verified — Automated — `.\gradlew.bat --no-daemon :spinygui.core:test --tests "*XmlEventBindingDispatchTest" --tests "*XmlEventBindingLoaderTest" --tests "*InputProcessingContractTest" --tests "*SystemMouseClickEventListenerTest" --tests "*SystemKeyEventListenerTest" --tests "*DisabledControlEventListenerTest" --rerun-tasks --console=plain` passed 81/81 tests (6 binding dispatch, 8 binding loader, 7 processor contract, 23 mouse, 33 key, 4 disabled-control); `.\gradlew.bat --no-daemon :spinygui.core:pmdTest :spinygui.core:spotbugsTest --rerun-tasks --console=plain` also passed.
- Decisions and Deviations: T4 is test-only because accepted T1-T3 already compose bindings over `DefaultEventProcessor`; no second dispatch mechanism was added. Existing disabled-control semantics are covered by the focused compatibility suite. `ERROR` remains intentionally fail-fast, while `WARNING` and `SILENT` skips allow later manual listeners to run.
- Review Outcome: Accepted — test-only T4 was manager-reviewed; the fresh dispatcher gate passed and the implementer compatibility matrix passed 81 tests with PMD/SpotBugs test analysis
- Remaining Work: None for T4. T5 is newly unlocked.
- Resume or Closure: Closed after manager acceptance; continue with T5 demo adoption.

### T5: Migrate one demo to the optional registry path

**Purpose:** Prove the API is concise for a real resource-backed view while retaining manual binding as
a supported compatibility path elsewhere.

**Prerequisites:** T3 and T4 establish binding and dispatch behavior.

**Changes:**

- [x] Add `on-action` declarations to `button-demo.xml` and migrate `ButtonExample` to a caller-owned
  registry plus the optional loader/binder path.
- [x] Preserve activation counts, status text updates, logging, CSS loading, nested button content, and
  input-button behavior.
- [x] Add or extend the focused complex-demo test so it verifies the resource declares resolvable
  handlers and each action updates the expected state.

**Acceptance Checks:**

- [x] The migrated demo compiles and its focused automated test proves all three current controls bind
  and activate once per action.
- [x] At least one existing demo remains on manual `getElementById(...)` plus `addListener(...)`, proving
  declarative binding is optional rather than a migration requirement.

**Risks:** Demo-only success can hide public API friction. Keep the demo on the same public classes and
resource-loading path available to external consumers.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Migrated the resource-backed button demo's three current controls to `on-action` declarations resolved by a caller-owned `HandlerRegistry` through `XmlEventBindingLoader`; preserved activation numbering, status text, logging, CSS loading, nested content, and input-button behavior; added focused dispatcher-backed demo coverage while retaining existing manual-binding demos.
- Relevant Files and Symbols: `spinygui.demo.complex/src/main/resources/com/spinyowl/spinygui/demo/button-demo.xml` (`on-action` declarations); `spinygui.demo.complex/src/main/java/com/spinyowl/spinygui/demo/complex/ButtonExample.java` (`createGuiElements`, `registerActivationFeedback`); `spinygui.demo.complex/src/test/java/com/spinyowl/spinygui/demo/complex/ButtonExampleTest.java` (`declarativeActionsPreserveAllButtonDemoStateChanges`)
- Acceptance Evidence:
  - The migrated demo compiles and its focused automated test proves all three current controls bind and activate once per action.: Verified — Automated — `.\gradlew.bat --no-daemon :spinygui.demo.complex:test --tests "*ButtonExampleTest" :spinygui.demo.complex:compileJava --rerun-tasks --console=plain` passed 1/1 focused test; `.\gradlew.bat --no-daemon :spinygui.demo.complex:check :spinygui.demo.complex:compileJava --rerun-tasks --console=plain` passed all 5 module tests plus PMD and SpotBugs.
  - At least one existing demo remains on manual `getElementById(...)` plus `addListener(...)`, proving declarative binding is optional rather than a migration requirement.: Verified — Documentation — current `MainMenuExample` retains its manual `getElementById(...)` and `addListener(...)` action setup unchanged.
- Decisions and Deviations: The caller-owned registry is populated after XML loading because proxies resolve at dispatch time, keeping the example concise while exercising the public late-registration contract. Native/manual smoke is not required for T5 acceptance because renderer and system-input paths are unchanged and accepted T4 already covers mouse/keyboard action synthesis; it remains optional for visual confidence.
- Review Outcome: Accepted — manager review confirmed the three declarative controls, preserved observable state contract, unchanged manual-binding MainMenuExample, and successful fresh focused demo test/compilation
- Remaining Work: None for T5. T6 is newly unlocked.
- Resume or Closure: Closed after manager acceptance; continue with T6 documentation and final integrated verification.

### T6: Document adoption and run final verification

**Purpose:** Publish the optional registry and dispatch-time failure contract clearly enough that
callers understand when proxies are installed and when resolution occurs.

**Prerequisites:** T5 provides the final public usage shape.

**Changes:**

- [x] Add a focused feature guide showing default proxy attachment, optional registry configuration,
  and manual listeners, plus supported attributes, the default `ERROR` policy, `WARNING` diagnostic
  sink configuration, and explicit `SILENT` behavior.
- [x] Document event-time attribute lookup, controlled registry replacement/removal, warning
  deduplication, owner-thread requirements, and the absence of a binding-session lifecycle.
- [x] Update the nearest project documentation index or README entry that currently advertises XML and
  event usage without claiming reflection, data binding, or unsupported event declarations.
- [x] Record deferred work for additional event mappings, high-frequency-event measurement,
  controller adapters, and reactive data binding.
- [x] Run formatting/diff checks and the focused plus aggregate module verification commands.

**Acceptance Checks:**

- [x] Documentation examples compile against the delivered public API and state that the registry is
  optional, resolution failures default to `ERROR` even when the registry is absent, and callers can
  explicitly select `WARNING` or `SILENT`.
- [x] Documentation examples cover direct attribute changes and registry replacement without implying
  thread-safe mid-dispatch mutation or mutation callbacks inside `setAttribute(...)`.
- [x] `:spinygui.core:test` and `:spinygui.demo.complex:test` pass, and complex-demo production sources
  compile.
- [x] `git diff --check` passes; native demo smoke, if performed, is reported separately from automated
  evidence.

**Risks:** Documentation can accidentally imply a full template/controller framework. Keep claims
limited to named event handler binding.

**Execution Record:**

- Status: Completed
- Last Updated: 2026-09-02
- Implemented Scope: Added a focused named XML event-handler guide covering opt-in loader composition, supported declarations, exact-type registry usage, default and permissive policies, event-time mutation and registry replacement/removal, warning deduplication, owner-thread constraints, manual listeners, the absence of `BindingSession`, and deferred scope; linked it from the root README and ran the final integrated core/demo gate.
- Relevant Files and Symbols: `docs/features/xml-event-binding.md` (public usage, policies, mutation/lifecycle contract, manual path, deferred scope); `README.md` (`Named XML event handlers` link)
- Acceptance Evidence:
  - Documentation examples compile against the delivered public API and state that the registry is optional, resolution failures default to `ERROR` even when the registry is absent, and callers can explicitly select `WARNING` or `SILENT`.: Verified — Documentation — guide snippets were checked against the delivered constructors and methods; the same registry/loader composition in `ButtonExample` compiled during the final gate, and the guide explicitly covers optional direct parsing plus `ERROR`, `WARNING`, and `SILENT`.
  - Documentation examples cover direct attribute changes and registry replacement without implying thread-safe mid-dispatch mutation or mutation callbacks inside `setAttribute(...)`.: Verified — Documentation — the guide shows `setAttribute(...)`, entry replacement/removal, and whole-registry supplier replacement, and limits all mutation to the owner thread between event batches while stating that `setAttribute(...)` invokes no callback or proxy attachment.
  - `:spinygui.core:test` and `:spinygui.demo.complex:test` pass, and complex-demo production sources compile.: Verified — Automated — `.\gradlew.bat --no-daemon :spinygui.core:test :spinygui.demo.complex:test :spinygui.demo.complex:compileJava --rerun-tasks --console=plain` passed 725/725 core tests and 5/5 complex-demo tests with production compilation successful.
  - `git diff --check` passes; native demo smoke, if performed, is reported separately from automated evidence.: Verified — Automated — `git diff --check` passed after removing one README hard-break whitespace issue; native smoke was not run and is not included in automated evidence.
- Decisions and Deviations: Published the guide under `docs/features/` and linked it from the root README's feature Links section. The guide deliberately describes only named event-handler binding, states there is no `BindingSession`, and records additional event mappings, high-frequency-event measurement, controller adapters, and reactive binding as deferred. Native smoke was not needed for this documentation-only slice and was not run.
- Review Outcome: Accepted — final independent integrated review found no findings, confirmed documentation/API alignment, and passed core 725/725 plus complex-demo 5/5 and production compilation; the sandbox-only javac directory failure was reproduced and cleared by the successful unsandboxed gate
- Remaining Work: None for T6 or P1.
- Resume or Closure: Closed after manager acceptance; all T1-T6 records and acceptance checks are complete.

## Verification Strategy

- Registry contract: `./gradlew.bat --no-daemon :spinygui.core:test --tests "*HandlerRegistry*" --console=plain`.
- Binder contract: run the focused XML binding tests plus `DefaultNodeParserTest`.
- Dispatch compatibility: run `DefaultEventProcessor` coverage and the focused system mouse/key and
  disabled-control suites.
- Demo integration: run the focused `ButtonExample` test, `:spinygui.demo.complex:test`, and
  `:spinygui.demo.complex:compileJava`.
- Final gate: run `:spinygui.core:test :spinygui.demo.complex:test
  :spinygui.demo.complex:compileJava` and `git diff --check`; do not report unrelated aggregate or
  native checks as green unless they finish successfully.

## Review Boundaries

- Review T1-T3 together as the public registry, mapping, and resolving-proxy contract.
- Review T4 separately as compatibility evidence against the existing event pipeline.
- Review T5-T6 together as adoption, documentation, and final integration evidence.

## Deferred Work

- Additional explicit event attributes after consumer evidence establishes their semantics.
- An optional controller/annotation adapter in a separate layer; no private reflection in core.
- Automatic proxy installation for a supported `on-*` attribute added to an element that had no such
  declaration when loaded; the initial slice dynamically reads values only for already proxied
  declarations.
- Declarative mappings for high-frequency pointer movement or scrolling before measurement shows the
  event-time lookup and diagnostic state are acceptable.
- Reactive values, collections, expressions, includes, or template inheritance.
- ID indexing and duplicate-ID policy, which remain independent frame-performance work.
