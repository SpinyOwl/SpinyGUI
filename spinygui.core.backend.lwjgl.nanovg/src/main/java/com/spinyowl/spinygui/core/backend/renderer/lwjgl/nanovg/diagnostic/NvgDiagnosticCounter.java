package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticUnit;

/** NanoVG/UTF-8 submission, state, and path-specific culling diagnostic vocabulary. */
public enum NvgDiagnosticCounter implements DiagnosticCounter {
  RENDER_NODE_VISITS(
      "nanovg.render.node-visits",
      DiagnosticUnit.ITEMS,
      "Layout-tree nodes visited by the immediate-mode NanoVG renderer."),
  UTF8_PAYLOAD_BYTES(
      "nanovg.utf8.payload-bytes",
      DiagnosticUnit.BYTES,
      "UTF-8 payload bytes encoded, excluding any native terminator."),
  UTF8_ALLOCATION_CALLS(
      "nanovg.utf8.allocation-calls",
      DiagnosticUnit.CALLS,
      "Native or direct UTF-8 staging allocation calls."),
  UTF8_ALLOCATED_BYTES(
      "nanovg.utf8.allocated-bytes",
      DiagnosticUnit.BYTES,
      "Bytes reserved by UTF-8 staging allocations, including terminators or spare capacity."),
  TEXT_CALLS(
      "nanovg.calls.text",
      DiagnosticUnit.CALLS,
      "NanoVG text submission calls."),
  FONT_FACE_CALLS(
      "nanovg.calls.font-face",
      DiagnosticUnit.CALLS,
      "NanoVG font-face selection calls."),
  FONT_FACE_FAILURES(
      "nanovg.results.font-face-failures",
      DiagnosticUnit.ITEMS,
      "Font-face selections that could not resolve a usable NanoVG face."),
  FONT_SIZE_CALLS(
      "nanovg.calls.font-size",
      DiagnosticUnit.CALLS,
      "NanoVG font-size state calls."),
  FILL_COLOR_CALLS(
      "nanovg.calls.fill-color",
      DiagnosticUnit.CALLS,
      "NanoVG fill-color state calls used by text paths."),
  TEXT_ALIGN_CALLS(
      "nanovg.calls.text-align",
      DiagnosticUnit.CALLS,
      "NanoVG text-alignment state calls."),
  SAVE_CALLS(
      "nanovg.calls.save",
      DiagnosticUnit.CALLS,
      "NanoVG state-save calls."),
  RESTORE_CALLS(
      "nanovg.calls.restore",
      DiagnosticUnit.CALLS,
      "NanoVG state-restore calls."),
  SCISSOR_CALLS(
      "nanovg.calls.scissor",
      DiagnosticUnit.CALLS,
      "NanoVG scissor replacement calls."),
  INTERSECT_SCISSOR_CALLS(
      "nanovg.calls.intersect-scissor",
      DiagnosticUnit.CALLS,
      "NanoVG intersect-scissor calls."),
  RESET_SCISSOR_CALLS(
      "nanovg.calls.reset-scissor",
      DiagnosticUnit.CALLS,
      "NanoVG reset-scissor calls."),
  TRANSFORM_CALLS(
      "nanovg.calls.transform",
      DiagnosticUnit.CALLS,
      "NanoVG affine transform multiplication calls."),
  TRANSLATE_CALLS(
      "nanovg.calls.translate",
      DiagnosticUnit.CALLS,
      "NanoVG translation state calls."),
  NORMAL_TEXT_ITEMS_CONSIDERED(
      "nanovg.normal-text.items-considered",
      DiagnosticUnit.ITEMS,
      "Normal-text submissions considered before any culling gate."),
  NORMAL_TEXT_ITEMS_SUBMITTED(
      "nanovg.normal-text.items-submitted",
      DiagnosticUnit.ITEMS,
      "Normal-text submissions sent to NanoVG."),
  NORMAL_TEXT_ITEMS_CULLED(
      "nanovg.normal-text.items-culled",
      DiagnosticUnit.ITEMS,
      "Normal-text submissions omitted by an approved culling gate."),
  NORMAL_TEXT_ITEMS_FACE_SELECTION_FAILED(
      "nanovg.normal-text.items-face-selection-failed",
      DiagnosticUnit.ITEMS,
      "Normal-text submissions terminated because no usable NanoVG face resolved."),
  NORMAL_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP(
      "nanovg.normal-text.cull-reason.outside-effective-clip",
      DiagnosticUnit.ITEMS,
      "Normal-text submissions culled because conservative ink is outside the effective clip."),
  INPUT_TEXT_ITEMS_CONSIDERED(
      "nanovg.input-text.items-considered",
      DiagnosticUnit.ITEMS,
      "Input text/run submissions considered before any culling gate."),
  INPUT_TEXT_ITEMS_SUBMITTED(
      "nanovg.input-text.items-submitted",
      DiagnosticUnit.ITEMS,
      "Input text/run submissions sent to NanoVG."),
  INPUT_TEXT_ITEMS_CULLED(
      "nanovg.input-text.items-culled",
      DiagnosticUnit.ITEMS,
      "Input text/run submissions omitted by an approved culling gate."),
  INPUT_TEXT_ITEMS_FACE_SELECTION_FAILED(
      "nanovg.input-text.items-face-selection-failed",
      DiagnosticUnit.ITEMS,
      "Input text/run submissions terminated because no usable NanoVG face resolved."),
  INPUT_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP(
      "nanovg.input-text.cull-reason.outside-effective-clip",
      DiagnosticUnit.ITEMS,
      "Input text/run submissions culled because conservative ink is outside the effective clip."),
  TEXTAREA_LINES_CONSIDERED(
      "nanovg.textarea.lines-considered",
      DiagnosticUnit.ITEMS,
      "Textarea visual lines considered before the independent line-culling gate."),
  TEXTAREA_LINES_SUBMITTED(
      "nanovg.textarea.lines-submitted",
      DiagnosticUnit.ITEMS,
      "Textarea visual lines retained for text submission."),
  TEXTAREA_LINES_CULLED(
      "nanovg.textarea.lines-culled",
      DiagnosticUnit.ITEMS,
      "Textarea visual lines omitted by an approved line-culling gate."),
  TEXTAREA_LINES_CULLED_OUTSIDE_EFFECTIVE_CLIP(
      "nanovg.textarea.line-cull-reason.outside-effective-clip",
      DiagnosticUnit.ITEMS,
      "Textarea lines culled because conservative line ink is outside the effective clip."),
  TEXTAREA_TEXT_ITEMS_CONSIDERED(
      "nanovg.textarea.text-items-considered",
      DiagnosticUnit.ITEMS,
      "Textarea text/run submissions considered after line retention."),
  TEXTAREA_TEXT_ITEMS_SUBMITTED(
      "nanovg.textarea.text-items-submitted",
      DiagnosticUnit.ITEMS,
      "Textarea text/run submissions sent to NanoVG."),
  TEXTAREA_TEXT_ITEMS_CULLED(
      "nanovg.textarea.text-items-culled",
      DiagnosticUnit.ITEMS,
      "Textarea text/run submissions omitted by an approved general culling gate."),
  TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED(
      "nanovg.textarea.text-items-face-selection-failed",
      DiagnosticUnit.ITEMS,
      "Textarea text/run submissions terminated because no usable NanoVG face resolved."),
  TEXTAREA_TEXT_CULLED_OUTSIDE_EFFECTIVE_CLIP(
      "nanovg.textarea.text-cull-reason.outside-effective-clip",
      DiagnosticUnit.ITEMS,
      "Textarea text/run submissions culled because conservative ink is outside the effective clip."),
  ;

  public static final String VOCABULARY_VERSION = "nanovg-text-diagnostics-3";

  private final String id;
  private final DiagnosticUnit unit;
  private final String description;

  NvgDiagnosticCounter(String id, DiagnosticUnit unit, String description) {
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
