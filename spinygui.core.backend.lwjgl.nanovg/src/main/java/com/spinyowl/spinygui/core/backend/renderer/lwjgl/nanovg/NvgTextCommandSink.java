package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.types.Color;

interface NvgTextCommandSink {
  void beginScope(long context, NvgTextCommand.TextPath path);
  void endScope(long context, NvgTextCommand.TextPath path);
  void scissor(long context, float x, float y, float width, float height);
  void intersectScissor(long context, float x, float y, float width, float height);
  void resetScissor(long context);
  void beginTransform(long context);
  void transform(long context, float a, float b, float c, float d, float tx, float ty);
  void translate(long context, float x, float y);
  void endTransform(long context);
  void align(long context, int value);
  boolean selectFace(long context, NvgTextCommand.TextPath path, Font font);
  String displayText(long context, Font font, String text);
  void fontSize(long context, float value);
  void fillColor(long context, Color color);
  void text(long context, NvgTextCommand.TextPath path, NvgRenderedText text, float x, float baseline);
  void advance(NvgTextCommand.TextPath path, float x, float advance);
  void selection(long context, float x, float y, float width, float height, Color color);
  void caret(long context, float x, float y, float width, float height, Color color);
  void outcome(NvgTextCommand.TextPath path, NvgDiagnosticCounter counter);

  /** Invalidates any backend-local state knowledge after an unmediated native mutation boundary. */
  default void unknownMutation() {}
}
