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
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.joml.Vector2f;

class NvgInputRenderer {

  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  private static final Color SELECTION_COLOR = new Color(59, 130, 246, 0.28f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final float CARET_WIDTH = 1.5f;

  private final InputStateSink stateSink;
  private final InputSelectionSink selectionSink;
  private final InputTextSink textSink;
  private final InputCaretSink caretSink;
  private TextMeasurer textMeasurer;

  NvgInputRenderer() {
    this(new NvgFontRegistry());
  }

  NvgInputRenderer(NvgFontRegistry fontRegistry) {
    this(
        new NanoVgInputStateSink(),
        new NanoVgInputSelectionSink(),
        new NanoVgInputTextSink(fontRegistry),
        new NanoVgInputCaretSink());
  }

  NvgInputRenderer(
      InputStateSink stateSink,
      InputSelectionSink selectionSink,
      InputTextSink textSink,
      InputCaretSink caretSink) {
    this.stateSink = stateSink;
    this.selectionSink = selectionSink;
    this.textSink = textSink;
    this.caretSink = caretSink;
  }

  void textMeasurer(TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  void render(InputElement input, long nanovgContext) {
    if ((!input.textInput() && !input.buttonInput()) || textMeasurer == null) {
      return;
    }

    TextGeometry geometry = textGeometry(input);
    stateSink.begin(nanovgContext, input, geometry.contentPosition(), geometry.contentSize());
    if (input.buttonInput()) {
      drawValue(input, nanovgContext, geometry);
      stateSink.end(nanovgContext);
      return;
    }

    if (input.hasSelection()) {
      drawSelection(input, nanovgContext, geometry);
    }
    drawValue(input, nanovgContext, geometry);
    if (input.focused()) {
      drawCaret(input, nanovgContext, geometry);
    }
    stateSink.end(nanovgContext);
  }

  private void drawValue(InputElement input, long nanovgContext, TextGeometry geometry) {
    textSink.drawText(
        nanovgContext,
        input.value(),
        geometry.font(),
        geometry.fontSize(),
        geometry.color(),
        geometry.textX(),
        geometry.baseline());
  }

  private void drawSelection(InputElement input, long nanovgContext, TextGeometry geometry) {
    float selectionStartX = textX(input, input.selectionStart(), geometry);
    float selectionEndX = textX(input, input.selectionEnd(), geometry);
    if (selectionEndX <= selectionStartX) {
      return;
    }
    selectionSink.drawSelection(
        nanovgContext,
        geometry.textX() + selectionStartX,
        geometry.lineTop(),
        selectionEndX - selectionStartX,
        geometry.lineHeight());
  }

  private void drawCaret(InputElement input, long nanovgContext, TextGeometry geometry) {
    float caretX = caretX(input, geometry);
    caretSink.drawCaret(
        nanovgContext,
        geometry.textX() + caretX,
        geometry.lineTop(),
        geometry.lineHeight());
  }

  private float caretX(InputElement input, TextGeometry geometry) {
    return textX(input, input.caretIndex(), geometry);
  }

  private float textX(InputElement input, int textIndex, TextGeometry geometry) {
    int safeTextIndex = Math.max(0, Math.min(textIndex, input.value().length()));
    TextLineMetrics line =
        textMeasurer.getTextLineMetrics(
            input.value().substring(0, safeTextIndex),
            geometry.font(),
            geometry.fontSize(),
            geometry.requestedLineHeight());
    return line.width();
  }

  private TextGeometry textGeometry(InputElement input) {
    ResolvedStyle style = input.resolvedStyle();
    Font font = findFont(style);
    float fontSize = fontSize(input);
    float lineHeight = lineHeight(style);
    TextLineMetrics line =
        textMeasurer.getTextLineMetrics(input.value(), font, fontSize, lineHeight);
    Vector2f contentPosition = contentPosition(input);
    Vector2f contentSize = input.box().contentSize();
    float lineTop = contentPosition.y() + Math.max(0, (contentSize.y() - line.height()) / 2f);
    return new TextGeometry(
        font,
        fontSize,
        color(style),
        contentPosition,
        contentSize,
        contentPosition.x() - textScrollLeft(input),
        lineTop,
        lineTop + line.baseline(),
        line.height(),
        lineHeight);
  }

  private float textScrollLeft(InputElement input) {
    return input.textInput() ? input.textScrollLeft() : 0;
  }

  private Vector2f contentPosition(InputElement input) {
    Vector2f position = input.absolutePosition();
    position.add(
        input.box().border().left() + input.box().padding().left(),
        input.box().border().top() + input.box().padding().top());
    return position;
  }

  private Font findFont(ResolvedStyle style) {
    Set<String> fontFamilies = style.fontFamilies();
    if (fontFamilies == null) {
      return Font.DEFAULT;
    }
    Set<Font> fonts =
        fontFamilies.stream()
            .filter(Font::hasFont)
            .map(f -> Font.find(f, style.fontStyle(), style.fontWeight()))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    return fonts.stream().findFirst().orElse(Font.DEFAULT);
  }

  private float fontSize(InputElement input) {
    Length<?> fontSize = input.resolvedStyle().fontSize();
    return fontSize == null ? 16f : StyleUtils.getFontSize(input);
  }

  private float lineHeight(ResolvedStyle style) {
    Float lineHeight = style.lineHeight();
    return lineHeight == null ? 1f : lineHeight;
  }

  private Color color(ResolvedStyle style) {
    return style.color() == null ? DEFAULT_TEXT_COLOR : style.color();
  }

  interface InputStateSink {
    void begin(long context, InputElement input, Vector2f contentPosition, Vector2f contentSize);

    void end(long context);
  }

  interface InputSelectionSink {
    void drawSelection(long context, float x, float y, float width, float height);
  }

  interface InputTextSink {
    void drawText(
        long context,
        String text,
        Font font,
        float fontSize,
        Color color,
        float x,
        float baseline);
  }

  interface InputCaretSink {
    void drawCaret(long context, float x, float y, float height);
  }

  private record TextGeometry(
      Font font,
      float fontSize,
      Color color,
      Vector2f contentPosition,
      Vector2f contentSize,
      float textX,
      float lineTop,
      float baseline,
      float lineHeight,
      float requestedLineHeight) {}

  private static final class NanoVgInputStateSink implements InputStateSink {
    @Override
    public void begin(
        long context, InputElement input, Vector2f contentPosition, Vector2f contentSize) {
      createScissor(context, input);
      nvgSave(context);
      nvgIntersectScissor(
          context, contentPosition.x(), contentPosition.y(), contentSize.x(), contentSize.y());
    }

    @Override
    public void end(long context) {
      nvgRestore(context);
      resetScissor(context);
    }
  }

  private static final class NanoVgInputTextSink implements InputTextSink {
    private final NvgFontRegistry fontRegistry;

    private NanoVgInputTextSink(NvgFontRegistry fontRegistry) {
      this.fontRegistry = fontRegistry;
    }

    @Override
    public void drawText(
        long context,
        String text,
        Font font,
        float fontSize,
        Color color,
        float x,
        float baseline) {
      String fontFace = fontRegistry.fontFace(font, context);
      if (fontFace == null) {
        return;
      }
      nvgFontFace(context, fontFace);
      nvgFontSize(context, fontSize);
      nvgTextAlign(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
      try (var nvgColor = create(color)) {
        nvgFillColor(context, nvgColor);
        ByteBuffer textBuffer = memUTF8(text, false);
        try {
          nvgText(context, x, baseline, textBuffer);
        } finally {
          memFree(textBuffer);
        }
      }
    }
  }

  private static final class NanoVgInputSelectionSink implements InputSelectionSink {
    @Override
    public void drawSelection(long context, float x, float y, float width, float height) {
      drawRect(context, new Vector2f(x, y), new Vector2f(width, height), SELECTION_COLOR);
    }
  }

  private static final class NanoVgInputCaretSink implements InputCaretSink {
    @Override
    public void drawCaret(long context, float x, float y, float height) {
      drawRect(context, new Vector2f(x, y), new Vector2f(CARET_WIDTH, height), CARET_COLOR);
    }
  }
}
