package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath.INPUT;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.InputBehaviorRegistry;
import com.spinyowl.spinygui.core.system.input.RangeBehavior;
import java.util.List;
import org.joml.Vector2f;

class NvgInputRenderer {

  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  private static final Color SELECTION_COLOR = new Color(59, 130, 246, 0.28f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final Color CONTROL_BORDER_COLOR = new Color(90, 90, 90, 1f);
  private static final Color CONTROL_SURFACE_COLOR = new Color(255, 255, 255, 1f);
  private static final Color CONTROL_ACCENT_COLOR = new Color(59, 130, 246, 1f);
  private static final Color RANGE_TRACK_COLOR = new Color(160, 160, 160, 1f);
  private static final float CARET_WIDTH = 1.5f;
  private static final String PASSWORD_GLYPH = "\u2022";

  private final InputStateSink stateSink;
  private final InputSelectionSink selectionSink;
  private final InputTextSink textSink;
  private final InputCaretSink caretSink;
  private final InputControlSink controlSink;
  private final RangeBehavior rangeBehavior = new RangeBehavior();
  private TextMeasurer textMeasurer;

  NvgInputRenderer() {
    this(new NvgFontRegistry(), DiagnosticSession.disabled());
  }

  NvgInputRenderer(NvgFontRegistry fontRegistry) {
    this(fontRegistry, DiagnosticSession.disabled());
  }

  NvgInputRenderer(NvgFontRegistry fontRegistry, DiagnosticSession diagnostics) {
    this(new NanoVgTextCommandSink(fontRegistry, diagnostics), diagnostics);
  }

  NvgInputRenderer(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
    this(
        new CommandStateSink(commands),
        new CommandSelectionSink(commands),
        new CommandTextSink(commands, diagnostics),
        new CommandCaretSink(commands),
        new NanoVgControlSink());
  }

  NvgInputRenderer(
      InputStateSink stateSink,
      InputSelectionSink selectionSink,
      InputTextSink textSink,
      InputCaretSink caretSink) {
    this(stateSink, selectionSink, textSink, caretSink, InputControlSink.NO_OP);
  }

  NvgInputRenderer(
      InputStateSink stateSink,
      InputSelectionSink selectionSink,
      InputTextSink textSink,
      InputCaretSink caretSink,
      InputControlSink controlSink) {
    this.stateSink = stateSink;
    this.selectionSink = selectionSink;
    this.textSink = textSink;
    this.caretSink = caretSink;
    this.controlSink = controlSink;
  }

  void textMeasurer(TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  void render(InputElement input, long nanovgContext) {
    InputBehaviorRegistry.Kind kind = InputBehaviorRegistry.kind(input);
    if (kind == InputBehaviorRegistry.Kind.CHECKBOX) {
      controlSink.drawCheckbox(nanovgContext, input, contentPosition(input), input.box().contentSize());
      return;
    }
    if (kind == InputBehaviorRegistry.Kind.RADIO) {
      controlSink.drawRadio(nanovgContext, input, contentPosition(input), input.box().contentSize());
      return;
    }
    if (kind == InputBehaviorRegistry.Kind.RANGE) {
      controlSink.drawRange(
          nanovgContext,
          input,
          contentPosition(input),
          input.box().contentSize(),
          rangeBehavior.fraction(input));
      return;
    }
    if ((!input.textInput() && !input.buttonInput()) || textMeasurer == null) {
      return;
    }

    TextGeometry geometry = textGeometry(input);
    stateSink.begin(nanovgContext, input, geometry.contentPosition(), geometry.contentSize());
    if (input.buttonInput()) {
      drawValue(nanovgContext, geometry);
      stateSink.end(nanovgContext);
      return;
    }

    if (input.hasSelection()) {
      drawSelection(input, nanovgContext, geometry);
    }
    drawValue(nanovgContext, geometry);
    if (input.focused()) {
      drawCaret(input, nanovgContext, geometry);
    }
    stateSink.end(nanovgContext);
  }

  private void drawValue(long nanovgContext, TextGeometry geometry) {
    textSink.drawText(
        nanovgContext,
        geometry.displayValue(),
        geometry.font(),
        geometry.runs(),
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
            geometry.displayValue().substring(0, safeTextIndex),
            geometry.fonts(),
            geometry.fontSize(),
            geometry.requestedLineHeight());
    return line.width();
  }

  private TextGeometry textGeometry(InputElement input) {
    textMeasurer.diagnostics().increment(TextDiagnosticCounter.INPUT_COMPLETE_LAYOUTS);
    ResolvedStyle style = input.resolvedStyle();
    List<Font> fonts = findFonts(style);
    Font font = fonts.isEmpty() ? Font.DEFAULT : fonts.get(0);
    float fontSize = fontSize(input);
    float lineHeight = lineHeight(style);
    String displayValue = displayValue(input);
    TextLineMetrics line = textMeasurer.getTextLineMetrics(displayValue, fonts, fontSize, lineHeight);
    Vector2f contentPosition = contentPosition(input);
    Vector2f contentSize = input.box().contentSize();
    float lineTop = contentPosition.y() + Math.max(0, (contentSize.y() - line.height()) / 2f);
    return new TextGeometry(
        displayValue,
        font,
        fonts,
        line.runs(),
        fontSize,
        color(input),
        contentPosition,
        contentSize,
        contentPosition.x() - textScrollLeft(input),
        lineTop,
        lineTop + line.baseline(),
        line.height(),
        lineHeight);
  }

  private String displayValue(InputElement input) {
    return input.passwordInput() ? PASSWORD_GLYPH.repeat(input.value().length()) : input.value();
  }

  private float textScrollLeft(InputElement input) {
    return input.textInput() ? input.textScrollLeft() : 0;
  }

  private Vector2f contentPosition(InputElement input) {
    Vector2f position = input.layoutAbsolutePosition();
    position.add(
        input.box().border().left() + input.box().padding().left(),
        input.box().border().top() + input.box().padding().top());
    return position;
  }

  List<Font> findFonts(ResolvedStyle style) {
    if (style.fontFamilies() == null) {
      return List.of(Font.DEFAULT);
    }
    textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
    return Font.semanticOwner().resolver()
        .resolve(style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL);
  }

  private float fontSize(InputElement input) {
    Length<?> fontSize = input.resolvedStyle().fontSize();
    return fontSize == null ? 16f : StyleUtils.getFontSize(input);
  }

  private float lineHeight(ResolvedStyle style) {
    Float lineHeight = style.lineHeight();
    return lineHeight == null ? 1f : lineHeight;
  }

  private Color color(InputElement input) {
    Color color = input.presentedStyle().color();
    return withPresentedOpacity(color == null ? DEFAULT_TEXT_COLOR : color, input);
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
        List<ResolvedTextRun> runs,
        float fontSize,
        Color color,
        float x,
        float baseline);
  }

  interface InputCaretSink {
    void drawCaret(long context, float x, float y, float height);
  }

  interface InputControlSink {
    InputControlSink NO_OP =
        new InputControlSink() {
          @Override
          public void drawCheckbox(
              long context, InputElement input, Vector2f position, Vector2f size) {}

          @Override
          public void drawRadio(long context, InputElement input, Vector2f position, Vector2f size) {}

          @Override
          public void drawRange(
              long context,
              InputElement input,
              Vector2f position,
              Vector2f size,
              double fraction) {}
        };

    void drawCheckbox(long context, InputElement input, Vector2f position, Vector2f size);

    void drawRadio(long context, InputElement input, Vector2f position, Vector2f size);

    void drawRange(
        long context, InputElement input, Vector2f position, Vector2f size, double fraction);
  }

  private record TextGeometry(
      String displayValue,
      Font font,
      List<Font> fonts,
      List<ResolvedTextRun> runs,
      float fontSize,
      Color color,
      Vector2f contentPosition,
      Vector2f contentSize,
      float textX,
      float lineTop,
      float baseline,
      float lineHeight,
      float requestedLineHeight) {}

  private static final class CommandStateSink implements InputStateSink {
    private final NvgTextCommandSink commands;
    private final NvgClipStack clipStack;

    private CommandStateSink(NvgTextCommandSink commands) {
      this.commands = commands;
      clipStack =
          new NvgClipStack(
              new NvgClipStack.ClipSink() {
                @Override
                public void scissor(long context, float x, float y, float width, float height) {
                  commands.scissor(context, x, y, width, height);
                }

                @Override
                public void intersectScissor(
                    long context, float x, float y, float width, float height) {
                  commands.intersectScissor(context, x, y, width, height);
                }

                @Override
                public void reset(long context) {
                  commands.resetScissor(context);
                }
              });
    }

    @Override
    public void begin(
        long context, InputElement input, Vector2f contentPosition, Vector2f contentSize) {
      clipStack.create(context, input);
      commands.beginScope(context, NvgTextCommand.TextPath.INPUT);
      commands.intersectScissor(
          context, contentPosition.x(), contentPosition.y(), contentSize.x(), contentSize.y());
    }

    @Override
    public void end(long context) {
      commands.endScope(context, NvgTextCommand.TextPath.INPUT);
      clipStack.reset(context);
    }
  }

  private static final class CommandTextSink implements InputTextSink {
    private final NvgTextCommandSink commands;
    private final NvgTextSubmission submission;

    private CommandTextSink(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
      this.commands = commands;
      submission = new NvgTextSubmission(commands, diagnostics);
    }

    @Override
    public void drawText(
        long context,
        String text,
        Font font,
        List<ResolvedTextRun> runs,
        float fontSize,
        Color color,
        float x,
        float baseline) {
      commands.align(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
      commands.fillColor(context, color);
      if (runs.isEmpty()) {
        submission.submit(
            context,
            NvgTextCommand.TextPath.INPUT,
            INPUT,
            font,
            fontSize,
            null,
            commands.displayText(context, font, text),
            x,
            baseline);
        return;
      }
      float runX = x;
      for (ResolvedTextRun run : runs) {
        if (submission.submit(
            context,
            NvgTextCommand.TextPath.INPUT,
            INPUT,
            run.font(),
            fontSize,
            null,
            run.renderedText(),
            runX,
            baseline)) {
          commands.advance(NvgTextCommand.TextPath.INPUT, runX, run.advance());
          runX += run.advance();
        }
      }
    }
  }

  private static final class CommandSelectionSink implements InputSelectionSink {
    private final NvgTextCommandSink commands;

    private CommandSelectionSink(NvgTextCommandSink commands) {
      this.commands = commands;
    }

    @Override
    public void drawSelection(long context, float x, float y, float width, float height) {
      commands.selection(context, x, y, width, height, SELECTION_COLOR);
    }
  }

  private static final class CommandCaretSink implements InputCaretSink {
    private final NvgTextCommandSink commands;

    private CommandCaretSink(NvgTextCommandSink commands) {
      this.commands = commands;
    }

    @Override
    public void drawCaret(long context, float x, float y, float height) {
      commands.caret(context, x, y, CARET_WIDTH, height, CARET_COLOR);
    }
  }

  private static final class NanoVgControlSink implements InputControlSink {
    @Override
    public void drawCheckbox(long context, InputElement input, Vector2f position, Vector2f size) {
      float extent = controlExtent(size);
      if (extent <= 0f) {
        return;
      }
      Vector2f outerSize = new Vector2f(extent, extent);
      Vector2f outerPosition = verticallyCentered(position, size, extent);
      drawRect(
          context,
          outerPosition,
          outerSize,
          withPresentedOpacity(CONTROL_BORDER_COLOR, input),
          3f);
      float inset = Math.max(2f, extent * 0.14f);
      Vector2f innerPosition = new Vector2f(outerPosition).add(inset, inset);
      Vector2f innerSize = new Vector2f(extent - inset * 2f, extent - inset * 2f);
      Color inner = input.checked() ? CONTROL_ACCENT_COLOR : CONTROL_SURFACE_COLOR;
      drawRect(context, innerPosition, innerSize, withPresentedOpacity(inner, input), 2f);
    }

    @Override
    public void drawRadio(long context, InputElement input, Vector2f position, Vector2f size) {
      float extent = controlExtent(size);
      if (extent <= 0f) {
        return;
      }
      Vector2f outerSize = new Vector2f(extent, extent);
      Vector2f outerPosition = verticallyCentered(position, size, extent);
      drawRect(
          context,
          outerPosition,
          outerSize,
          withPresentedOpacity(CONTROL_BORDER_COLOR, input),
          extent / 2f);
      float inset = Math.max(2f, extent * 0.14f);
      Vector2f innerPosition = new Vector2f(outerPosition).add(inset, inset);
      float innerExtent = extent - inset * 2f;
      drawRect(
          context,
          innerPosition,
          new Vector2f(innerExtent, innerExtent),
          withPresentedOpacity(CONTROL_SURFACE_COLOR, input),
          innerExtent / 2f);
      if (input.checked()) {
        float dotInset = extent * 0.30f;
        float dotExtent = extent - dotInset * 2f;
        drawRect(
            context,
            new Vector2f(outerPosition).add(dotInset, dotInset),
            new Vector2f(dotExtent, dotExtent),
            withPresentedOpacity(CONTROL_ACCENT_COLOR, input),
            dotExtent / 2f);
      }
    }

    @Override
    public void drawRange(
        long context,
        InputElement input,
        Vector2f position,
        Vector2f size,
        double fraction) {
      if (size.x <= 0f || size.y <= 0f) {
        return;
      }
      float trackHeight = Math.min(4f, size.y);
      float trackY = position.y + (size.y - trackHeight) / 2f;
      drawRect(
          context,
          new Vector2f(position.x, trackY),
          new Vector2f(size.x, trackHeight),
          withPresentedOpacity(RANGE_TRACK_COLOR, input),
          trackHeight / 2f);

      float thumbExtent = Math.min(16f, Math.max(8f, size.y));
      float centerX = position.x + (float) Math.max(0.0, Math.min(1.0, fraction)) * size.x;
      Vector2f thumbPosition =
          new Vector2f(centerX - thumbExtent / 2f, position.y + (size.y - thumbExtent) / 2f);
      drawRect(
          context,
          thumbPosition,
          new Vector2f(thumbExtent, thumbExtent),
          withPresentedOpacity(CONTROL_ACCENT_COLOR, input),
          thumbExtent / 2f);
    }

    private static float controlExtent(Vector2f size) {
      return Math.min(18f, Math.min(size.x, size.y));
    }

    private static Vector2f verticallyCentered(Vector2f position, Vector2f size, float extent) {
      return new Vector2f(position.x, position.y + Math.max(0f, (size.y - extent) / 2f));
    }
  }
}
