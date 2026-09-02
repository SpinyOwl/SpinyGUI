# E8/M1/P1: Bind XML Event Declarations Through an Optional Registry

## Document Context

- Status: Planned
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

- [ ] Add `HandlerRegistry` in a dedicated exported core binding package, with controlled registration,
  replacement, and event-time lookup based on a nonblank name and exact `Class<T extends Event>`.
- [ ] Add `replace(name, eventClass, listener)` or an equivalently typed operation that rejects
  event-type changes and makes the new listener visible to the next proxy dispatch without tree
  traversal or listener reattachment.
- [ ] Add immutable loader/binder initialization options with a `MissingHandlerPolicy` enum containing
  `ERROR`, `WARNING`, and `SILENT`; default omitted configuration to `ERROR`.
- [ ] Add an injectable structured diagnostic sink for `WARNING`, with a documented default sink and no
  warning emission in `SILENT` mode.
- [ ] Reject null/blank names and duplicate names deterministically; define controlled replacement and
  removal semantics using a monotonically changing registry revision for diagnostic deduplication.
- [ ] Add focused registry tests for typed lookup, missing lookup, duplicate registration, invalid
  names, controlled mutation, and revision changes.

**Acceptance Checks:**

- [ ] Registry tests prove exact event type, controlled replacement/removal, revision changes, and
  event-time visibility across multiple references without reflection or backend dependencies.
- [ ] Options tests prove the `ERROR` default, explicit `WARNING`/`SILENT` selection, injected warning
  delivery, and immutable initialization state.
- [ ] The new public package is exported by `spinygui.core` and compiles on the module path.

**Risks:** A generic lookup API can hide unsafe casts. Keep unchecked conversion inside one reviewed
exact-type boundary, use structured diagnostics, and reject event-type mutation explicitly.

### T2: Add explicit XML event declaration mappings

**Purpose:** Define a small stable XML vocabulary whose event semantics already exist in SpinyGUI.

**Prerequisites:** T1 provides the typed registry entry contract.

**Changes:**

- [ ] Define lowercase kebab-case `on-action` to `ActionEvent` and `on-click` to `MouseClickEvent`
  mappings without arbitrary class-name loading.
- [ ] Validate declaration values as nonblank handler names and retain ordinary attribute
  parse/serialization behavior.
- [ ] Add parser/binding fixtures proving Jsoup normalization and XML round-trip behavior for the
  reserved attributes.

**Acceptance Checks:**

- [ ] Focused tests prove both supported attributes map to their exact GUI event classes after parsing.
- [ ] Existing XML without handler attributes and XML with unrelated custom attributes parse exactly as
  before.

**Risks:** Treating every `on-*` attribute as reserved would block future application metadata. Match
only the explicitly supported vocabulary.

### T3: Install event-time resolving proxy listeners

**Purpose:** Make supported XML declarations active by default through stable proxies while keeping the
registry optional and resolving current handler state only when an event is dispatched.

**Prerequisites:** T1 and T2 define registry and declaration contracts.

**Changes:**

- [ ] Add a loader composition initialized with an injected `NodeParser`, an optional registry source,
  and immutable binding options; retain the existing `NodeParser.fromHtml(String)` API and current call
  sites unchanged.
- [ ] Traverse the parsed tree once and automatically attach exactly one stable resolving proxy for
  each supported `on-*` declaration, whether or not a registry is currently available.
- [ ] Make the proxy read the current attribute value on every event, then read the current optional
  registry and perform an exact event-class lookup for that handler name.
- [ ] When no registry is available or no exact-type handler exists, apply `ERROR` by failing the
  dispatch, `WARNING` by skipping invocation and emitting a structured diagnostic, or `SILENT` by
  skipping invocation without diagnostics.
- [ ] Deduplicate `WARNING` diagnostics for the same element, attribute, current value, and registry
  revision/state; emit again after any of those resolution inputs changes.
- [ ] Treat blank/malformed current declarations and event-type mismatches as hard errors in all modes;
  validate initial syntax during loading and dynamically introduced values at dispatch.
- [ ] Include event attribute, handler name, tag, and element `id` or deterministic tree path in errors
  and warnings.
- [ ] Make the proxy delegate both `process(...)` and `processWithImpact(...)` to the currently resolved
  handler; when `WARNING` or `SILENT` skips invocation, call `batch.markUnknownFallback()` so the
  installed proxy cannot incorrectly turn an unresolved application path into proven unchanged input.
- [ ] Prevent duplicate proxy attachment through all public loader paths without introducing a
  separate binding lifecycle or requiring teardown/rebind calls.

**Acceptance Checks:**

- [ ] Loading attaches one proxy per supported declaration even when the optional registry is absent;
  templates without supported declarations receive no proxy listeners.
- [ ] One handler registration serves multiple elements, and each event invokes its resolved handler
  exactly once.
- [ ] With no registry and with a missing exact-type handler, `ERROR` fails dispatch, `WARNING` skips and
  reports once per unresolved state, and `SILENT` skips without warning emission.
- [ ] Directly changing an existing declaration attribute changes the handler used on the next event
  without traversal, rebind, or listener reattachment.
