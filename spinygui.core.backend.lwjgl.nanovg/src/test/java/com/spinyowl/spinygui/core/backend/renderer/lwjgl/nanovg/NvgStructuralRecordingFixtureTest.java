package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.layout.InlineFragment;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgStructuralRecordingFixtureTest {

  @Test
  void normalTextFixtureFreezesFragmentsRunsReplacementTransformClipAndOffscreenSubmission() {
    DiagnosticSession diagnostics = enabledDiagnostics();
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder(diagnostics);
    NvgTextRenderer renderer = new NvgTextRenderer(recorder, diagnostics);
    Element clip = clippedElement(10, 20, 100, 30);
    Text text = new Text("a\u96EA?");
    clip.addChild(text);
    text.inlineFragments(
        List.of(
            fragment(
                "a\u96EA",
                2,
                12,
                List.of(
                    run(0, 'a', Font.ROBOTO_REGULAR, 5),
                    run(1, '\u96EA', Font.NOTO_SANS_CJK_SC_REGULAR, 11))),
            fragment("?", 180, 28, List.of())));

    try (var transform =
            NvgTransformState.apply(0, AffineTransform.translation(3, 4), recorder);
        var content = NvgSubtreeContentState.apply(0, clip, recorder)) {
      renderer.render(text, 0);
    }

    assertEquals(
        List.of(
            new NvgTextCommand.TransformScope(true),
            new NvgTextCommand.Transform(1, 0, 0, 1, 3, 4),
            new NvgTextCommand.TransformScope(true),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, 10, 20, 100, 30),
            new NvgTextCommand.Translate(-0f, -0f),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.NORMAL, true),
            new NvgTextCommand.Alignment(65),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.NORMAL, Font.ROBOTO_REGULAR, true),
            new NvgTextCommand.FontSize(16),
            new NvgTextCommand.FillColor(Color.BLACK),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.NORMAL, "a", 1, 12, 32),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.NORMAL, 12, 5),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(
                NvgTextCommand.TextPath.NORMAL, Font.NOTO_SANS_CJK_SC_REGULAR, true),
            new NvgTextCommand.FontSize(16),
            new NvgTextCommand.FillColor(Color.BLACK),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.NORMAL, "\u96EA", 3, 17, 32),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.NORMAL, 17, 11),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.NORMAL, Font.DEFAULT, true),
            new NvgTextCommand.FontSize(16),
            new NvgTextCommand.FillColor(Color.BLACK),
            outcome(NvgTextCommand.TextPath.NORMAL, NvgDiagnosticCounter.NORMAL_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.NORMAL, "?", 1, 190, 48),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.NORMAL, false),
            new NvgTextCommand.TransformScope(false),
            new NvgTextCommand.TransformScope(false)),
        recorder.commands());
    NvgTextRecordingAssertions.assertReconciled(recorder.commands(), diagnostics.snapshot());
  }

  @Test
  void normalFailureFixtureCorrelatesUnavailableFaceAndTerminalOutcome() {
    DiagnosticSession diagnostics = enabledDiagnostics();
    NvgTextCommandRecorder recorder =
        new NvgTextCommandRecorder(font -> !font.equals(Font.ROBOTO_REGULAR), diagnostics);
    NvgTextRenderer renderer = new NvgTextRenderer(recorder, diagnostics);
    Element parent = new Element("div");
    Text text = new Text("a");
    parent.addChild(text);
    text.inlineFragments(
        List.of(fragment("a", 0, 12, List.of(run(0, 'a', Font.ROBOTO_REGULAR, 5)))));

    renderer.render(text, 0);

    NvgTextRecordingAssertions.assertReconciled(recorder.commands(), diagnostics.snapshot());
  }

  @Test
  void inputFailureFixtureCorrelatesUnavailableFaceAndTerminalOutcome() {
    DiagnosticSession diagnostics = enabledDiagnostics();
    NvgTextCommandRecorder recorder =
        new NvgTextCommandRecorder(font -> !font.equals(Font.ROBOTO_REGULAR), diagnostics);
    NvgInputRenderer renderer = new NvgInputRenderer(recorder, diagnostics);
    renderer.textMeasurer(
        new FixtureTextMeasurer(List.of(run(0, 'a', Font.ROBOTO_REGULAR, 5))));
    InputElement input = new InputElement();
    input.value("a");
    style(input);
    input.box().contentPosition(20, 30);
    input.box().contentSize(60, 20);

    renderer.render(input, 0);

    NvgTextRecordingAssertions.assertReconciled(recorder.commands(), diagnostics.snapshot());
  }

  @Test
  void inputFixtureFreezesValueRunsSelectionCaretScrollAndUnchangedSubmission() {
    DiagnosticSession diagnostics = enabledDiagnostics();
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder(diagnostics);
    List<ResolvedTextRun> runs =
        List.of(
            run(0, 'a', Font.ROBOTO_REGULAR, 5),
            run(1, '\u96EA', Font.NOTO_SANS_CJK_SC_REGULAR, 11));
    NvgInputRenderer renderer = new NvgInputRenderer(recorder, diagnostics);
    renderer.textMeasurer(new FixtureTextMeasurer(runs));
    InputElement input = new InputElement();
    input.value("a\u96EA");
    style(input);
    input.box().contentPosition(20, 30);
    input.box().contentSize(60, 20);
    input.textScrollLeft(7);
    input.caretIndex(1);
    input.select(0, 1);
    input.focused(true);

    int[] predecessorExecutions = {0};
    predecessorExecutions[0]++;
    renderer.render(input, 0);
    List<NvgTextCommand> predecessor = recorder.snapshot();
    assertEquals(1, predecessorExecutions[0]);
    assertEquals(2, predecessor.stream().filter(NvgTextCommand.Text.class::isInstance).count());
    diagnostics.reset();
    recorder.reset();
    renderer.render(input, 0);
    List<NvgTextCommand> current = recorder.snapshot();

    assertEquals(
        List.of(
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.INPUT, true),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, 20, 30, 60, 20),
            new NvgTextCommand.Selection(13, 32, 5, 16, new Color(59, 130, 246, 0.28f)),
            new NvgTextCommand.Alignment(65),
            new NvgTextCommand.FillColor(Color.BLACK),
            outcome(NvgTextCommand.TextPath.INPUT, NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.INPUT, Font.ROBOTO_REGULAR, true),
            new NvgTextCommand.FontSize(16),
            outcome(NvgTextCommand.TextPath.INPUT, NvgDiagnosticCounter.INPUT_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.INPUT, "a", 1, 13, 44),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.INPUT, 13, 5),
            outcome(NvgTextCommand.TextPath.INPUT, NvgDiagnosticCounter.INPUT_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(
                NvgTextCommand.TextPath.INPUT, Font.NOTO_SANS_CJK_SC_REGULAR, true),
            new NvgTextCommand.FontSize(16),
            outcome(NvgTextCommand.TextPath.INPUT, NvgDiagnosticCounter.INPUT_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.INPUT, "\u96EA", 3, 18, 44),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.INPUT, 18, 11),
            new NvgTextCommand.Caret(18, 32, 1.5f, 16, new Color(33, 33, 33, 0.95f)),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.INPUT, false),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.RESET, 0, 0, 0, 0)),
        current);
    NvgTextRecordingAssertions.assertReconciled(current, diagnostics.snapshot());
  }

  @Test
  void textareaFixtureFreezesLinesEmptyParagraphOffscreenSubmissionDecorationAndNestedClips() {
    DiagnosticSession diagnostics = enabledDiagnostics();
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder(diagnostics);
    NvgTextareaRenderer renderer = new NvgTextareaRenderer(recorder, diagnostics);
    Element outer = clippedElement(5, 7, 120, 44);
    List<ResolvedTextRun> textareaRuns =
        List.of(
            run(0, 'a', Font.ROBOTO_REGULAR, 5),
            replacementRun(1, 0x1F600, Font.NOTO_SANS_CJK_SC_REGULAR, 9));
    renderer.textMeasurer(new FixtureTextMeasurer(textareaRuns));
    TextareaElement textarea = new TextareaElement("a\uD83D\uDE00\n\nlast");
    outer.addChild(textarea);
    textarea.offsetParent(outer);
    style(textarea);
    textarea.box().contentPosition(20, 30);
    textarea.box().contentSize(80, 16);
    textarea.textScrollLeft(3);
    textarea.textScrollTop(5);
    textarea.caretIndex(3);
    textarea.select(0, 3);
    textarea.focused(true);

    int[] predecessorExecutions = {0};
    predecessorExecutions[0]++;
    renderer.render(textarea, 0);
    List<NvgTextCommand> predecessor = recorder.snapshot();
    assertEquals(1, predecessorExecutions[0]);
    assertEquals(4, predecessor.stream().filter(NvgTextCommand.Text.class::isInstance).count());
    diagnostics.reset();
    recorder.reset();
    renderer.render(textarea, 0);
    List<NvgTextCommand> current = recorder.snapshot();

    assertEquals(
        List.of(
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.SCISSOR, 5, 7, 120, 44),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.TEXTAREA, true),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.INTERSECT, 25, 37, 80, 16),
            new NvgTextCommand.Selection(22, 32, 15, 16, new Color(59, 130, 246, 0.28f)),
            new NvgTextCommand.FontSize(16),
            new NvgTextCommand.Alignment(65),
            new NvgTextCommand.FillColor(Color.BLACK),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.TEXTAREA, Font.ROBOTO_REGULAR, true),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "a", 1, 22, 44),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.TEXTAREA, 22, 5),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(
                NvgTextCommand.TextPath.TEXTAREA, Font.NOTO_SANS_CJK_SC_REGULAR, true),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "\uFFFD", 3, 27, 44),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.TEXTAREA, 27, 9),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.TEXTAREA, Font.ROBOTO_BOLD, true),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "", 0, 22, 60),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_LINES_SUBMITTED),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.TEXTAREA, Font.ROBOTO_BOLD, true),
            outcome(NvgTextCommand.TextPath.TEXTAREA, NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "last", 4, 22, 76),
            new NvgTextCommand.Caret(37, 32, 1.5f, 16, new Color(33, 33, 33, 0.95f)),
            new NvgTextCommand.Scope(NvgTextCommand.TextPath.TEXTAREA, false),
            new NvgTextCommand.Clip(NvgTextCommand.ClipOperation.RESET, 0, 0, 0, 0)),
        current);
    NvgTextRecordingAssertions.assertReconciled(current, diagnostics.snapshot());
  }

  private static DiagnosticSession enabledDiagnostics() {
    return DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
  }

  private static InlineFragment fragment(
      String text, float x, float baseline, List<ResolvedTextRun> runs) {
    return InlineFragment.builder()
        .text(text)
        .x(x)
        .baseline(baseline)
        .font(Font.DEFAULT)
        .fontSize(16)
        .color(Color.BLACK)
        .runs(runs)
        .build();
  }

  private static ResolvedTextRun run(int index, int codePoint, Font font, float advance) {
    return new ResolvedTextRun(
        index,
        index + Character.charCount(codePoint),
        font,
        List.of(
            new ResolvedGlyph(
                index,
                index + Character.charCount(codePoint),
                codePoint,
                codePoint,
                font,
                false)),
        advance);
  }

  private static ResolvedTextRun replacementRun(
      int index, int sourceCodePoint, Font font, float advance) {
    return new ResolvedTextRun(
        index,
        index + Character.charCount(sourceCodePoint),
        font,
        List.of(
            new ResolvedGlyph(
                index,
                index + Character.charCount(sourceCodePoint),
                sourceCodePoint,
                0xFFFD,
                font,
                true)),
        advance);
  }

  private static NvgTextCommand.Outcome outcome(
      NvgTextCommand.TextPath path, NvgDiagnosticCounter counter) {
    return new NvgTextCommand.Outcome(path, counter.id());
  }

  private static Element clippedElement(float x, float y, float width, float height) {
    Element element = new Element("div");
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);
    return element;
  }

  private static void style(Element element) {
    element.resolvedStyle().fontFamilies(
        List.of(Font.ROBOTO_REGULAR.fontFamily(), Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily()));
    element.resolvedStyle().fontSize(Length.pixel(16));
    element.resolvedStyle().lineHeight(1f);
    element.resolvedStyle().color(Color.BLACK);
  }

  private static final class FixtureTextMeasurer implements TextMeasurer {
    private final List<ResolvedTextRun> runs;

    private FixtureTextMeasurer(List<ResolvedTextRun> runs) {
      this.runs = runs;
    }

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      return metrics(text);
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
      return metrics(text);
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
      return metrics(text);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return line(text);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, List<Font> fonts, float fontSize, float lineHeight) {
      return line(text);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      return new TextCaretMetrics(Math.min(text.length(), Math.round(offsetX / 5)), offsetX);
    }

    private TextMetrics metrics(String text) {
      return TextMetrics.builder().line(line(text)).build();
    }

    private TextLineMetrics line(String text) {
      List<ResolvedTextRun> applicableRuns = runsFor(text);
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(width(text))
          .height(16)
          .baseline(12)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .runs(applicableRuns)
          .build();
    }

    private List<ResolvedTextRun> runsFor(String text) {
      if (text.isEmpty() || runs.isEmpty()) return List.of();
      if (text.equals("a\u96EA") || text.equals("a\uD83D\uDE00")) return runs;
      if (!text.equals("a")) return List.of();
      List<ResolvedTextRun> matching = new ArrayList<>();
      for (ResolvedTextRun run : runs) {
        if (run.sourceEnd() <= text.length()) matching.add(run);
      }
      return matching;
    }

    private float width(String text) {
      if (text.equals("a\u96EA")) return 16;
      return text.length() * 5f;
    }
  }
}
