package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.node.NodeBuilder.TYPE_BUTTON;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NvgInputRendererTest {

  @Test
  void render_whenTextInputIsFocused_drawsValueAndCaretClippedToContentBox() {
    RecordingStateSink stateSink = new RecordingStateSink();
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(stateSink, selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = input("abcd");
    input.caretIndex(2);
    input.focused(true);

    renderer.render(input, 9);

    assertEquals(List.of("begin(9,20.0,30.0,60.0,20.0)", "end(9)"), stateSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of("text(9,abcd,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(9,40.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_passesResolvedRunsInCoreOrderAndRetainsAdvances() {
    RecordingTextSink textSink = new RecordingTextSink();
    List<ResolvedTextRun> runs =
        List.of(
            new ResolvedTextRun(
                0,
                1,
                Font.ROBOTO_REGULAR,
                List.of(new ResolvedGlyph(0, 1, 'a', 'a', Font.ROBOTO_REGULAR, false)),
                7f),
            new ResolvedTextRun(
                1,
                2,
                Font.NOTO_SANS_CJK_SC_REGULAR,
                List.of(new ResolvedGlyph(1, 2, '\u96ea', '\u96ea', Font.NOTO_SANS_CJK_SC_REGULAR, false)),
                16f));
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, new RecordingCaretSink());
    renderer.textMeasurer(new FixedTextMeasurer(runs));

    renderer.render(input("a\u96ea"), 11);

    assertEquals(
        List.of("Roboto:a@20.0+7.0", "Noto Sans CJK SC:\u96ea@27.0+16.0"),
        textSink.runSummary());
  }

  @Test
  void render_passesReplacementRunAsRenderableUfffdText() {
    RecordingTextSink textSink = new RecordingTextSink();
    List<ResolvedTextRun> runs =
        List.of(
            new ResolvedTextRun(
                0,
                2,
                Font.NOTO_SANS_CJK_SC_REGULAR,
                List.of(new ResolvedGlyph(0, 2, 0x10FFFF, 0xFFFD, Font.NOTO_SANS_CJK_SC_REGULAR, true)),
                9f));
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, new RecordingCaretSink());
    renderer.textMeasurer(new FixedTextMeasurer(runs));

    renderer.render(input(new String(Character.toChars(0x10FFFF))), 12);

    assertEquals(
        List.of("Noto Sans CJK SC:\uFFFD@20.0+9.0"), textSink.runSummary());
  }

  @Test
  void render_whenTextInputIsNotFocused_skipsCaret() {
    RecordingStateSink stateSink = new RecordingStateSink();
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(stateSink, selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    renderer.render(input("abc"), 5);

    assertEquals(List.of("text(5,abc,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenFocusedTextInputIsEmpty_drawsCaretAtContentStart() {
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = input("");
    input.focused(true);

    renderer.render(input, 3);

    assertEquals(List.of("text(3,,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(3,20.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_whenInputHasHorizontalScroll_offsetsTextAndCaret() {
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), new RecordingSelectionSink(), textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());
    InputElement input = input("abcdef");
    input.focused(true);
    input.caretIndex(4);
    input.textScrollLeft(15);

    renderer.render(input, 3);

    assertEquals(List.of("text(3,abcdef,5.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of("caret(3,45.0,32.0,16.0)"), caretSink.calls());
  }

  @Test
  void render_whenInputHasSelection_drawsSelectionBeforeText() {
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(), selectionSink, textSink, new RecordingCaretSink());
    renderer.textMeasurer(new FixedTextMeasurer());
    InputElement input = input("abcdef");
    input.select(1, 4);

    renderer.render(input, 8);

    assertEquals(List.of("selection(8,30.0,32.0,30.0,16.0)"), selectionSink.calls());
    assertEquals(List.of("text(8,abcdef,20.0,44.0,16.0)"), textSink.calls());
  }

  @Test
  void render_whenButtonInput_drawsValueClippedToContentBox() {
    RecordingStateSink stateSink = new RecordingStateSink();
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(stateSink, selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = buttonInput("Save");

    renderer.render(input, 7);

    assertEquals(List.of("begin(7,20.0,30.0,60.0,20.0)", "end(7)"), stateSink.calls());
    assertEquals(List.of("text(7,Save,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenFocusedButtonInputWithSelection_drawsNoSelectionOrCaret() {
    RecordingSelectionSink selectionSink = new RecordingSelectionSink();
    RecordingTextSink textSink = new RecordingTextSink();
    RecordingCaretSink caretSink = new RecordingCaretSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(new RecordingStateSink(), selectionSink, textSink, caretSink);
    renderer.textMeasurer(new FixedTextMeasurer());

    InputElement input = buttonInput("Save");
    input.focused(true);
    input.select(0, 4);
    input.textScrollLeft(15);

    renderer.render(input, 4);

    assertEquals(List.of("text(4,Save,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(List.of(), selectionSink.calls());
    assertEquals(List.of(), caretSink.calls());
  }

  @Test
  void render_whenTextMeasurerIsMissing_skipsInputText() {
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(),
            new RecordingSelectionSink(),
            textSink,
            new RecordingCaretSink());

    renderer.render(input("abc"), 1);

    assertEquals(List.of(), textSink.calls());
  }

  @Test
  void render_usesThePresentedTextColorAndOpacityWithoutChangingTextGeometry() {
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(),
            new RecordingSelectionSink(),
            textSink,
            new RecordingCaretSink());
    renderer.textMeasurer(new FixedTextMeasurer());
    InputElement input = input("abc");
    input.presentationState().setValue(COLOR, Color.BLUE);
    input.presentationState().setValue(OPACITY, 0.5f);

    renderer.render(input, 4);

    assertEquals(List.of("text(4,abc,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(Color.BLUE.withA(0.5f), textSink.color());
  }

  @Test
  void render_whenButtonInputTextMeasurerIsMissing_skipsValueText() {
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(),
            new RecordingSelectionSink(),
            textSink,
            new RecordingCaretSink());

    renderer.render(buttonInput("Save"), 1);

    assertEquals(List.of(), textSink.calls());
  }

  @Test
  void render_reportsOneCompleteInputLayoutAndEveryRepeatedPrefixMeasurement() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    FixedTextMeasurer textMeasurer = new FixedTextMeasurer(List.of(), diagnostics);
    RecordingTextSink textSink = new RecordingTextSink();
    NvgInputRenderer renderer =
        new NvgInputRenderer(
            new RecordingStateSink(),
            new RecordingSelectionSink(),
            textSink,
            new RecordingCaretSink());
    renderer.textMeasurer(textMeasurer);
    InputElement input = input("abcdef");
    input.focused(true);
    input.select(1, 4);

    renderer.render(input, 13);

    assertEquals(List.of("abcdef", "a", "abcd", "abcd"), textMeasurer.measuredTexts());
    assertEquals(List.of("text(13,abcdef,20.0,44.0,16.0)"), textSink.calls());
    assertEquals(
        1, diagnostics.snapshot().value(TextDiagnosticCounter.INPUT_COMPLETE_LAYOUTS));
    assertEquals(
        4,
        diagnostics
            .snapshot()
            .value(
                TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES));
    assertEquals(
        1, diagnostics.snapshot().value(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS));
  }

  @Test
  void commandSinkRecordsFocusedSelectionReplacementAndFaceFailureInProductionOrder() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgTextCommandRecorder recorder =
        new NvgTextCommandRecorder(font -> !font.equals(Font.ROBOTO_REGULAR));
    List<ResolvedTextRun> runs =
        List.of(
            new ResolvedTextRun(
                0,
                2,
                Font.NOTO_SANS_CJK_SC_REGULAR,
                List.of(
                    new ResolvedGlyph(
                        0, 2, 0x1F600, 0xFFFD, Font.NOTO_SANS_CJK_SC_REGULAR, true)),
                9),
            new ResolvedTextRun(
                2,
                3,
                Font.ROBOTO_REGULAR,
                List.of(new ResolvedGlyph(2, 3, 'a', 'a', Font.ROBOTO_REGULAR, false)),
                7));
    NvgInputRenderer renderer = new NvgInputRenderer(recorder, diagnostics);
    renderer.textMeasurer(new FixedTextMeasurer(runs));
    InputElement input = input("\uD83D\uDE00a");
    input.caretIndex(2);
    input.select(0, 2);
    input.focused(true);

    renderer.render(input, 4);

    List<NvgTextCommand> commands = recorder.commands();
    assertEquals(new NvgTextCommand.Scope(NvgTextCommand.TextPath.INPUT, true), commands.get(0));
    assertEquals(NvgTextCommand.Clip.class, commands.get(1).getClass());
    assertEquals(NvgTextCommand.Selection.class, commands.get(2).getClass());
    assertEquals(new NvgTextCommand.Alignment(65), commands.get(3));
    assertEquals(new NvgTextCommand.FillColor(Color.BLACK), commands.get(4));
    assertEquals(
        new NvgTextCommand.Text(NvgTextCommand.TextPath.INPUT, "\uFFFD", 3, 20, 44),
        commands.get(9));
    assertEquals(
        new NvgTextCommand.Advance(NvgTextCommand.TextPath.INPUT, 20, 9), commands.get(10));
    assertEquals(
        new NvgTextCommand.Outcome(
            NvgTextCommand.TextPath.INPUT,
            NvgDiagnosticCounter.INPUT_TEXT_ITEMS_FACE_SELECTION_FAILED.id()),
        commands.get(13));
    assertEquals(NvgTextCommand.Caret.class, commands.get(14).getClass());
    assertEquals(new NvgTextCommand.Scope(NvgTextCommand.TextPath.INPUT, false), commands.get(15));
    assertEquals(NvgTextCommand.Clip.class, commands.get(16).getClass());
    assertEquals(2, diagnostics.snapshot().value(NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CONSIDERED));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.INPUT_TEXT_ITEMS_SUBMITTED));
    assertEquals(
        1,
        diagnostics.snapshot().value(NvgDiagnosticCounter.INPUT_TEXT_ITEMS_FACE_SELECTION_FAILED));
  }

  private InputElement input(String value) {
    InputElement input = new InputElement();
    input.value(value);
    input.box().contentPosition(20, 30);
    input.box().contentSize(60, 20);
    input.resolvedStyle().fontFamilies(List.of(Font.DEFAULT.fontFamily()));
    input.resolvedStyle().fontSize(Length.pixel(16));
    input.resolvedStyle().lineHeight(1f);
    input.resolvedStyle().color(Color.BLACK);
    return input;
  }

  private InputElement buttonInput(String value) {
    InputElement input = input(value);
    input.type(TYPE_BUTTON);
    return input;
  }

  private static final class RecordingStateSink implements NvgInputRenderer.InputStateSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void begin(
        long context, InputElement input, Vector2f contentPosition, Vector2f contentSize) {
      calls.add(
          "begin(%d,%.1f,%.1f,%.1f,%.1f)"
              .formatted(
                  context,
                  contentPosition.x(),
                  contentPosition.y(),
                  contentSize.x(),
                  contentSize.y()));
    }

    @Override
    public void end(long context) {
      calls.add("end(%d)".formatted(context));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingTextSink implements NvgInputRenderer.InputTextSink {
    private final List<String> calls = new ArrayList<>();
    private List<ResolvedTextRun> runs = List.of();
    private Color color;

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
      this.color = color;
      this.runs = runs;
      calls.add("text(%d,%s,%.1f,%.1f,%.1f)".formatted(context, text, x, baseline, fontSize));
    }

    List<String> calls() {
      return calls;
    }

    Color color() {
      return color;
    }

    List<String> runSummary() {
      float x = 20;
      List<String> summary = new ArrayList<>();
      for (ResolvedTextRun run : runs) {
        summary.add(run.font().fontFamily() + ":" + run.renderedText() + "@" + x + "+" + run.advance());
        x += run.advance();
      }
      return summary;
    }
  }

  private static final class RecordingSelectionSink implements NvgInputRenderer.InputSelectionSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawSelection(long context, float x, float y, float width, float height) {
      calls.add(
          "selection(%d,%.1f,%.1f,%.1f,%.1f)".formatted(context, x, y, width, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  private static final class RecordingCaretSink implements NvgInputRenderer.InputCaretSink {
    private final List<String> calls = new ArrayList<>();

    @Override
    public void drawCaret(long context, float x, float y, float height) {
      calls.add("caret(%d,%.1f,%.1f,%.1f)".formatted(context, x, y, height));
    }

    List<String> calls() {
      return calls;
    }
  }

  static final class FixedTextMeasurer implements TextMeasurer {
    private static final float CHAR_WIDTH = 10;
    private final List<ResolvedTextRun> runs;
    private final DiagnosticSession diagnostics;
    private final List<String> measuredTexts = new ArrayList<>();

    FixedTextMeasurer() {
      this(List.of(), DiagnosticSession.disabled());
    }

    FixedTextMeasurer(List<ResolvedTextRun> runs) {
      this(runs, DiagnosticSession.disabled());
    }

    FixedTextMeasurer(
        List<ResolvedTextRun> runs, DiagnosticSession diagnostics) {
      this.runs = runs;
      this.diagnostics = diagnostics;
    }

    @Override
    public DiagnosticSession diagnostics() {
      return diagnostics;
    }

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextMetrics getTextMetrics(
        String text,
        float offsetX,
        Font font,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      throw new UnsupportedOperationException();
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return TextLineMetrics.builder()
          .characters(text)
          .width(text.length() * CHAR_WIDTH)
          .height(16)
          .baseline(12)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .runs(runs)
          .build();
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, List<Font> fonts, float fontSize, float lineHeight) {
      diagnostics.increment(
          TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES);
      measuredTexts.add(text);
      return getTextLineMetrics(text, fonts.get(0), fontSize, lineHeight);
    }

    private List<String> measuredTexts() {
      return List.copyOf(measuredTexts);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      throw new UnsupportedOperationException();
    }
  }
}
