# Text Diagnostics Counter Contract

This document defines the opt-in counter contract used by E5 structural investigation. The core
vocabulary is `core-text-diagnostics-1`; the NanoVG vocabulary is
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
  character, run, and glyph-slot builders.
- Completion work: complete underlying text measurements, complete input layouts, and complete
  textarea layouts.
- API entries: all nine currently declared `TextMeasurer` signatures—four list/default signatures and
  five single-font signatures—each with a distinct entry counter.

Copied slots mean data duplicated into another representation. In the current run assembly this
includes both the copy into an exact-sized mutable `ArrayList` and the later immutable freeze copy.
Moved slots mean existing mutable storage shifted or relocated, including the exact-sized list's
existing slots relocated when the following append grows its storage. Appends count appended items;
freezes count builder-to-immutable transitions. Normalization counts complete scans, while source
scanning counts visited code points.

Current `resolveRuns` has no resolved-character builder. Its mutable run list records run appends but
is returned directly, so it records no run-builder freeze. A new run's singleton `List.of` glyph list
is constructed immutable and is not a glyph-slot builder append/freeze. Extending an existing run does
append one glyph to mutable construction storage and then freezes that storage through
`ResolvedTextRun`'s immutable copy, so only that extension records glyph-slot append/freeze events.

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
