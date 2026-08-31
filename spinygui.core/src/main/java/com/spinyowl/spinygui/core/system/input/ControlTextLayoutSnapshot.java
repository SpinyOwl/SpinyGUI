package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import java.util.List;
import java.util.Objects;

/** One immutable, text-local layout value retained by an editable control. */
public final class ControlTextLayoutSnapshot {
  private final Key key;
  private final List<Paragraph> paragraphs;
  private final List<Line> lines;
  private final float width;
  private final float height;

  public ControlTextLayoutSnapshot(Key key, List<Line> lines, float width, float height) {
    this.key = Objects.requireNonNull(key, "key");
    this.lines = List.copyOf(lines);
    if (this.lines.isEmpty()) {
      throw new IllegalArgumentException("A control snapshot requires at least one visual line");
    }
    this.paragraphs = paragraphs(key.value(), this.lines);
    this.width = width;
    this.height = height;
  }

  public Key key() {
    return key;
  }

  public List<Line> lines() {
    return lines;
  }

  public List<Paragraph> paragraphs() {
    return paragraphs;
  }

  public float width() {
    return width;
  }

  public float height() {
    return height;
  }

  /** Deterministic retained-weight estimate used by aggregate cache observations. */
  public long retainedWeight() {
    return Math.max(1L, (long) key.value().length() * 2L + (long) lines.size() * 64L);
  }

  public Line lineForIndex(int sourceIndex) {
    int safeIndex = Math.max(0, Math.min(sourceIndex, key.value().length()));
    Line previous = lines.get(0);
    for (Line line : lines) {
      if (safeIndex < line.startIndex()) {
        return previous;
      }
      if (safeIndex >= line.startIndex() && safeIndex <= line.endIndex()) {
        return line;
      }
      previous = line;
    }
    return lines.get(lines.size() - 1);
  }

  public Line lineForY(float y) {
    for (Line line : lines) {
      if (y < line.y() + line.height()) {
        return line;
      }
    }
    return y <= 0 ? lines.get(0) : lines.get(lines.size() - 1);
  }

  public Caret caret(int sourceIndex, DiagnosticSession diagnostics) {
    Line line = lineForIndex(sourceIndex);
    TextCaretMetrics caret = line.caretStops().caretAtSourceIndex(sourceIndex, diagnostics);
    return new Caret(caret.charIndex(), caret.x(), line.y(), line.height());
  }

  public int indexAt(float x, float y, DiagnosticSession diagnostics) {
    Line line = lineForY(y);
    return line.caretStops().caretAt(Math.max(0, x), diagnostics).charIndex();
  }

  public Caret caretAt(float x, float y, DiagnosticSession diagnostics) {
    Line line = lineForY(y);
    TextCaretMetrics caret = line.caretStops().caretAt(Math.max(0, x), diagnostics);
    return new Caret(caret.charIndex(), caret.x(), line.y(), line.height());
  }

  private static List<Paragraph> paragraphs(String value, List<Line> lines) {
    java.util.ArrayList<Paragraph> paragraphs = new java.util.ArrayList<>();
    int start = 0;
    while (true) {
      int separator = -1;
      int separatorLength = 0;
      for (int index = start; index < value.length(); index++) {
        char character = value.charAt(index);
        if (character == '\r' || character == '\n') {
          separator = index;
          separatorLength =
              character == '\r' && index + 1 < value.length() && value.charAt(index + 1) == '\n'
                  ? 2
                  : 1;
          break;
        }
      }
      int end = separator < 0 ? value.length() : separator;
      int firstLine = -1;
      int lineCount = 0;
      for (int lineIndex = 0; lineIndex < lines.size(); lineIndex++) {
        Line line = lines.get(lineIndex);
        if (line.startIndex() >= start && line.endIndex() <= end) {
          if (firstLine < 0) {
            firstLine = lineIndex;
          }
          lineCount++;
        }
      }
      paragraphs.add(new Paragraph(start, end, Math.max(0, firstLine), lineCount));
      if (separator < 0) break;
      start = separator + separatorLength;
    }
    return List.copyOf(paragraphs);
  }

  /** Complete immutable effective text-layout key. */
  public record Key(
      String value,
      List<String> fontFamilies,
      FontStyle fontStyle,
      FontWeight fontWeight,
      FontStretch fontStretch,
      List<Font> resolvedFonts,
      float fontSize,
      float lineHeight,
      long semanticGeneration,
      boolean semanticGenerationAvailable,
      Object measurementContext,
      float maxWidth,
      boolean wordWrap) {
    public Key {
      value = Objects.requireNonNull(value, "value");
      fontFamilies = List.copyOf(fontFamilies);
      resolvedFonts = List.copyOf(resolvedFonts);
      fontStyle = Objects.requireNonNull(fontStyle, "fontStyle");
      fontWeight = Objects.requireNonNull(fontWeight, "fontWeight");
      fontStretch = Objects.requireNonNull(fontStretch, "fontStretch");
      measurementContext = Objects.requireNonNull(measurementContext, "measurementContext");
    }
  }

  /** One immutable visual line with absolute source mapping and text-local geometry. */
  public static final class Line {
    private final String text;
    private final int startIndex;
    private final int endIndex;
    private final float width;
    private final float height;
    private final float baseline;
    private final List<ResolvedTextRun> runs;
    private final float y;
    private final FinalLineCaretStops caretStops;

    public Line(String text, int startIndex, int endIndex, float width, float height,
        float baseline, List<ResolvedTextRun> runs, float y, FinalLineCaretStops caretStops) {
      this.text = Objects.requireNonNull(text, "text");
      this.startIndex = startIndex;
      this.endIndex = endIndex;
      this.width = width;
      this.height = height;
      this.baseline = baseline;
      this.runs = List.copyOf(runs);
      this.y = y;
      this.caretStops = Objects.requireNonNull(caretStops, "caretStops");
    }

    public String text() { return text; }
    public int startIndex() { return startIndex; }
    public int endIndex() { return endIndex; }
    public float width() { return width; }
    public float height() { return height; }
    public float baseline() { return baseline; }
    public List<ResolvedTextRun> runs() { return runs; }
    public float y() { return y; }
    FinalLineCaretStops caretStops() { return caretStops; }
  }

  public record Caret(int index, float x, float y, float height) {}

  /** Immutable source paragraph mapped to its contiguous visual-line range. */
  public record Paragraph(int startIndex, int endIndex, int firstLineIndex, int lineCount) {}
}
