# P1: Approve Semantic Font and Thread Contracts

**Status:** Complete

## Document Context

- Parent: [M3 - Establish font identity generations and lifecycle](../M3%20-%20Establish%20font%20identity%20generations%20and%20lifecycle.md)
- Children: None
- Related: [E5 - Text performance improvements](../../E5%20-%20Text%20performance%20improvements.md)
- Next: [P2 - Centralize registry generation and resolver ownership](P2%20-%20Centralize%20registry%20generation%20and%20resolver%20ownership.md)

## Goal

Approve one core semantic font identity/generation model, exact mutation transitions, UI-thread
confinement, and separation from context-local NanoVG faces before changing ownership.

## Non-Goals

- Implementing concurrent atomic registry snapshots.
- Treating NanoVG face IDs/names as semantic core font identity.

## Context

- Parent milestone: `docs/work/E5/M3 - Establish font identity generations and lifecycle.md`.
- Phase entry gate: M1 evidence/counters are accepted.
- M5 and M7 require a production generation whose semantics cover actual font byte and registry
  mutation, not a temporary cache version.

## Approved Contract

### Semantic owner and identity

- One explicitly installed core semantic owner holds registered font identities, the monotonic
  generation, and the production resolver. Production static/default compatibility entry points
  delegate to that owner and cannot create a second registry.
- A semantic face key is the normalized case-insensitive family plus style, weight, and stretch.
  The immutable semantic identity also includes the normalized resource locator and a byte-content
  revision derived from the successfully parsed font bytes. Object identity, map identity, NanoVG
  context handles, face IDs, and face names are never semantic identity inputs.
- Built-in `Font` constants are immutable descriptors. Their class initialization does not select
  an owner thread or mutate a separate registry; the owner performs built-in bootstrap atomically.
- Arbitrary public per-face removal is unsupported until a concrete consumer requires it. The owner
  supports `clear`/`close` for deterministic teardown.

### Generation transitions

The generation is a non-negative monotonic `long`, initially `0`. A semantic transaction computes,
validates, and parses its replacement state before publishing content and the next generation
together on the owner thread. At `Long.MAX_VALUE`, a semantic mutation is rejected before
publication rather than wrapping.

| Operation | Content/identity outcome | Generation | Old-resource outcome | Error outcome |
| --- | --- | ---: | --- | --- |
| Initial built-in bootstrap into an empty owner | Publish the complete built-in set atomically | `+1` | None | Any failure publishes nothing and remains `0` |
| Repeat identical built-in bootstrap | No change | `+0` | Retained | Return the existing identities |
| Add a new semantic face key | Publish the parsed identity | `+1` | None | Load/parse/validation failure publishes nothing |
| Add the exact existing semantic identity | No change | `+0` | Retained | Return the existing identity |
| Replace the same face key with a different locator or byte revision | Publish one replacement atomically | `+1` | Retire under the P3 lifetime contract | Failure retains the prior identity |
| Reload the same locator with unchanged bytes | No change | `+0` | Retained | Return the existing identity |
| Reload the same locator with changed bytes | Publish one revised identity atomically | `+1` | Retire under the P3 lifetime contract | Failure retains the prior identity |
| Load system fonts | Treat each successfully parsed face as one independent semantic transaction | `+1` per changed face | Per-face P3 rules | A failed face publishes nothing and does not affect successful siblings |
| Clear a non-empty owner during teardown | Publish the empty registry | `+1` | Release later in P3-defined order | Partial clear is forbidden |
| Clear an empty owner | No change | `+0` | None | No error |
| Remove one face | Unsupported | `+0` | Retained | Reject before mutation |
| Mutation when generation is `Long.MAX_VALUE` | No change | `+0` | Retained | Reject before load, parse, validation, or publication |

### T1 baseline alias inventory and executable ownership

At P1 acceptance, `FontSemanticContractTest` separated characterization methods prefixed `current`
from migration methods prefixed `p2Target`. P2/T1 activated its 17 migration targets against the production
`SemanticFontOwner` through the test-local `SemanticRegistryTarget` harness; remaining disabled core
and backend targets belong to P3/P4. The harness encodes observable state transitions without
prescribing later alias/resolver API shapes.

