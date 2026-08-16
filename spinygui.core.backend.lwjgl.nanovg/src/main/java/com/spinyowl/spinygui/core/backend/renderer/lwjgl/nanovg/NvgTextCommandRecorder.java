package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** In-memory command sink. It follows the production render path; it does not recreate it. */
final class NvgTextCommandRecorder implements NvgTextCommandSink {
  private static final long RECORDING_CONTEXT = 1L;

  private final Predicate<Font> availableFaces;
  private final DiagnosticSession diagnostics;
  private final NvgFontRegistry fontRegistry = new NvgFontRegistry();
  private final List<NvgTextCommand> commands = new ArrayList<>();

  NvgTextCommandRecorder() {
    this(font -> true, DiagnosticSession.disabled());
  }

  NvgTextCommandRecorder(Predicate<Font> availableFaces) {
    this(availableFaces, DiagnosticSession.disabled());
  }

  NvgTextCommandRecorder(DiagnosticSession diagnostics) {
    this(font -> true, diagnostics);
  }

  NvgTextCommandRecorder(
      Predicate<Font> availableFaces, DiagnosticSession diagnostics) {
    this.availableFaces = availableFaces;
    this.diagnostics = diagnostics;
  }

  List<NvgTextCommand> commands() {
    return List.copyOf(commands);
  }

  List<NvgTextCommand> snapshot() {
    return commands();
  }

  void reset() {
    commands.clear();
  }

  @Override
  public void beginScope(long context, NvgTextCommand.TextPath path) {
    diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
    commands.add(new NvgTextCommand.Scope(path, true));
  }

  @Override
  public void endScope(long context, NvgTextCommand.TextPath path) {
    diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
    commands.add(new NvgTextCommand.Scope(path, false));
  }

  @Override
  public void scissor(long context, float x, float y, float width, float height) {
    diagnostics.increment(NvgDiagnosticCounter.SCISSOR_CALLS);
    commands.add(new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.SCISSOR, x, y, width, height));
  }

  @Override
  public void intersectScissor(long context, float x, float y, float width, float height) {
    diagnostics.increment(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS);
    commands.add(new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, x, y, width, height));
  }

  @Override
  public void resetScissor(long context) {
    diagnostics.increment(NvgDiagnosticCounter.RESET_SCISSOR_CALLS);
    commands.add(new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.RESET, 0, 0, 0, 0));
  }

  @Override
  public void beginTransform(long context) {
    diagnostics.increment(NvgDiagnosticCounter.SAVE_CALLS);
    commands.add(new NvgTextCommand.TransformScope(true));
  }

  @Override
  public void transform(long context, float a, float b, float c, float d, float tx, float ty) {
    diagnostics.increment(NvgDiagnosticCounter.TRANSFORM_CALLS);
    commands.add(new NvgTextCommand.Transform(a, b, c, d, tx, ty));
  }

  @Override
  public void translate(long context, float x, float y) {
    diagnostics.increment(NvgDiagnosticCounter.TRANSLATE_CALLS);
    commands.add(new NvgTextCommand.Translate(x, y));
  }

  @Override
  public void endTransform(long context) {
    diagnostics.increment(NvgDiagnosticCounter.RESTORE_CALLS);
    commands.add(new NvgTextCommand.TransformScope(false));
  }

  @Override
  public void align(long context, int value) {
    diagnostics.increment(NvgDiagnosticCounter.TEXT_ALIGN_CALLS);
    commands.add(new NvgTextCommand.Alignment(value));
  }

  @Override
  public boolean selectFace(long context, NvgTextCommand.TextPath path, Font font) {
    boolean selected = availableFaces.test(font);
    if (selected) {
      diagnostics.increment(NvgDiagnosticCounter.FONT_FACE_CALLS);
    }
    commands.add(new NvgTextCommand.Face(path, font, selected));
    return selected;
  }

  @Override
  public String displayText(long context, Font font, String text) {
    return fontRegistry.displayText(context == 0 ? RECORDING_CONTEXT : context, font, text);
  }

  @Override
  public void fontSize(long context, float value) {
    diagnostics.increment(NvgDiagnosticCounter.FONT_SIZE_CALLS);
    commands.add(new NvgTextCommand.FontSize(value));
  }

  @Override
  public void fillColor(long context, Color color) {
    diagnostics.increment(NvgDiagnosticCounter.FILL_COLOR_CALLS);
    commands.add(new NvgTextCommand.FillColor(color));
  }

  @Override
  public void text(long context, NvgTextCommand.TextPath path, String text, float x, float baseline) {
    diagnostics.increment(NvgDiagnosticCounter.TEXT_CALLS);
    commands.add(
        new NvgTextCommand.Text(
            path, text, text.getBytes(StandardCharsets.UTF_8).length, x, baseline));
  }

  @Override
  public void advance(NvgTextCommand.TextPath path, float x, float advance) {
    commands.add(new NvgTextCommand.Advance(path, x, advance));
  }

  @Override
  public void selection(long context, float x, float y, float width, float height, Color color) {
    commands.add(new NvgTextCommand.Selection(x, y, width, height, color));
  }

  @Override
  public void caret(long context, float x, float y, float width, float height, Color color) {
    commands.add(new NvgTextCommand.Caret(x, y, width, height, color));
  }

  @Override
  public void outcome(NvgTextCommand.TextPath path, NvgDiagnosticCounter counter) {
    commands.add(new NvgTextCommand.Outcome(path, counter.id()));
  }
}