- [ ] Replacing or removing a registry entry changes subsequent dispatch for every referencing element
  without traversal or listener reattachment.
- [ ] Blank/malformed declarations and event-type mismatches remain hard errors in all three modes.
- [ ] The proxy preserves resolved handlers' custom `processWithImpact(...)` behavior, and skipped
  `WARNING`/`SILENT` resolution records `FULL_UNKNOWN` through `markUnknownFallback()`.
- [ ] Existing direct parser and manual `addListener(...)` tests remain green.

**Risks:** Event-time lookup adds work and runtime failure modes to dispatch. Keep proxy state small,
limit the initial vocabulary to action/click, deduplicate warnings, and test the exact error boundary
and input-impact result rather than allowing exceptions or classification to emerge accidentally.

### T4: Prove dispatch semantics and compatibility

**Purpose:** Demonstrate that declarative attachment is only composition over the existing event
system and does not alter dispatch, input, or invalidation semantics.

**Prerequisites:** T3 provides bound views.

**Changes:**

- [ ] Add focused tests that dispatch `ActionEvent` and `MouseClickEvent` through
  `DefaultEventProcessor` to XML-bound elements and assert the correct handler invocation.
- [ ] Cover multiple declarations, shared handlers, no-handler events, disabled controls where
  applicable, exact-class mismatch behavior, and dispatch after `WARNING`/`SILENT` skips.
- [ ] Prove a custom listener override of `processWithImpact(...)` survives registry binding unchanged.
- [ ] Prove direct attribute mutation and registry replacement/removal affect all relevant elements on
  their next event without changing proxy listener identity.
- [ ] Prove missing registry and missing-handler dispatch follow each configured policy without
  interfering with manually attached listeners.
- [ ] Run the existing button mouse/keyboard activation suites to confirm that `on-action` continues to
  receive semantic activation from both supported input paths.

**Acceptance Checks:**

- [ ] Binding-focused dispatch tests pass without adding a second queue, event bus, bubbling rule, or
  native callback path.
- [ ] Dynamic attribute lookup, registry replacement/removal, warning deduplication, and missing-registry
  policy tests pass without repeating the initial full-tree traversal.
- [ ] Existing event processor, system mouse, system key, and disabled-control focused suites pass.

**Risks:** Tests that invoke listeners directly would miss dispatcher compatibility. At least one test
per supported declaration must pass through `DefaultEventProcessor`.

### T5: Migrate one demo to the optional registry path

**Purpose:** Prove the API is concise for a real resource-backed view while retaining manual binding as
a supported compatibility path elsewhere.

**Prerequisites:** T3 and T4 establish binding and dispatch behavior.

**Changes:**

- [ ] Add `on-action` declarations to `button-demo.xml` and migrate `ButtonExample` to a caller-owned
  registry plus the optional loader/binder path.
- [ ] Preserve activation counts, status text updates, logging, CSS loading, nested button content, and
  input-button behavior.
- [ ] Add or extend the focused complex-demo test so it verifies the resource declares resolvable
  handlers and each action updates the expected state.

**Acceptance Checks:**

- [ ] The migrated demo compiles and its focused automated test proves all three current controls bind
  and activate once per action.
- [ ] At least one existing demo remains on manual `getElementById(...)` plus `addListener(...)`, proving
  declarative binding is optional rather than a migration requirement.

**Risks:** Demo-only success can hide public API friction. Keep the demo on the same public classes and
resource-loading path available to external consumers.

### T6: Document adoption and run final verification

**Purpose:** Publish the optional registry and dispatch-time failure contract clearly enough that
callers understand when proxies are installed and when resolution occurs.

**Prerequisites:** T5 provides the final public usage shape.

**Changes:**

- [ ] Add a focused feature guide showing default proxy attachment, optional registry configuration,
  and manual listeners, plus supported attributes, the default `ERROR` policy, `WARNING` diagnostic
  sink configuration, and explicit `SILENT` behavior.
- [ ] Document event-time attribute lookup, controlled registry replacement/removal, warning
  deduplication, owner-thread requirements, and the absence of a binding-session lifecycle.
- [ ] Update the nearest project documentation index or README entry that currently advertises XML and
  event usage without claiming reflection, data binding, or unsupported event declarations.
- [ ] Record deferred work for additional event mappings, high-frequency-event measurement,
  controller adapters, and reactive data binding.
- [ ] Run formatting/diff checks and the focused plus aggregate module verification commands.

**Acceptance Checks:**

- [ ] Documentation examples compile against the delivered public API and state that the registry is
  optional, resolution failures default to `ERROR` even when the registry is absent, and callers can
  explicitly select `WARNING` or `SILENT`.
- [ ] Documentation examples cover direct attribute changes and registry replacement without implying
  thread-safe mid-dispatch mutation or mutation callbacks inside `setAttribute(...)`.
- [ ] `:spinygui.core:test` and `:spinygui.demo.complex:test` pass, and complex-demo production sources
  compile.
- [ ] `git diff --check` passes; native demo smoke, if performed, is reported separately from automated
  evidence.

**Risks:** Documentation can accidentally imply a full template/controller framework. Keep claims
limited to named event handler binding.

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
