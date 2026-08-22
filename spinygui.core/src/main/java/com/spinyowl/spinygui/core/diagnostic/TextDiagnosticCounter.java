package com.spinyowl.spinygui.core.diagnostic;

/** Backend-neutral text, font, builder, and editable-control diagnostic vocabulary. */
public enum TextDiagnosticCounter implements DiagnosticCounter {
  SOURCE_CODE_POINTS_SCANNED(
      "core.text.source-code-points-scanned",
      DiagnosticUnit.CODE_POINTS,
      "Source Unicode code points visited by text processing."),
  LOGICAL_GLYPH_RESOLUTIONS(
      "core.text.logical-glyph-resolutions",
      DiagnosticUnit.GLYPHS,
      "Logical source glyph requests resolved, independent of candidate font probes."),
  NATIVE_GLYPH_INDEX_PROBES(
      "core.text.native-glyph-index-probes",
      DiagnosticUnit.CALLS,
      "Native glyph-index probes across primary, fallback, and replacement candidates."),
  NATIVE_GLYPH_ADVANCE_CALLS(
      "core.text.native-glyph-advance-calls",
      DiagnosticUnit.CALLS,
      "Native glyph horizontal-metrics calls."),
  NATIVE_KERNING_CALLS(
      "core.text.native-kerning-calls",
      DiagnosticUnit.CALLS,
      "Native pair-kerning calls."),
  GLYPH_SLOTS_COPIED(
      "core.text.glyph-slots-copied",
      DiagnosticUnit.GLYPH_SLOTS,
      "Glyph slots copied into a distinct durable or intermediate representation."),
  GLYPH_SLOTS_MOVED(
      "core.text.glyph-slots-moved",
      DiagnosticUnit.GLYPH_SLOTS,
      "Glyph slots shifted or relocated within mutable construction storage."),
  INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED(
      "core.text.initial-resolution.glyph-slots-copied",
      DiagnosticUnit.GLYPH_SLOTS,
      "Resolved glyph slots copied when initial source resolution storage is frozen."),
  RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED(
      "core.text.range-materialization.glyph-slots-copied",
      DiagnosticUnit.GLYPH_SLOTS,
      "Resolved glyph slots copied while materializing later private or public ranges."),
  CHARACTER_BUILDER_APPENDS(
      "core.text.character-builder-appends",
      DiagnosticUnit.ITEMS,
      "Resolved-character items appended to a builder."),
  CHARACTER_BUILDER_FREEZES(
      "core.text.character-builder-freezes",
      DiagnosticUnit.CALLS,
      "Resolved-character builders frozen into immutable output."),
  RUN_BUILDER_APPENDS(
      "core.text.run-builder-appends",
      DiagnosticUnit.ITEMS,
      "Resolved-run items appended to a builder."),
  RUN_BUILDER_FREEZES(
      "core.text.run-builder-freezes",
      DiagnosticUnit.CALLS,
      "Resolved-run builders frozen into immutable output."),
  GLYPH_SLOT_BUILDER_APPENDS(
      "core.text.glyph-slot-builder-appends",
      DiagnosticUnit.GLYPH_SLOTS,
      "Glyph slots appended to a builder."),
  GLYPH_SLOT_BUILDER_FREEZES(
      "core.text.glyph-slot-builder-freezes",
      DiagnosticUnit.CALLS,
      "Glyph-slot builders frozen into immutable output."),
  LINE_BUILDER_APPENDS(
      "core.text.line-builder-appends",
      DiagnosticUnit.ITEMS,
      "Private pre-wrap lines appended to a result builder."),
  LINE_BUILDER_FREEZES(
      "core.text.line-builder-freezes",
      DiagnosticUnit.CALLS,
      "Private pre-wrap line builders frozen into immutable output."),
  CARET_BOUNDARY_BUILDER_APPENDS(
      "core.text.caret-boundary-builder-appends",
      DiagnosticUnit.ITEMS,
      "UTF-16 caret boundaries appended to private line storage."),
  CARET_BOUNDARY_BUILDER_FREEZES(
      "core.text.caret-boundary-builder-freezes",
      DiagnosticUnit.CALLS,
      "Private caret-boundary builders frozen into immutable output."),
  ADVANCE_SLOT_BUILDER_APPENDS(
      "core.text.advance-slot-builder-appends",
      DiagnosticUnit.ITEMS,
      "Raw or line-start-rebased glyph advance slots appended to private storage."),
  ADVANCE_SLOT_BUILDER_FREEZES(
      "core.text.advance-slot-builder-freezes",
      DiagnosticUnit.CALLS,
      "Private raw or rebased advance-slot builders frozen into immutable output."),
  RESULT_BUILDER_FREEZES(
      "core.text.result-builder-freezes",
      DiagnosticUnit.CALLS,
      "Private prepared-measurement result builders frozen into immutable output."),
  RANGE_PREPARATIONS(
      "core.text.range-preparations",
      DiagnosticUnit.CALLS,
      "Validated shared-source ranges prepared into private measurement storage."),
  RANGE_TEMPORARY_STRINGS(
      "core.text.range-temporary-strings",
      DiagnosticUnit.CALLS,
      "Temporary String slices allocated by the legacy range-adapter fallback."),
  WRAP_PRIMITIVE_VISITS(
      "core.text.wrap.primitive-visits",
      DiagnosticUnit.ITEMS,
      "Resolved primitives inspected while selecting private wrap and hard-line ranges."),
  CARET_STOP_SEARCH_COMPARISONS(
      "core.text.caret-stop-search-comparisons",
      DiagnosticUnit.ITEMS,
      "Advance-midpoint or source-boundary comparisons performed by final line caret lookup."),
  NORMALIZATION_SCANS(
      "core.text.normalization-scans",
      DiagnosticUnit.CALLS,
      "Complete source normalization scans."),
  INLINE_PREPARED_CODE_POINTS_APPENDED(
      "core.text.inline-preparation.code-points-appended",
      DiagnosticUnit.CODE_POINTS,
      "Normalized code points appended during inline preparation."),
  INLINE_PREPARATION_FREEZES(
      "core.text.inline-preparation.freezes",
      DiagnosticUnit.CALLS,
      "Immutable prepared inline text values frozen after one source scan."),
  INLINE_PREPARED_RANGES(
      "core.text.inline-preparation.ranges",
      DiagnosticUnit.ITEMS,
      "Code-point-safe text, whitespace, and forced-break ranges prepared for inline layout."),
  INLINE_RANGE_CODE_POINT_VISITS(
      "core.text.inline-ranges.code-point-visits",
      DiagnosticUnit.CODE_POINTS,
      "Prepared code points visited while splitting or materializing inline ranges."),
  INLINE_TEMPORARY_UNITS(
      "core.text.inline-ranges.temporary-units",
      DiagnosticUnit.ITEMS,
      "Legacy per-code-point temporary inline-unit objects allocated during range traversal."),
  INLINE_MEASUREMENT_RANGE_CALLS(
      "core.text.inline-measurement.range-calls",
      DiagnosticUnit.CALLS,
      "Distinct prepared ranges measured during one inline layout pass."),
  INLINE_MEASUREMENT_REUSES(
      "core.text.inline-measurement.reuses",
      DiagnosticUnit.CALLS,
      "Compatible prepared-range measurement results reused during one inline layout pass."),
  INLINE_DURABLE_TEXT_STRINGS(
      "core.text.inline-output.durable-text-strings",
      DiagnosticUnit.ITEMS,
      "Durable Strings materialized for text-bearing inline fragments."),
  INLINE_NULL_TEXT_FRAGMENTS(
      "core.text.inline-output.null-text-fragments",
      DiagnosticUnit.ITEMS,
      "Durable spacer, element, or union fragments emitted without text materialization."),
  INLINE_PASS_CLEANUPS(
      "core.text.inline-pass.cleanups",
      DiagnosticUnit.CALLS,
      "Pass-local inline preparation and reuse state dropped after success or failure."),
  FONT_CHAIN_RESOLUTIONS(
      "core.text.font-chain-resolutions",
      DiagnosticUnit.CALLS,
      "Ordered font-chain resolver invocations."),
  COMPLETE_TEXT_MEASUREMENTS(
      "core.text.complete-measurements",
      DiagnosticUnit.CALLS,
      "Complete underlying text measurement/layout passes, excluding API delegation."),
  INPUT_COMPLETE_LAYOUTS(
      "core.control.input-complete-layouts",
      DiagnosticUnit.CALLS,
      "Complete single-line input text/control layout builds."),
  TEXTAREA_COMPLETE_LAYOUTS(
      "core.control.textarea-complete-layouts",
      DiagnosticUnit.CALLS,
      "Complete textarea text/control layout builds."),
  TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES(
      "core.text-measurer.measure-text-font-list.entries",
      DiagnosticUnit.CALLS,
      "Calls to measureText(String,List<Font>,float,float), including its default body."),
  TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES(
      "core.text-measurer.measure-text-font-list-full.entries",
      DiagnosticUnit.CALLS,
      "Calls to the full measureText overload that accepts an ordered font list."),
  TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES(
      "core.text-measurer.get-text-line-metrics-font-list.entries",
      DiagnosticUnit.CALLS,
      "Calls to getTextLineMetrics(String,List<Font>,float,float), including its default body."),
  TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES(
      "core.text-measurer.get-text-caret-metrics-font-list.entries",
      DiagnosticUnit.CALLS,
      "Calls to getTextCaretMetrics(String,List<Font>,float,float), including its default body."),
  TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES(
      "core.text-measurer.measure-text-font.entries",
      DiagnosticUnit.CALLS,
      "Calls to measureText(String,Font,float,float)."),
  TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES(
      "core.text-measurer.measure-text-font-full.entries",
      DiagnosticUnit.CALLS,
      "Calls to the full measureText overload that accepts one font."),
  TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES(
      "core.text-measurer.get-text-metrics-font.entries",
      DiagnosticUnit.CALLS,
      "Calls to the full getTextMetrics overload that accepts one font."),
  TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES(
      "core.text-measurer.get-text-line-metrics-font.entries",
      DiagnosticUnit.CALLS,
      "Calls to getTextLineMetrics(String,Font,float,float)."),
  TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES(
      "core.text-measurer.get-text-caret-metrics-font.entries",
      DiagnosticUnit.CALLS,
      "Calls to getTextCaretMetrics(String,Font,float,float)."),
  ;

  public static final String VOCABULARY_VERSION = "core-text-diagnostics-8";

  private final String id;
  private final DiagnosticUnit unit;
  private final String description;

  TextDiagnosticCounter(String id, DiagnosticUnit unit, String description) {
    this.id = id;
    this.unit = unit;
    this.description = description;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public DiagnosticUnit unit() {
    return unit;
  }

  @Override
  public String description() {
    return description;
  }
}