| Alias / operation | Current characterized behavior | Approved P2 target | Fixture ownership |
| --- | --- | --- | --- |
| Built-in `Font` static fields | Class initialization calls `Font.addFont` for four descriptors and mutates the process-wide static map before an explicit owner exists | Installation starts at generation `0`; the complete built-in set publishes atomically at `1`; repeat is a no-op | Active characterization plus P2/T1 initial/repeat and load/parse/validation bootstrap targets |
| `Font.addFont` | Uses a case-sensitive family map key; same family/style/weight/stretch replaces regardless of path; an exact duplicate still traverses remove/add; no generation is observable | Normalize the case-insensitive semantic face key; add/replace advances once; exact identity is `+0` | Active characterization plus P2/T1 identity and single-mutation targets |
| `FontStorage.loadFont` / `getFontData` | Loads bytes directly into a path-keyed concurrent map; explicit reload replaces the cached buffer; load failure returns `null`; no semantic publication/generation boundary exists | Byte load/validation is preparation inside one owner transaction; unchanged bytes are `+0`, changed bytes publish with `+1`, and failure publishes nothing | Active characterization plus P2/T1 single-mutation, failure-atomicity, and overflow targets |
| `FontService.loadFont` | Parses/caches STB information and returns a descriptor without adding it to `Font`'s static registry; failure throws and does not make the path available | Parsing completes before semantic publication; failure retains the prior identity and generation | Active characterization plus P2/T1 failure-atomicity target |
| `SystemFontLoader.loadSystemFonts` | Discovers `.ttf` paths, loads storage first, then independently parses/registers each storage-successful path; parse failure is logged, later siblings continue, and the return list still includes that storage-successful path | Each face is one semantic transaction; changed siblings each advance once, exact duplicates do not advance, and storage-load, parse, or validation failure does not affect successful siblings | Active characterization plus P2/T1 transaction and explicit system-font failure-point targets |
| Replacement / reload | Replacement is observable through `Font.addFont`; byte reload is separately observable through `FontStorage.loadFont`; there is no atomic identity/content/generation owner spanning them | Different locator or byte revision replaces atomically with `+1`; unchanged locator/bytes is `+0`; failure retains the prior identity | Active characterization plus P2/T1 semantic-identity, single-mutation, and failure-atomicity targets |
| Clear / single-face removal | `Font`, `FontStorage`, and `FontService` expose no public generation or clear/remove mutation surface | Non-empty owner clear is atomic `+1`; empty clear is `+0`; arbitrary single-face removal is rejected with `+0` | Active characterization plus P2/T1 single-mutation and removal targets |
| Generation overflow | No current semantic generation surface exists | At `Long.MAX_VALUE`, reject before any byte load, parse, validation, or publication and retain content/generation | Active characterization plus P2/T1 `p2TargetOverflowIsRejectedBeforePreparationOrPublication` |

P2/T2 has now replaced the five baseline characterization fixtures with eleven active mutation-alias
migration fixtures. The table retains the pre-migration evidence used to approve the contract; the
current production result is recorded in P2/T2.

### UI-thread confinement

- Production composition explicitly installs the semantic owner on the current UI thread. Registry
  observation, resolution, measurement, future cache access, NanoVG face creation, and teardown
  require that exact thread for the owner's full lifetime.
- Registry operations before owner installation and every off-owner-thread operation fail
  deterministically with `IllegalStateException`; ownership never migrates implicitly.
- Bootstrap is part of owner installation. Public/static aliases cannot bootstrap or mutate a
  separate registry before or after installation.
- A measurement/layout/render pass may nest owner reads but establishes a read-use scope. Semantic
  mutation and teardown are rejected while such a scope is active, preventing reentrant generation
  changes within one pass without locks or atomic snapshots.
- A semantic transaction establishes its exclusive mutation guard before byte load, parse, or
  validation begins. Preparation callbacks cannot reenter any mutation surface or open a read-use
  scope; rejection/failure releases the guard before the next owner-thread operation.
- Isolated tests may construct explicitly owned registries, but production defaults must delegate to
  the installed owner.

### T2 baseline enforcement inventory and migration result

At P1 acceptance, a characterization fixture recorded the absence of a semantic owner and the
accidental ability to use registry/storage/service state across threads. P2/T2 has replaced that
fixture with active pre-install, exact-thread, and off-thread alias tests: production composition now
installs one owner explicitly, compatibility observations use it, and mutation/read/use reject on a
different thread without changing state.
`DiagnosticSession` is only implementation precedent for storing the exact creating `Thread` and
throwing `IllegalStateException`; it is not the semantic owner or an API dependency.

