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
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutService;
import com.spinyowl.spinygui.core.system.input.ControlTextLayoutSnapshot;
import java.util.List;
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
  private ControlTextLayoutService layoutService;

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
    this.layoutService = textMeasurer == null ? null : new ControlTextLayoutService(textMeasurer);
  }

  void layoutService(ControlTextLayoutService layoutService) {
    this.layoutService = layoutService;
  }

  void render(TextareaElement textarea, long nanovgContext) {
    if (layoutService == null) {
      return;
    }
    ControlTextLayoutSnapshot snapshot = layoutService.query(textarea);
    Vector2f contentPosition = contentPosition(textarea);
    Vector2f contentSize = textarea.box().contentSize();
    TextStyle textStyle =
        new TextStyle(
            snapshot.key().resolvedFonts(), snapshot.key().fontSize(), snapshot.key().lineHeight());

    stateSink.begin(nanovgContext, textarea, contentPosition, contentSize);
    try {
      if (textarea.hasSelection()) {
        drawSelection(textarea, nanovgContext, snapshot, contentPosition);
      }
      drawLines(textarea, nanovgContext, snapshot, textStyle, contentPosition);
      if (textarea.focused()) {
        drawCaret(textarea, nanovgContext, snapshot, contentPosition);
      }
    } finally {
      stateSink.end(nanovgContext);
    }
  }

  private Vector2f contentPosition(TextareaElement textarea) {
    Vector2f position = textarea.layoutAbsolutePosition();
    position.add(
        textarea.box().border().left() + textarea.box().padding().left(),
        textarea.box().border().top() + textarea.box().padding().top());
    return position;
  }

  private void drawLines(
      TextareaElement textarea,
      long nanovgContext,
      ControlTextLayoutSnapshot snapshot,
      TextStyle textStyle,
      Vector2f contentPosition) {
    Color color = color(textarea);
    textSink.begin(nanovgContext, textStyle, color);
    for (ControlTextLayoutSnapshot.Line line : snapshot.lines()) {
      float baseline = contentPosition.y() - textarea.textScrollTop() + line.y() + line.baseline();
      float x = contentPosition.x() - textarea.textScrollLeft();
      textSink.drawLine(nanovgContext, line, textStyle, color, x, baseline);
    }
    textSink.end(nanovgContext);
  }

  private void drawCaret(
      TextareaElement textarea,
      long nanovgContext,
      ControlTextLayoutSnapshot snapshot,
      Vector2f contentPosition) {
    ControlTextLayoutSnapshot.Caret caret =
        snapshot.caret(textarea.caretIndex(), layoutService.diagnostics());
    caretSink.drawCaret(
        nanovgContext,
        contentPosition.x() - textarea.textScrollLeft() + caret.x(),
        contentPosition.y() - textarea.textScrollTop() + caret.y(),
        caret.height());
  }

  private void drawSelection(
      TextareaElement textarea,
      long nanovgContext,
      ControlTextLayoutSnapshot snapshot,
      Vector2f contentPosition) {
    int start = textarea.selectionStart();
    int end = textarea.selectionEnd();
    for (ControlTextLayoutSnapshot.Line line : snapshot.lines()) {
      int lineStart = Math.max(start, line.startIndex());
      int lineEnd = Math.min(end, line.endIndex());
      if (lineEnd <= lineStart) {
        continue;
      }
      ControlTextLayoutSnapshot.Caret startCaret =
          snapshot.caret(lineStart, layoutService.diagnostics());
      ControlTextLayoutSnapshot.Caret endCaret =
          snapshot.caret(lineEnd, layoutService.diagnostics());
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
    void begin(long context, TextStyle style, Color color);

    void drawLine(
        long context,
        ControlTextLayoutSnapshot.Line line,
        TextStyle style,
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
      boolean scopeStarted = false;
      try {
        clipStack.create(context, textarea);
        commands.beginScope(context, NvgTextCommand.TextPath.TEXTAREA);
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
        commands.endScope(context, NvgTextCommand.TextPath.TEXTAREA);
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
          commands.endScope(context, NvgTextCommand.TextPath.TEXTAREA);
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
    public void begin(long context, TextStyle style, Color color) {
      commands.fontSize(context, style.fontSize());
      commands.align(context, NVG_ALIGN_LEFT | NVG_ALIGN_BASELINE);
      commands.fillColor(context, color);
    }

    @Override
    public void drawLine(
        long context,
        ControlTextLayoutSnapshot.Line line,
        TextStyle style,
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
            NvgRenderedText.literal(commands.displayText(context, style.fonts().get(0), line.text())),
            x,
            baseline);
        return;
      }
      float runX = x;
      for (ResolvedTextRun run : line.runs()) {
        submission.submit(
            context,
            NvgTextCommand.TextPath.TEXTAREA,
            TEXTAREA,
            run.font(),
            null,
            null,
            NvgRenderedText.run(run),
            runX,
            baseline);
        commands.advance(NvgTextCommand.TextPath.TEXTAREA, runX, run.advance());
        runX += run.advance();
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

  record TextStyle(List<Font> fonts, float fontSize, float lineHeight) {
    TextStyle {
      fonts = List.copyOf(fonts);
    }
  }

}
