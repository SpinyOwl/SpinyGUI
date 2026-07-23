package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector2fc;

/** Shared line, caret, and hit-test metrics for editable multiline text controls. */
public class MultilineTextControlMetrics {

  @NonNull private final TextMeasurer textMeasurer;

  public MultilineTextControlMetrics(@NonNull TextMeasurer textMeasurer) {
    this.textMeasurer = textMeasurer;
  }

  public List<Line> lines(TextareaElement textarea) {
    TextStyle textStyle = textStyle(textarea);
    float maxWidth = Math.max(0, textarea.box().contentSize().x());
    String value = textarea.value();
    String[] paragraphs = value.split("\n", -1);
    List<Line> lines = new ArrayList<>();
    int paragraphStart = 0;
    float y = 0;
    for (String paragraph : paragraphs) {
      List<TextLineMetrics> measuredLines = measureParagraph(paragraph, textStyle, maxWidth);
      for (TextLineMetrics measuredLine : measuredLines) {
        String lineText = measuredLine.characters().toString();
        int start = paragraphStart + measuredLine.startIndex();
        int end = paragraphStart + measuredLine.endIndex();
        lines.add(
            new Line(
                lineText,
                start,
                end,
                measuredLine.width(),
                measuredLine.height(),
                measuredLine.baseline(),
                measuredLine.runs(),
                y));
        y += measuredLine.height();
      }
      paragraphStart += paragraph.length() + 1;
    }
    return lines;
  }

  public Caret caret(TextareaElement textarea, int index) {
    List<Line> lines = lines(textarea);
    if (lines.isEmpty()) {
      return new Caret(0, 0, 0, lineHeight(textarea));
    }
    int safeIndex = Math.max(0, Math.min(index, textarea.value().length()));
    Line line = lineForIndex(lines, safeIndex);
    TextStyle textStyle = textStyle(textarea);
    int lineOffset = Math.max(0, Math.min(safeIndex - line.startIndex(), line.text().length()));
    float x =
        textMeasurer
            .getTextLineMetrics(
                line.text().substring(0, lineOffset),
                 textStyle.fonts(),
                textStyle.fontSize(),
                textStyle.lineHeight())
            .width();
    return new Caret(safeIndex, x, line.y(), line.height());
  }

  public int indexAt(TextareaElement textarea, Vector2fc cursorPosition) {
    List<Line> lines = lines(textarea);
    if (lines.isEmpty()) {
      return 0;
    }
    Vector2f contentPosition = contentPosition(textarea);
    float localX = cursorPosition.x() - contentPosition.x() + textarea.textScrollLeft();
    float localY = cursorPosition.y() - contentPosition.y() + textarea.textScrollTop();
    Line line = lineForY(lines, localY);
    TextStyle textStyle = textStyle(textarea);
    TextCaretMetrics caretMetrics =
        textMeasurer.getTextCaretMetrics(
             line.text(), textStyle.fonts(), textStyle.fontSize(), Math.max(0, localX));
    return Math.max(
        0, Math.min(line.startIndex() + caretMetrics.charIndex(), textarea.value().length()));
  }

  public int lineStart(TextareaElement textarea, int index) {
    return lineForIndex(lines(textarea), Math.max(0, Math.min(index, textarea.value().length())))
        .startIndex();
  }

  public int lineEnd(TextareaElement textarea, int index) {
    return lineForIndex(lines(textarea), Math.max(0, Math.min(index, textarea.value().length())))
        .endIndex();
  }

  public int verticalCaretIndex(TextareaElement textarea, int index, int direction) {
    List<Line> lines = lines(textarea);
    if (lines.isEmpty()) {
      return 0;
    }
    int currentLineIndex =
        lineIndexForIndex(lines, Math.max(0, Math.min(index, textarea.value().length())));
    int nextLineIndex = Math.max(0, Math.min(currentLineIndex + direction, lines.size() - 1));
    if (nextLineIndex == currentLineIndex) {
      return index;
    }
    Caret currentCaret = caret(textarea, index);
    Line nextLine = lines.get(nextLineIndex);
    TextStyle textStyle = textStyle(textarea);
    TextCaretMetrics nextCaret =
        textMeasurer.getTextCaretMetrics(
            nextLine.text(), textStyle.fonts(), textStyle.fontSize(), currentCaret.x());
    return Math.max(
        0, Math.min(nextLine.startIndex() + nextCaret.charIndex(), textarea.value().length()));
  }

  public Vector2f contentPosition(TextareaElement textarea) {
    Vector2f position = textarea.layoutAbsolutePosition();
    position.add(
        textarea.box().border().left() + textarea.box().padding().left(),
        textarea.box().border().top() + textarea.box().padding().top());
    return position;
  }

  public TextStyle textStyle(TextareaElement textarea) {
    ResolvedStyle style = textarea.resolvedStyle();
    return new TextStyle(findFonts(style), fontSize(textarea), lineHeight(style));
  }

  private List<TextLineMetrics> measureParagraph(
      String paragraph, TextStyle textStyle, float maxWidth) {
    if (paragraph.isEmpty()) {
      return List.of(
          textMeasurer.getTextLineMetrics(
               "", textStyle.fonts(), textStyle.fontSize(), textStyle.lineHeight()));
    }
    TextMetrics metrics =
        textMeasurer.measureText(
            paragraph,
            0,
            textStyle.fonts(),
            textStyle.fontSize(),
            textStyle.lineHeight(),
            maxWidth,
            false);
    if (!metrics.lines().isEmpty()) {
      return metrics.lines();
    }
    return List.of(
        textMeasurer.getTextLineMetrics(
            paragraph, textStyle.fonts(), textStyle.fontSize(), textStyle.lineHeight()));
  }

  private Line lineForIndex(List<Line> lines, int index) {
    return lines.get(lineIndexForIndex(lines, index));
  }

  private int lineIndexForIndex(List<Line> lines, int index) {
    for (int i = 0; i < lines.size(); i++) {
      Line line = lines.get(i);
      if (index >= line.startIndex() && index <= line.endIndex()) {
        return i;
      }
    }
    return index <= 0 ? 0 : lines.size() - 1;
  }

  private Line lineForY(List<Line> lines, float y) {
    for (Line line : lines) {
      if (y < line.y() + line.height()) {
        return line;
      }
    }
    return y <= 0 ? lines.get(0) : lines.get(lines.size() - 1);
  }

  private List<Font> findFonts(ResolvedStyle style) {
    if (style.fontFamilies() == null) {
      return List.of(Font.DEFAULT);
    }
    return FontChainResolver.DEFAULT
        .resolve(
            style.fontFamilies(), style.fontStyle(), style.fontWeight(), FontStretch.NORMAL);
  }

  private float fontSize(TextareaElement textarea) {
    Length<?> fontSize = textarea.resolvedStyle().fontSize();
    return fontSize == null ? 16f : StyleUtils.getFontSize(textarea);
  }

  private float lineHeight(TextareaElement textarea) {
    return lineHeight(textarea.resolvedStyle());
  }

  private float lineHeight(ResolvedStyle style) {
    Float lineHeight = style.lineHeight();
    return lineHeight == null ? 1f : lineHeight;
  }

  public record Line(
      String text,
      int startIndex,
      int endIndex,
      float width,
      float height,
      float baseline,
      List<ResolvedTextRun> runs,
      float y) {}

  public record Caret(int index, float x, float y, float height) {}

  public record TextStyle(List<Font> fonts, float fontSize, float lineHeight) {}
}