| Production call site / boundary | Current behavior requiring migration | Required enforcement and fixture owner |
| --- | --- | --- |
| `Font` built-in static initialization plus composition in `Demo.initializeServices` and benchmark service factories | Constants currently mutate the static registry before an explicit composition-owned installation boundary | P2 installation performs one atomic built-in bootstrap on `Thread.currentThread()`; active P2/T1 pre-install and install/bootstrap targets own this transition |
| `Font.addFont`, `SystemFontLoader.loadFontSafe`, `FontStorage.loadFont`, and `FontService.loadFont` | Mutation, byte loading, parsing, and static publication have no shared owner-thread guard | P2 mutation aliases delegate to one owner and supply thread/closed-state guards; P3 activates clear/close/resource teardown through that surface |
| `Font.fonts`/`find`/`hasFont` and `DefaultFontChainResolver.resolve` | Static observation and resolution have no installation, close, or thread check | P2 observation/resolution operations are owner-only and own exact-thread/pre-install/off-thread/nested-scope checks; P3 activates post-close checks through P2's surface |
| `TextLayoutImpl`, `BlockLayout`, `InlineFormattingContext`, `MultilineTextControlMetrics`, `TextInputMouseCaretBehavior`, and `TextInputViewportBehavior` | Layout/input paths independently call the default resolver and `TextMeasurer` without a shared read-use scope | P2 routes resolution/measurement through the owner and owns nested scopes plus mutation exclusion; P3 activates teardown exclusion during those scopes |
| `FontServiceImpl` measurement entries, `RangeTextMeasurerAdapter`, `fontInfoMap`, and `FontStorageImpl` | `FontStorageImpl` byte-cache access and `FontServiceImpl` parsed font-info cache access can currently occur from any thread | P2 guards byte-cache access, font-info cache access, and future semantic cache access as distinct owner operations; P3 owns eventual native-resource retirement |
| `NvgDebugRenderer`, `NvgInputRenderer`, and `NvgTextareaRenderer` | Renderer-side caret/measurement and default resolution calls have no shared render scope | P2 supplies the owner read-use scope contract; P4 must establish the concrete render-scope boundary around these calls |
| `NvgFontRegistry.fontFace`/`displayText`/`glyphIndex` and `NvgRenderer.destroy` | Context-local face creation, font-info caching, and destruction are not tied to semantic owner state | The disabled backend `NvgFontSemanticContractTest` assigns pre-install/exact-thread/off-thread/post-close face creation/use activation to P4; T3 separately owns semantic-versus-context identity/generation fixtures |
| `Demo.destroy` and other runtime teardown | Window/GLFW teardown exists, but no semantic registry close is composed before backend destruction | P2 supplies the owner/thread guard and closed-state integration surface; P3 activates clear/close/resource teardown; P4 wires face/context teardown ordering |

### Core/backend boundary

- Core publishes only immutable semantic identity and generation observations plus resolver output.
  It exposes no mutable registry maps, raw backend handles, or context-local face state.
- A NanoVG context owner keys local faces by core semantic identity and its own context. Face
  creation, failure, and retry never advance core generation.
- A changed semantic identity invalidates the affected backend-local face. P4 must choose either
  rejection while that identity is active or bounded context rotation; unbounded retention of old
  `freeData=false` buffers is not allowed.
- M5/M7 keys depend on core identity/generation only and remain renderer-neutral.

### T3 current behavior and executable ownership

The active fixtures characterize only behavior that exists today. Core `Font` descriptors contain
the immutable family/style/stretch/weight/path fields, and the current resolver returns an immutable
descriptor list with no NanoVG context, face ID, or face-name state. `NvgFontRegistry` owns separate
per-registry face, buffer, and STB-info maps; its current face key accepts only `Font`, while native
face creation and STB initialization receive duplicate buffer views. Failed native registry loads
retain no backend map entries, and successful/repeated/failed renderer face selection plus failed
registry retries do not mutate the process-wide core descriptor registry. Core has no semantic
generation yet, so exact zero-generation behavior is a disabled P4 integration target consuming the
P2 observation rather than a claim about a nonexistent current counter.

