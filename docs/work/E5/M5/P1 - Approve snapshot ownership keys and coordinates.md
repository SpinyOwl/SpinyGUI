# P1: Approve Snapshot Ownership, Keys, and Coordinates

## Goal

Approve one control-local slot, a shared backend-neutral core service, a complete query-time key, an
immutable text-local snapshot, and all source/geometry mappings before implementation.

## Non-Goals

- A global control identity map, historical snapshots, or NanoVG-dependent core data.
- Automatic observation/interception of every mutable style/node alias.

## Context

- Parent milestone: `docs/work/E5/M5 - Share bounded editable-control snapshots.md`.
- Phase entry gate: M2 contract and M3 production font generation/lifecycle are complete.
- Every query must validate the full effective key because mutation hooks are incomplete by design.

## Phase Tasks

### T1: Define node-slot and core-service ownership
**Purpose:** Make snapshot retention naturally bounded and reachable by all core/backend consumers.

**Depends on:** None.
**Enables:** T2.
**Parallelizable with:** None.

**Changes:**
- [ ] Define exactly one current snapshot slot on each `InputElement` and `TextareaElement`, its
  visibility/mutation owner, replacement/clear behavior, and no-history guarantee.
- [ ] Require explicit Lombok/generated equality/hash/`toString` exclusion so cache state is not part
  of node identity/diagnostics output.
- [ ] Define a shared core service composition/query API used by renderers and char/key/mouse/cursor/
  scroll/viewport behaviors plus `NvgDebugRenderer` control inspection with no NanoVG type or global
  node map.

**Acceptance Checks:**
- [ ] An ownership diagram has one strong path from a live control to at most one snapshot and no
  service-global control references.
- [ ] Population/clear cannot affect node equality, hash, or string representation.

**Risks / Stop Criteria:** Stop if a service needs to retain control identity or if equality excludes
the slot only accidentally through current Lombok defaults.

### T2: Define exact lazy-validation keys
**Purpose:** Ensure every text-layout-affecting input replaces the slot and every presentation-only
input reuses it.

**Depends on:** T1.
**Enables:** T3.
**Parallelizable with:** None.

**Changes:**
- [ ] Define immutable key fields for exact value; effective family list/style/weight/stretch/font
  size/line height and any other typography; measurement context/configuration/rounding; real M3
  semantic generation; and textarea current content width/current actual wrap policy.
- [ ] Explicitly exclude input placement and content height plus color, focus, caret, selection,
  scroll, viewport position, ancestor scroll, and presentation transforms; document consumer-time use.
- [ ] Define exact float/value equality/canonicalization, defensive copying, null/default effective
  values, and validation on every service query.
- [ ] Do not define a mutable textarea wrap-policy transition/test unless M2 approved adding such a
  public API; otherwise key the existing actual constant/policy.

**Acceptance Checks:**
- [ ] A field-by-field matrix shows hit/miss expectations and the code path that computes each
  effective value without retaining mutable aliases.
- [ ] Font validity comes from the real M3 generation, never a fake/local counter.

**Risks / Stop Criteria:** Stop if a key includes resolved style object identity, omits an effective
typography/configuration field, or uses approximate width equality.

### T3: Define immutable geometry and source mappings
**Purpose:** Make one snapshot sufficient for all rendering/editing/viewport consumers without storing
placement state.

**Depends on:** T2.
**Enables:** T4.
**Parallelizable with:** None.

**Changes:**
- [ ] Define immutable whole-control extent, paragraph, visual-line, fragment/run, glyph/replacement,
  cumulative caret boundary, hit-test, and source UTF-16 mappings.
- [ ] Define empty/trailing paragraphs, wrapped line boundary ownership, newline positions,
  multi-paragraph wrapped fallback/replacement, and M2 surrogate-interior behavior.
- [ ] Define text-local axes/origins and explicit consumer conversions through content/layout
  placement, viewport, control scroll, ancestor scroll, and presentation transforms.

**Acceptance Checks:**
- [ ] A multi-paragraph wrapped fallback fixture can answer line, caret, selection, hit-test, extent,
  and run queries without remeasurement or ambiguous boundary ownership.
- [ ] No snapshot field stores absolute layout position, viewport origin, ancestor scroll, transform,
  color, focus, caret, or selection state.

**Risks / Stop Criteria:** Stop if two consumers require incompatible coordinate interpretations or
if replacement geometry loses original source positions.

### T4: Approve invalidation, compatibility, and consumer matrix
**Purpose:** Authorize implementation only after every consumer and mutation class has one path.

**Depends on:** T3.
**Enables:** None.
**Parallelizable with:** None.

**Changes:**
- [ ] Map input/textarea renderer, char/key/mouse/cursor, selection, scroll, viewport, line navigation,
  hit-test, shared listener/provider dispatch, and debug renderer consumers to service queries and
  coordinate conversions.
- [ ] Map key/non-key mutations, direct mutable aliases, query-time validation, explicit clear, and
  M3 generation changes to reuse/replacement behavior.
- [ ] Record compatibility impact for node fields/generated methods, M2 surrogate setters, and any
  service/constructor injection changes.

**Acceptance Checks:**
- [ ] No consumer remains authorized to instantiate independent multiline metrics or call
  `TextMeasurer` directly for control text geometry after migration.
- [ ] Review explicitly confirms one slot, no history/global map, backend neutrality, and query-time
  full-key validation.

**Risks / Stop Criteria:** Do not start P2 while a consumer or mutation has “special case later”
ownership.

## Verification Strategy

- Run `./gradlew :spinygui.core:test --tests 'com.spinyowl.spinygui.core.system.input.*'`.
- Run `./gradlew :spinygui.core.backend.lwjgl.nanovg:test --tests 'com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgInputRendererTest'`.
- Review current node Lombok annotations and M2/M3 contracts; no production snapshot code yet.

## Review Boundaries

- Review ownership, then key table, then geometry/mappings, then consumer/invalidation matrix.

## Deferred Work

- Slot/service implementation belongs to P2; control-specific routing belongs to P3/P4; shared
  listener/provider/debug integration belongs to P5; proof belongs to P6.
- Global control caching and full retained layout remain deferred.

## Dependency Graph

```mermaid
flowchart TD
  T1["T1: Define node-slot and core-service ownership"]
  T2["T2: Define exact lazy-validation keys"]
  T3["T3: Define immutable geometry and source mappings"]
  T4["T4: Approve invalidation, compatibility, and consumer matrix"]
  T1 --> T2
  T2 --> T3
  T3 --> T4
```
