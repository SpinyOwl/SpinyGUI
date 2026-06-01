package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil.create;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.nvgFillColor;
import static org.lwjgl.nanovg.NanoVG.nvgFontFace;
import static org.lwjgl.nanovg.NanoVG.nvgFontSize;
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;
import static org.lwjgl.nanovg.NanoVG.nvgText;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import java.nio.ByteBuffer;
import org.joml.Vector2f;

class NvgTextareaRenderer {

  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  private static final Color SELECTION_COLOR = new Color(59, 130, 246, 0.28f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final float CARET_WIDTH = 1.5f;

  private final NvgFontRegistry fontRegistry;
  private TextMeasurer textMeasurer;

  NvgTextareaRenderer() {
    this(new NvgFontRegistry());
  }

  NvgTextareaRenderer(NvgFontRegistry fontRegistry) {
    this.fontRegistry = fontRegistry;
  }

  void textMeasurer(TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  void render(TextareaElement textarea, long nanovgContext) {
    if (textMeasurer == null) {
      return;
    }
    MultilineTextControlMetrics metrics = new MultilineTextControlMetrics(textMeasurer);
    Vector2f contentPosition = metrics.contentPosition(textarea);
    Vector2f contentSize = textarea.box().contentSize();
    MultilineTextControlMetrics.TextStyle textStyle = metrics.textStyle(textarea);

    createScissor(nanovgContext, textarea);
    nvgSave(nanovgContext);
    nvgIntersectScissor(
        nanovgContext, contentPosition.x(), contentPosition.y(), contentSize.x(), contentSize.y());
    if (textarea.hasSelection()) {
      drawSelection(textarea, nanovgContext, metrics, contentPosition);
    }
    drawLines(textarea, nanovgContext, metrics, textStyle, contentPosition);
    if (textarea.focused()) {
      drawCaret(textarea, nanovgContext, metrics, contentPosition);
    }
    nvgRestore(nanovgContext);
    resetScissor(nanovgContext);
  }

  private void drawLines(
      TextareaElement textarea,
      long nanovgContext,
      MultilineTextControlMetrics metrics,
      MultilineTextControlMetrics.TextStyle textStyle,
      Vector2f contentPosition) {
    String fontFace = fontRegistry.fontFace(textStyle.font(), nanovgContext);
    if (fontFace == null) {
      return;
    }
    nvgFontFace(nanovgContext, fontFace);
    nvgFontSize(nanovgContext, textStyle.fontSize());
    nvgTextAlign(nanovgContext, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
    try (var nvgColor = create(color(textarea.resolvedStyle()))) {
      nvgFillColor(nanovgContext, nvgColor);
      for (MultilineTextControlMetrics.Line line : metrics.lines(textarea)) {
        float baseline =
            contentPosition.y() - textarea.textScrollTop() + line.y() + line.baseline();
        ByteBuffer textBuffer = memUTF8(line.text(), false);
        try {
          nvgText(
              nanovgContext,
              contentPosition.x() - textarea.textScrollLeft(),
              baseline,
              textBuffer);
        } finally {
          memFree(textBuffer);
        }
      }
    }
  }

  private void drawCaret(
      TextareaElement textarea,
      long nanovgContext,
      MultilineTextControlMetrics metrics,
      Vector2f contentPosition) {
    MultilineTextControlMetrics.Caret caret = metrics.caret(textarea, textarea.caretIndex());
    drawRect(
        nanovgContext,
        new Vector2f(
            contentPosition.x() - textarea.textScrollLeft() + caret.x(),
            contentPosition.y() - textarea.textScrollTop() + caret.y()),
        new Vector2f(CARET_WIDTH, caret.height()),
        CARET_COLOR);
  }

  private void drawSelection(
      TextareaElement textarea,
      long nanovgContext,
      MultilineTextControlMetrics metrics,
      Vector2f contentPosition) {
    int start = textarea.selectionStart();
    int end = textarea.selectionEnd();
    for (MultilineTextControlMetrics.Line line : metrics.lines(textarea)) {
      int lineStart = Math.max(start, line.startIndex());
      int lineEnd = Math.min(end, line.endIndex());
      if (lineEnd <= lineStart) {
        continue;
      }
      MultilineTextControlMetrics.Caret startCaret = metrics.caret(textarea, lineStart);
      MultilineTextControlMetrics.Caret endCaret = metrics.caret(textarea, lineEnd);
      drawRect(
          nanovgContext,
          new Vector2f(
              contentPosition.x() - textarea.textScrollLeft() + startCaret.x(),
              contentPosition.y() - textarea.textScrollTop() + line.y()),
          new Vector2f(endCaret.x() - startCaret.x(), line.height()),
          SELECTION_COLOR);
    }
  }

  private Color color(ResolvedStyle style) {
    return style.color() == null ? DEFAULT_TEXT_COLOR : style.color();
  }
}
