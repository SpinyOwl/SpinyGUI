package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NvgTextareaRendererTest {

  @BeforeEach
  void installFontOwner() {
    NvgFontTestOwner.install();
  }

  @Test
  void commandSinkRecordsSelectionTextCaretAndOneProductionFontSizeInOrder() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgTextCommandRecorder recorder = new NvgTextCommandRecorder();
    NvgTextareaRenderer renderer = new NvgTextareaRenderer(recorder, diagnostics);
    renderer.textMeasurer(NvgFontTestOwner.install());
    TextareaElement textarea = new TextareaElement("a\u96EA");
    textarea.box().contentPosition(20, 30);
    textarea.box().contentSize(100, 40);
    textarea.resolvedStyle().fontFamilies(
        List.of(Font.ROBOTO_REGULAR.fontFamily(), Font.NOTO_SANS_CJK_SC_REGULAR.fontFamily()));
    textarea.resolvedStyle().fontSize(Length.pixel(16));
    textarea.resolvedStyle().lineHeight(1f);
    textarea.resolvedStyle().color(Color.BLACK);
    textarea.caretIndex(1);
    textarea.select(0, 1);
    textarea.focused(true);

    renderer.render(textarea, 3);

    List<NvgTextCommand> commands = recorder.commands();
    assertEquals(new NvgTextCommand.Scope(NvgTextCommand.TextPath.TEXTAREA, true), commands.get(0));
    assertEquals(NvgTextCommand.Clip.class, commands.get(1).getClass());
    assertEquals(NvgTextCommand.Selection.class, commands.get(2).getClass());
    assertEquals(new NvgTextCommand.FontSize(16), commands.get(3));
    assertEquals(new NvgTextCommand.Alignment(65), commands.get(4));
    assertEquals(new NvgTextCommand.FillColor(Color.BLACK), commands.get(5));
    assertEquals(
        new NvgTextCommand.Outcome(
            NvgTextCommand.TextPath.TEXTAREA,
            NvgDiagnosticCounter.TEXTAREA_LINES_CONSIDERED.id()),
        commands.get(6));
    assertEquals(NvgTextCommand.Text.class, commands.get(11).getClass());
    assertEquals(NvgTextCommand.Advance.class, commands.get(12).getClass());
    assertEquals(NvgTextCommand.Caret.class, commands.get(commands.size() - 3).getClass());
    assertEquals(
        new NvgTextCommand.Scope(NvgTextCommand.TextPath.TEXTAREA, false),
        commands.get(commands.size() - 2));
    assertEquals(NvgTextCommand.Clip.class, commands.getLast().getClass());
    assertEquals(
        1, commands.stream().filter(NvgTextCommand.FontSize.class::isInstance).count());
    assertEquals(
        diagnostics.snapshot().value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED),
        diagnostics.snapshot().value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED)
            + diagnostics.snapshot().value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CULLED)
            + diagnostics
                .snapshot()
                .value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED));
  }

  @Test
  void unavailableMiddleFaceDoesNotAdvanceAndLaterRunUsesTheLastSubmittedX() {
    Font unavailable = Font.NOTO_SANS_CJK_SC_REGULAR;
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgTextCommandRecorder recorder =
        new NvgTextCommandRecorder(font -> !font.equals(unavailable), diagnostics);
    List<ResolvedTextRun> runs =
        List.of(
            run(0, 'a', Font.ROBOTO_REGULAR, 5),
            run(1, '\u96EA', unavailable, 11),
            run(2, 'b', Font.ROBOTO_REGULAR, 7));
    NvgTextareaRenderer renderer = new NvgTextareaRenderer(recorder, diagnostics);
    renderer.textMeasurer(new FixedTextareaMeasurer(runs));
    TextareaElement textarea = textarea("a\u96EAb");

    renderer.render(textarea, 0);

    List<NvgTextCommand> textAndAdvances =
        recorder.commands().stream()
            .filter(
                command ->
                    command instanceof NvgTextCommand.Text
                        || command instanceof NvgTextCommand.Advance)
            .toList();
    assertEquals(
        List.of(
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "a", 1, 20, 42),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.TEXTAREA, 20, 5),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "b", 1, 25, 42),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.TEXTAREA, 25, 7)),
        textAndAdvances);
    int failedFaceIndex =
        recorder.commands().indexOf(new NvgTextCommand.Face(NvgTextCommand.TextPath.TEXTAREA, unavailable, false));
    assertEquals(
        List.of(
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.TEXTAREA,
                NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED.id()),
            new NvgTextCommand.Face(NvgTextCommand.TextPath.TEXTAREA, unavailable, false),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.TEXTAREA,
                NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED.id()),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.TEXTAREA,
                NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED.id()),
            new NvgTextCommand.Face(
                NvgTextCommand.TextPath.TEXTAREA, Font.ROBOTO_REGULAR, true),
            new NvgTextCommand.Outcome(
                NvgTextCommand.TextPath.TEXTAREA,
                NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED.id()),
            new NvgTextCommand.Text(NvgTextCommand.TextPath.TEXTAREA, "b", 1, 25, 42),
            new NvgTextCommand.Advance(NvgTextCommand.TextPath.TEXTAREA, 25, 7)),
        recorder.commands().subList(failedFaceIndex - 1, failedFaceIndex + 7));
    assertEquals(3, diagnostics.snapshot().value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_CONSIDERED));
    assertEquals(2, diagnostics.snapshot().value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_SUBMITTED));
    assertEquals(
        1,
        diagnostics
            .snapshot()
            .value(NvgDiagnosticCounter.TEXTAREA_TEXT_ITEMS_FACE_SELECTION_FAILED));
    NvgTextRecordingAssertions.assertReconciled(recorder.commands(), diagnostics.snapshot());
  }

  private static ResolvedTextRun run(
      int index, int codePoint, Font font, float advance) {
    return new ResolvedTextRun(
        index,
        index + 1,
        font,
        List.of(new ResolvedGlyph(index, index + 1, codePoint, codePoint, font, false)),
        advance);
  }

  private static TextareaElement textarea(String value) {
    TextareaElement textarea = new TextareaElement(value);
    textarea.box().contentPosition(20, 30);
    textarea.box().contentSize(100, 40);
    textarea.resolvedStyle().fontFamilies(List.of(Font.ROBOTO_REGULAR.fontFamily()));
    textarea.resolvedStyle().fontSize(Length.pixel(16));
    textarea.resolvedStyle().lineHeight(1f);
    textarea.resolvedStyle().color(Color.BLACK);
    return textarea;
  }

  private static final class FixedTextareaMeasurer implements TextMeasurer {
    private final List<ResolvedTextRun> runs;

    private FixedTextareaMeasurer(List<ResolvedTextRun> runs) {
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
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      return new TextCaretMetrics(0, 0);
    }

    private TextMetrics metrics(String text) {
      return TextMetrics.builder().line(line(text)).build();
    }

    private TextLineMetrics line(String text) {
      return TextLineMetrics.builder()
          .characters(text)
          .startIndex(0)
          .endIndex(text.length())
          .charCount(text.length())
          .width(23)
          .height(16)
          .baseline(12)
          .fontMetrics(new FontMetrics(12, 4, 0, 16, 12))
          .runs(runs)
          .build();
    }
  }
}
