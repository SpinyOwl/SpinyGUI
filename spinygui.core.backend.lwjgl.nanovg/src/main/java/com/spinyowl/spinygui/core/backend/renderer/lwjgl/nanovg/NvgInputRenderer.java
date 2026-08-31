package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath.INPUT;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutSnapshot;
import java.util.List;
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
  private ControlTextLayoutService layoutService;

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
        new CommandCaretSink(commands));
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
    this.layoutService = textMeasurer == null ? null : new ControlTextLayoutService(textMeasurer);
  }

  void layoutService(ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  void render(InputElement input, long nanovgContext) {
    if ((!input.textInput() && !input.buttonInput()) || layoutService == null) {
      return;
    }

    TextGeometry geometry = textGeometry(input);
    stateSink.begin(nanovgContext, input, geometry.contentPosition(), geometry.contentSize());
    try {
      if (input.buttonInput()) {
        drawValue(input, nanovgContext, geometry);
        return;
      }

      if (input.hasSelection()) {
        drawSelection(input, nanovgContext, geometry);
      }
      drawValue(input, nanovgContext, geometry);
      if (input.focused()) {
        drawCaret(input, nanovgContext, geometry);
      }
    } finally {
      stateSink.end(nanovgContext);
    }
  }

  private void drawValue(InputElement input, long nanovgContext, TextGeometry geometry) {
    textSink.drawText(
        nanovgContext,
        input.value(),
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
    return geometry.snapshot().caret(textIndex, layoutService.diagnostics()).x();
  }

  private TextGeometry textGeometry(InputElement input) {
    ControlTextLayoutSnapshot snapshot = layoutService.query(input);
    ControlTextLayoutSnapshot.Key key = snapshot.key();
    List<Font> fonts = key.resolvedFonts();
    Font font = fonts.isEmpty() ? Font.DEFAULT : fonts.get(0);
    float fontSize = key.fontSize();
    ControlTextLayoutSnapshot.Line line = snapshot.lines().get(0);
    Vector2f contentPosition = contentPosition(input);
    Vector2f contentSize = input.box().contentSize();
    float lineTop = contentPosition.y() + Math.max(0, (contentSize.y() - line.height()) / 2f);
    return new TextGeometry(
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
        key.lineHeight(),
        snapshot);
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

  /** Compatibility inspection of the production semantic resolver used by the snapshot service. */
  List<Font> findFonts(ResolvedStyle style) {
    if (style.fontFamilies() == null) {
      return List.of(Font.DEFAULT);
    }
    return Font.semanticOwner().resolver()
        .resolve(style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL);
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

  private record TextGeometry(
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
      float requestedLineHeight,
      ControlTextLayoutSnapshot snapshot) {}

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
      boolean scopeStarted = false;
      try {
        clipStack.create(context, input);
        commands.beginScope(context, NvgTextCommand.TextPath.INPUT);
        scopeStarted = true;
        commands.intersectScissor(
            context, contentPosition.x(), contentPosition.y(), contentSize.x(), contentSize.y());
      } catch (RuntimeException | Error failure) {
        cleanupAfterBeginFailure(context, scopeStarted, failure);
        throw failure;
      }
    }

    @Override
    public void end(long context) {
      Throwable failure = null;
      try {
        commands.endScope(context, NvgTextCommand.TextPath.INPUT);
      } catch (RuntimeException | Error error) {
        failure = error;
      }
      try {
        clipStack.reset(context);
      } catch (RuntimeException | Error cleanupFailure) {
        if (failure != null) {
          failure.addSuppressed(cleanupFailure);
        } else {
          throw cleanupFailure;
        }
      }
      if (failure instanceof RuntimeException error) throw error;
      if (failure instanceof Error error) throw error;
    }

    private void cleanupAfterBeginFailure(long context, boolean scopeStarted, Throwable failure) {
      if (scopeStarted) {
        try {
          commands.endScope(context, NvgTextCommand.TextPath.INPUT);
        } catch (RuntimeException | Error cleanupFailure) {
          failure.addSuppressed(cleanupFailure);
        }
      }
      try {
        clipStack.reset(context);
      } catch (RuntimeException | Error cleanupFailure) {
        failure.addSuppressed(cleanupFailure);
      }
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
            NvgRenderedText.literal(commands.displayText(context, font, text)),
            x,
            baseline);
        return;
      }
      float runX = x;
      for (ResolvedTextRun run : runs) {
        submission.submit(
            context,
            NvgTextCommand.TextPath.INPUT,
            INPUT,
            run.font(),
            fontSize,
            null,
            NvgRenderedText.run(run),
            runX,
            baseline);
        commands.advance(NvgTextCommand.TextPath.INPUT, runX, run.advance());
        runX += run.advance();
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

}
