package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgTextOutcomeDiagnostics.TextPath.TEXTAREA;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.MultilineTextControlMetrics;
import org.joml.Vector2f;

class NvgTextareaRenderer {

  private static final Color DEFAULT_TEXT_COLOR = Color.BLACK;
  private static final Color SELECTION_COLOR = new Color(59, 130, 246, 0.28f);
  private static final Color CARET_COLOR = new Color(33, 33, 33, 0.95f);
  private static final float CARET_WIDTH = 1.5f;

  private final TextareaStateSink stateSink;
  private final TextareaTextSink textSink;
  private final TextareaSelectionSink selectionSink;
  private final TextareaCaretSink caretSink;
  private TextMeasurer textMeasurer;

  NvgTextareaRenderer() {
    this(new NvgFontRegistry(), DiagnosticSession.disabled());
  }

  NvgTextareaRenderer(NvgFontRegistry fontRegistry) {
    this(fontRegistry, DiagnosticSession.disabled());
  }

  NvgTextareaRenderer(NvgFontRegistry fontRegistry, DiagnosticSession diagnostics) {
    this(new NanoVgTextCommandSink(fontRegistry, diagnostics), diagnostics);
  }

  NvgTextareaRenderer(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
    this(
        new CommandStateSink(commands),
        new CommandTextSink(commands, diagnostics),
        new CommandSelectionSink(commands),
        new CommandCaretSink(commands));
  }

  NvgTextareaRenderer(
      TextareaStateSink stateSink,
      TextareaTextSink textSink,
      TextareaSelectionSink selectionSink,
      TextareaCaretSink caretSink) {
    this.stateSink = stateSink;
    this.textSink = textSink;
    this.selectionSink = selectionSink;
    this.caretSink = caretSink;
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

    stateSink.begin(nanovgContext, textarea, contentPosition, contentSize);
    if (textarea.hasSelection()) {
      drawSelection(textarea, nanovgContext, metrics, contentPosition);
    }
    drawLines(textarea, nanovgContext, metrics, textStyle, contentPosition);
    if (textarea.focused()) {
      drawCaret(textarea, nanovgContext, metrics, contentPosition);
    }
    stateSink.end(nanovgContext);
  }

  private void drawLines(
      TextareaElement textarea,
      long nanovgContext,
      MultilineTextControlMetrics metrics,
      MultilineTextControlMetrics.TextStyle textStyle,
      Vector2f contentPosition) {
    Color color = color(textarea);
    textSink.begin(nanovgContext, textStyle, color);
    for (MultilineTextControlMetrics.Line line : metrics.lines(textarea)) {
      float baseline = contentPosition.y() - textarea.textScrollTop() + line.y() + line.baseline();
      float x = contentPosition.x() - textarea.textScrollLeft();
      textSink.drawLine(nanovgContext, line, textStyle, color, x, baseline);
    }
    textSink.end(nanovgContext);
  }

  private void drawCaret(
      TextareaElement textarea,
      long nanovgContext,
      MultilineTextControlMetrics metrics,
      Vector2f contentPosition) {
    MultilineTextControlMetrics.Caret caret = metrics.caret(textarea, textarea.caretIndex());
    caretSink.drawCaret(
        nanovgContext,
        contentPosition.x() - textarea.textScrollLeft() + caret.x(),
        contentPosition.y() - textarea.textScrollTop() + caret.y(),
        caret.height());
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
      selectionSink.drawSelection(
          nanovgContext,
          contentPosition.x() - textarea.textScrollLeft() + startCaret.x(),
          contentPosition.y() - textarea.textScrollTop() + line.y(),
          endCaret.x() - startCaret.x(),
          line.height());
    }
  }

  private Color color(TextareaElement textarea) {
    Color color = textarea.presentedStyle().color();
    return withPresentedOpacity(color == null ? DEFAULT_TEXT_COLOR : color, textarea);
  }

  interface TextareaStateSink {
    void begin(long context, TextareaElement textarea, Vector2f contentPosition, Vector2f contentSize);

    void end(long context);
  }

  interface TextareaTextSink {
    void begin(long context, MultilineTextControlMetrics.TextStyle style, Color color);

    void drawLine(
        long context,
        MultilineTextControlMetrics.Line line,
        MultilineTextControlMetrics.TextStyle style,
        Color color,
        float x,
        float baseline);

    void end(long context);
  }

  interface TextareaSelectionSink {
    void drawSelection(long context, float x, float y, float width, float height);
  }

  interface TextareaCaretSink {
    void drawCaret(long context, float x, float y, float height);
  }

  private static final class CommandStateSink implements TextareaStateSink {
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
        long context, TextareaElement textarea, Vector2f contentPosition, Vector2f contentSize) {
      clipStack.create(context, textarea);
      commands.beginScope(context, NvgTextCommand.TextPath.TEXTAREA);
      commands.intersectScissor(
          context, contentPosition.x(), contentPosition.y(), contentSize.x(), contentSize.y());
    }

    @Override
    public void end(long context) {
      commands.endScope(context, NvgTextCommand.TextPath.TEXTAREA);
      clipStack.reset(context);
    }
  }

  private static final class CommandTextSink implements TextareaTextSink {
    private final NvgTextCommandSink commands;
    private final DiagnosticSession diagnostics;
    private final NvgTextSubmission submission;

    private CommandTextSink(NvgTextCommandSink commands, DiagnosticSession diagnostics) {
      this.commands = commands;
      this.diagnostics = diagnostics;
      submission = new NvgTextSubmission(commands, diagnostics);
    }

    @Override
    public void begin(long context, MultilineTextControlMetrics.TextStyle style, Color color) {
      commands.fontSize(context, style.fontSize());
      commands.align(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
      commands.fillColor(context, color);
    }

    @Override
    public void drawLine(
        long context,
        MultilineTextControlMetrics.Line line,
        MultilineTextControlMetrics.TextStyle style,
        Color color,
        float x,
        float baseline) {
      diagnostics.increment(NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED);
      commands.outcome(
          NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED);
      diagnostics.increment(NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED);
      commands.outcome(
          NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED);
      if (line.runs().isEmpty()) {
        submission.submit(
            context,
            NvgTextCommand.TextPath.TEXTAREA,
            TEXTAREA,
            style.fonts().get(0),
            null,
            null,
            commands.displayText(style.fonts().get(0), line.text()),
            x,
            baseline);
        return;
      }
      float runX = x;
      for (ResolvedTextRun run : line.runs()) {
        if (submission.submit(
            context,
            NvgTextCommand.TextPath.TEXTAREA,
            TEXTAREA,
            run.font(),
            null,
            null,
            run.renderedText(),
            runX,
            baseline)) {
          commands.advance(NvgTextCommand.TextPath.TEXTAREA, runX, run.advance());
          runX += run.advance();
        }
      }
    }

    @Override
    public void end(long context) {}
  }

  private static final class CommandSelectionSink implements TextareaSelectionSink {
    private final NvgTextCommandSink commands;

    private CommandSelectionSink(NvgTextCommandSink commands) {
      this.commands = commands;
    }

    @Override
    public void drawSelection(long context, float x, float y, float width, float height) {
      commands.selection(context, x, y, width, height, SELECTION_COLOR);
    }
  }

  private static final class CommandCaretSink implements TextareaCaretSink {
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