| State / transition | Approved owner and identity rule | Executable fixture ownership |
| --- | --- | --- |
| Core semantic observation | Immutable normalized semantic identity, byte revision, generation, and immutable resolved chain; no context handle, face ID/name, buffer, or STB info | Active core descriptor/resolver separation characterization; disabled P4 integration target consumes the P2 observation |
| Context-local face state | Per-live-context face name/ID, source buffer, duplicate submitted view, and STB info remain backend-local | Active backend map/record/source characterization; disabled P4 backend-state target |
| Face creation success/failure/retry | Never changes semantic identity or generation; failed attempts retain no reusable face | Active selection/load-failure characterization; disabled P4 load/STB/NanoVG failure and retry targets consume P2 identity/generation |
| Unchanged semantic identity | May reuse a face only within the same live context; another or retired context recreates | Disabled P4 context reuse/retirement target |
| Changed semantic identity | Advances only the core semantic generation and emits an explicit backend invalidation signal; affected face, buffer, font-info, identity, and context entries retire before recreation | Disabled P4 invalidation target consumes the P2 signal, with pre-recreation retention snapshots |
| P4 reload strategy | Either reject the semantic revision during preflight, before identity/generation publication, while an affected context is active, or rotate contexts with a finite retained-context bound; unbounded retention is invalid | Disabled P4 strategy target, with unchanged rejection snapshots and bounded rotation snapshots |
| M5/M7 keys | Depend only on core semantic identity and generation and are unchanged by face/context state | Disabled P4 backend-neutrality target consumes the P2 observation; M5/M7 use the resulting core key |

The current `NvgFontRegistry` is instance-local rather than explicitly bound to a NanoVG context;
P4 owns binding, retirement/recreation, invalidation consumption, and the selected bounded reload
strategy. Active-context rejection occurs in semantic-revision preflight so the original core
observation and every backend resource remain published together; bounded rotation releases all
affected backend-local entries before recreation. The active success evidence uses the renderer's
face-selection seam rather than creating a real native face; the disabled integration targets retain
ownership of native success/failure/retry and context lifecycle behavior.

## Assumptions and Deferred Decisions

- P2 owns the semantic owner, mutation routing, resolver migration, exact-thread guard, and
  closed-state integration surface; it does not activate resource teardown.
- P3 activates byte/STB-info retirement, alias lifetime, clear, close, and semantic resource
  teardown through the P2 surface.
- P4 activates exact-thread face/context creation, use, invalidation, and destruction and selects the
  active-context reload/reinitialization transition within the approved bounded choices above.
- Cross-thread reads, concurrent immutable snapshots, arbitrary face removal, and pluggable registry
  backends remain non-goals.

## Phase Tasks

### T1: Define semantic identity and generation transitions
**Purpose:** Give every font-dependent value one stable invalidation input.

**Depends on:** None.
**Enables:** T2.
**Parallelizable with:** None.

**Changes:**
- [x] Turn the approved semantic key/identity fields and mutation table into executable
  characterization/target fixtures without implementing the P2 owner.
- [x] Inventory `Font.addFont`, `SystemFontLoader`, `FontStorage.loadFont`, `FontService.loadFont`,
  built-in bootstrap, replacement/reload, and clear/removal aliases against the table.
- [x] Record the exact generation overflow, compound system-font publication, and failure atomicity
  fixtures P2 must activate.

**Acceptance Checks:**
- [x] A state table identifies content/identity outcome, generation delta, retained old resources,
  and error outcome for every operation.
- [x] The table includes bootstrap order/repeat behavior, clear/removal, every public alias, duplicate/
  no-op, and each partial/failure point in compound system-font loading.
- [x] No successful semantic byte/content change can remain visible under the prior generation; no-
  op/failed behavior is explicit and testable.

**T1 evidence:** P1 supplied five active baseline characterization fixtures and eleven initial P2/T1
targets. P2/T1 subsequently activated 17 owner targets covering the approved identity/generation
table, atomic preparation failures, overflow, installation/thread rules, mutation exclusion, and
normalized locators. P2/T2 then replaced the baseline alias fixtures with eleven active migration
fixtures and updated public Javadocs to describe the routed/rejected compatibility surfaces. Only
the P3 close/resource-teardown target and P4 backend targets remain disabled.

**Risks / Stop Criteria:** Stop if identity can alias two different byte resources, if failure can
partially change content without a generation transition, or if any public mutation alias bypasses
the table/owner.

### T2: Define UI-thread ownership and off-thread rejection
**Purpose:** Replace accidental concurrent-map semantics with one supported execution model.

**Depends on:** T1.
**Enables:** T3.
**Parallelizable with:** None.

**Changes:**
- [x] Add characterization/target fixtures for explicit owner installation, pre-install rejection,
  exact-thread use, non-migrating ownership, and owner-thread close.
- [x] Add target fixtures for nested read-use scopes and rejected mutation/teardown during an active
  measurement/layout/render pass.
