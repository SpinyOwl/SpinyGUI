# Text Diagnostics Counter Contract

This document defines the opt-in counter contract used by E5 structural investigation. The core
vocabulary is `core-text-diagnostics-7`; the NanoVG vocabulary is
`nanovg-text-diagnostics-2`. Counter-only runners, renderer recordings, and optimization behavior are
separate work.

## Session lifecycle

- A counter-only operation creates one enabled `DiagnosticSession` with the exact core and/or backend
  vocabulary it needs. The creating thread owns that session.
- The owner calls `reset()` immediately before each operation/sample and `snapshot()` immediately
  after it. Reset and snapshot are explicit boundaries; snapshots are immutable and later reset or
  increments cannot alter them.
- Enabled sessions reject increment, reset, or snapshot calls from another thread. Counts are never
  aggregated across threads. This matches the application/UI-thread execution model and makes an
  accidental worker-thread path fail closed.
- `DiagnosticSession.disabled()` is one stable no-op singleton. Its hooks, reset, and snapshot are
  safe on any thread; snapshot returns one stable empty object, every counter reads as zero, and every
  saturation check is false.
- Enabled snapshots are closed over the session's declared vocabulary. Reading or checking saturation
  for any undeclared core/backend counter fails; it is never confused with a declared zero.
- Increments are non-negative. An enabled counter that would exceed `Long.MAX_VALUE` saturates there
  and its snapshot marks that counter as saturated. Reset clears both value and saturation state.
- Enabled sessions accept only their declared, stable counter IDs. Missing, duplicate, malformed,
  negative, or undeclared enabled-session inputs fail instead of silently changing vocabulary.
  IDs are lowercase dot-separated segments; each segment begins with a letter and may contain digits
  or single hyphen-separated components. Empty, leading/trailing, repeated, or mixed separators such
  as `a.`, `a..b`, `a.-b`, and `a--b` are invalid.

There is no implicit global/current session and no hidden per-thread aggregation. Measured components
accept only narrow constructor or service hooks: `TextMeasurer.diagnostics()` defaults to the disabled
singleton, `FontServiceImpl` accepts a session in its diagnostic constructor, and `NvgRenderer`
accepts a backend or combined-vocabulary session in its diagnostic constructor. Existing constructors
keep diagnostics disabled. Hooks increment enum constants directly; they do not snapshot, create
result objects or per-operation diagnostic collections, synchronize, or reorder native calls.
The disabled-cost contract is executable: after warmup, the test JVM's thread-allocation counter must
report zero bytes across one million repeated disabled increment/add/reset/read hooks, and the measured
hook methods must not carry the JVM synchronized-method modifier.

## Attribution and nesting

Counters record events, not inferred elapsed-time regions. Nested/default/delegated calls are
additive and are not deduplicated:

- Every entered `TextMeasurer` signature increments its own `core.text-measurer.*.entries` counter,
  including the four interface default methods.
- A default method that enters another overload increments both entry counters. Only the code that
  completes an underlying full measurement increments `core.text.complete-measurements`.
- One logical source glyph request increments `core.text.logical-glyph-resolutions` once. Every
  primary/fallback/replacement native candidate query increments
  `core.text.native-glyph-index-probes`; the two values deliberately need not match.
- Complete input and textarea layouts are independent counters and are not inferred from the number
  of `TextMeasurer` calls they contain.

## Core vocabulary

`TextDiagnosticCounter` is authoritative. Its stable IDs cover:

- Source and font work: source code points scanned, logical glyph resolutions, native glyph-index
  probes, native advance calls, native kerning calls, normalization scans, and font-chain resolutions.
- Materialization work: copied and moved glyph slots plus separate append/freeze counts for
  character, run, glyph-slot, private pre-wrap line, UTF-16 caret-boundary, and raw/rebased advance
  slot builders, plus private prepared-result freezes and validated shared-source range
  preparations. Legacy range-adapter temporary `String` slices have a dedicated allocation counter;
  direct production capability dispatch must keep it at zero. Private wrap primitive visits count
  forward inspections used to select hard-line,
  word-boundary, and character-fallback ranges. Dedicated copy counters attribute slots to the
  initial resolved-primitive freeze or later range materialization.
- Final caret lookup: advance-midpoint and source-boundary comparisons performed by binary search
  over one immutable line-local caret-stop value.
- Completion work: complete underlying text measurements, complete input layouts, and complete
  textarea layouts.
- API entries: all nine currently declared `TextMeasurer` signatures—four list/default signatures and
  five single-font signatures—each with a distinct entry counter.

Copied slots mean data duplicated into another representation. Moved slots are explicit algorithmic
relocations between logical builder positions; internal `ArrayList` capacity growth is not observable
counter work. Appends count appended items and freezes count builder-to-immutable transitions. The
private resolved-primitive seam freezes its storage once and increments both the aggregate and
`initial-resolution` copy counters. Private pre-wrap line preparation retains primitive/run ranges,
line-local caret boundaries, and individual raw/rebased advance slots without copying glyph slots.
The active final range materializer increments the separate `range-materialization` copy counter, so
resolution and publication costs are not conflated. Production whole-string and range-capability
requests both route through `PreparedRange`, private wrapping, and one final materialization. Each
increments `range-preparations` and `complete-measurements` once; the direct capability adds no
public `TextMeasurer` entry, while public whole-string requests record the signatures they actually
enter through normal overload/default delegation.
The legacy range-adapter fallback increments `range-temporary-strings` exactly once before allocating
a selected-range string and records the public entry of the wrapped legacy measurer. Production
whole-string and direct-capability range paths never increment it. Normalization counts complete
scans, while source scanning counts
visited code points. P3's private wrapping planner
increments `wrap.primitive-visits` once per inspected resolved primitive, including both halves of an
atomic CRLF marker, and does not increment glyph-slot move or range-materialization copy counters.
Direct final advance-midpoint and source-index caret-stop lookups increment only
`caret-stop-search-comparisons`; they perform no source scan, resolution, native measurement,
substring, or complete measurement. A public `FontServiceImpl` caret request prepares and completes
one measurement before performing that final lookup, so it records the normal range preparation,
resolution, final materialization, and complete-measurement work exactly once.

## NanoVG vocabulary

`NvgDiagnosticCounter` is authoritative and backend-local. Its stable IDs cover:

- UTF-8 payload bytes, allocation calls, and allocated bytes. Payload excludes a terminator;
  allocated bytes include terminators or spare capacity.
- NanoVG text, face, size, fill-color, alignment, save, restore, scissor, intersect-scissor,
  reset-scissor, affine-transform, and translate calls, plus aggregate and path-specific face-selection
  failures.
- Separate considered/submitted/culled counts for normal text, input text, textarea visual lines, and
  textarea text items after line retention.
- A separate `outside-effective-clip` cull-reason counter for each path/gate. A reason is incremented
  only when the corresponding item is actually culled; uncertain or boundary items remain submitted.

For normal, input, and post-line textarea text items, every considered item has exactly one terminal
outcome: `considered = submitted + culled + face-selection-failed`. Face-selection failure is not a
clip cull and never increments a cull counter or reason. The generic face-failure counter is the sum of
the three path-specific terminal counters. Textarea lines do not select faces, so their independent
gate remains `considered = submitted + culled`. For every approved culling gate, `culled` equals the
sum of that gate's reason counters. Textarea line decisions and post-line text-item decisions never
share a counter. These invariants allow future structural recordings to reconcile commands without
coupling core APIs to NanoVG or native handles.
