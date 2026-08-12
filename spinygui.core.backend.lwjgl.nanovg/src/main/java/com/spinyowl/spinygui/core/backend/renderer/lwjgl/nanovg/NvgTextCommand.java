package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.font.Font;

/** Ordered, backend-local observation of text-related NanoVG commands. */
sealed interface NvgTextCommand {
  enum TextPath { NORMAL, INPUT, TEXTAREA }
  record Scope(TextPath path, boolean begin) implements NvgTextCommand {}

  enum ClipOperation { SCISSOR, INTERSECT, RESET }

  record Clip(ClipOperation operation, float x, float y, float width, float height)
      implements NvgTextCommand {}

  record Transform(float a, float b, float c, float d, float tx, float ty)
      implements NvgTextCommand {}

  record Translate(float x, float y) implements NvgTextCommand {}

  record TransformScope(boolean begin) implements NvgTextCommand {}

  record Alignment(int value) implements NvgTextCommand {}

  record Face(TextPath path, Font font, boolean selected) implements NvgTextCommand {}

  record FontSize(float value) implements NvgTextCommand {}

  record FillColor(Color value) implements NvgTextCommand {}

  record Text(TextPath path, String value, int utf8Bytes, float x, float baseline)
      implements NvgTextCommand {}

  record Advance(TextPath path, float x, float value) implements NvgTextCommand {}

  record Selection(float x, float y, float width, float height, Color color)
      implements NvgTextCommand {}

  record Caret(float x, float y, float width, float height, Color color)
      implements NvgTextCommand {}

  record Outcome(TextPath path, String diagnosticId) implements NvgTextCommand {}

  record Cull(TextPath path, String diagnosticId) implements NvgTextCommand {}
}