- [x] Document the deterministic `IllegalStateException` contract without promising cross-thread
  visibility, locks, or atomic snapshots.

**Acceptance Checks:**
- [x] Contract fixtures cover supported owner-thread calls and rejected off-thread mutation/read/use.
- [x] No requirement introduces locks/atomic registry snapshots or promises cross-thread visibility.

**T2 evidence:** The baseline cross-thread characterization was retired by P2/T2. Six P2/T1 targets
are active and cover descriptor constants before installation; rejection of all
semantic registry/read-use operations before installation; exact-current-thread installation with
atomic built-in bootstrap; same-owner and off-thread second-install rejection without rebootstrap,
state replacement, or owner migration; owner-thread observation, resolution, measurement, byte-cache
access, font-info cache access, future semantic cache access, and mutation; rejected off-thread calls
and read-use scope open/close; nested measurement/layout/render scopes; and rejected mutation during
every active scope. One disabled P3 target activates pre-install/off-thread/active-scope close
rejection, owner-thread close, resource-teardown entry, and rejection of every operation/scope after
close through P2's closed-state surface. Four backend-module targets are explicitly labeled for P4
activation and cover NanoVG face creation/reuse on the exact owner thread plus pre-install,
off-thread, and post-close rejection. No target promises locks, cross-thread visibility, owner
migration, or immutable concurrent snapshots.

**Risks / Stop Criteria:** Stop if concurrent maps remain as an implied promise or if owner-thread
checks can silently migrate between threads.

### T3: Separate semantic registry and context-local face state
**Purpose:** Prevent backend face creation/retry/context transitions from corrupting core identity.

**Depends on:** T2.
**Enables:** None.
**Parallelizable with:** None.

**Changes:**
- [x] Add core/backend contract fixtures proving that face creation, failure, and retry cannot change
  the semantic generation and that context handles never enter semantic identity.
- [x] Record backend invalidation on changed semantic identity and the two allowed bounded P4
  strategies: active-context rejection or bounded context rotation.
- [x] Reconcile M3/P1 status and the P2 handoff only after all contract rows have executable fixture
  ownership.

**Acceptance Checks:**
- [x] Face creation success/failure/retry has no core generation effect; semantic registry mutation
  has a defined backend invalidation effect.
- [x] M5/M7 can depend only on core identity/generation and remain backend-neutral.

**T3 evidence:** One active core fixture proves current immutable descriptor/resolver observations
contain no backend context state. Two active backend fixtures prove per-registry face/buffer/STB-info
ownership, duplicate buffer views, failed-load cleanup/retry, and no mutation of current core
descriptors during success/reuse/failure paths. Six explicitly disabled P4 integration targets assign
immutable core observation and zero-generation face outcomes to the P2/P4 boundary, same-live-context
reuse plus complete face/buffer/font-info/context retirement to P4, explicit changed-identity
invalidation across P2/P4, the two bounded P4 strategies, and renderer-neutral M5/M7 keying to P2.
The rejection target proves preflight failure leaves the original core identity/generation and exact
backend retention snapshot unchanged; retirement/invalidation targets prove stale entries are absent
before recreation and all retained kinds remain within the chosen context bound. The existing four
disabled P4 thread targets remain separate.
The focused contract selection now discovers 71 core tests (70 active, one P3 target disabled) and
13 backend tests (3 active, 10 P4 targets disabled), with no failures. The additional active backend
fixture belongs to P2/T2 and proves the preserved public locator remains loadable.

**Risks / Stop Criteria:** Reject any design that stores context handles in core cache/snapshot keys
or uses face-creation count as semantic versioning.

## Verification Strategy

- Run `./gradlew :spinygui.core:test --tests 'com.spinyowl.spinygui.core.system.font.FontChainResolverTest' --tests 'com.spinyowl.spinygui.core.system.font.impl.FontServiceImplTest' --tests 'com.spinyowl.spinygui.core.system.font.FontSemanticContractTest'`.
- Run `./gradlew :spinygui.core.backend.lwjgl.nanovg:test --tests 'com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgFontRegistryTest' --tests 'com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgFontSemanticContractTest'`.
- Review transition/thread tables; do not implement registry/resource changes yet.

## Review Boundaries

- Approve identity/generation, then thread model, then core/backend split.

## Deferred Work

- Production owner/resolver/thread-guard implementation belongs to P2; semantic resource closure
  belongs to P3; NanoVG face/context thread integration and destruction belong to P4.
- Concurrent registry snapshots remain outside E5.
