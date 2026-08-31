package com.spinyowl.spinygui.core.system.input;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.node.InputElement;
import com.spinyowl.spinygui.core.node.TextareaElement;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability;
import com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerAdapter;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Shared lazy builder/validator for the one current snapshot retained by editable controls. */
public final class ControlTextLayoutService {
  private final TextMeasurer textMeasurer;
  private final Object measurementContext;

  public ControlTextLayoutService(TextMeasurer textMeasurer) {
    this.textMeasurer = Objects.requireNonNull(textMeasurer, "textMeasurer");
    this.measurementContext = new IdentityKey(textMeasurer);
  }

  public TextMeasurer textMeasurer() {
    return textMeasurer;
  }

  public ControlTextLayoutSnapshot query(InputElement input) {
    Objects.requireNonNull(input, "input");
    ControlTextLayoutSnapshot.Key key =
        key(input.value(), input.resolvedStyle(), Float.MAX_VALUE, effectiveFontSize(input));
    ControlTextLayoutSnapshot current = input.currentTextLayoutSnapshot();
    if (current != null && current.key().equals(key)) {
      return current;
    }
    ControlTextLayoutSnapshot replacement = build(key, TextDiagnosticCounter.INPUT_COMPLETE_LAYOUTS);
    input.replaceTextLayoutSnapshot(replacement);
    return replacement;
  }

  public ControlTextLayoutSnapshot query(TextareaElement textarea) {
    Objects.requireNonNull(textarea, "textarea");
    float width = Math.max(0, textarea.box().contentSize().x());
    ControlTextLayoutSnapshot.Key key =
        key(textarea.value(), textarea.resolvedStyle(), width, effectiveFontSize(textarea));
    ControlTextLayoutSnapshot current = textarea.currentTextLayoutSnapshot();
    if (current != null && current.key().equals(key)) {
      return current;
    }
    ControlTextLayoutSnapshot replacement =
        build(key, TextDiagnosticCounter.TEXTAREA_COMPLETE_LAYOUTS);
    textarea.replaceTextLayoutSnapshot(replacement);
    return replacement;
  }

  public DiagnosticSession diagnostics() {
    return textMeasurer.diagnostics();
  }

  private ControlTextLayoutSnapshot.Key key(
      String value, ResolvedStyle style, float maxWidth, float fontSize) {
    List<String> families =
        style.fontFamilies() == null ? List.of() : List.copyOf(style.fontFamilies());
    FontStyle fontStyle = style.fontStyle() == null ? FontStyle.NORMAL : style.fontStyle();
    FontWeight fontWeight = style.fontWeight() == null ? FontWeight.NORMAL : style.fontWeight();
    List<Font> fonts;
    long generation = 0;
    boolean generationAvailable = Font.hasSemanticOwner();
    if (families.isEmpty()) {
      fonts = List.of(Font.DEFAULT);
    } else {
      textMeasurer.diagnostics().increment(TextDiagnosticCounter.FONT_CHAIN_RESOLUTIONS);
      fonts = Font.semanticOwner().resolver().resolve(families, fontStyle, fontWeight, FontStretch.NORMAL);
    }
    if (generationAvailable) {
      generation = Font.semanticOwner().generation();
    }
    float lineHeight = style.lineHeight() == null ? 1f : style.lineHeight();
    return new ControlTextLayoutSnapshot.Key(
        value,
        families,
        fontStyle,
        fontWeight,
        FontStretch.NORMAL,
        fonts,
        fontSize,
        lineHeight,
        generation,
        generationAvailable,
        measurementContext,
        maxWidth,
        false);
  }

  private float effectiveFontSize(com.spinyowl.spinygui.core.node.Element node) {
    return node.resolvedStyle().fontSize() == null ? 16f : StyleUtils.getFontSize(node);
  }

  private ControlTextLayoutSnapshot build(
      ControlTextLayoutSnapshot.Key key, TextDiagnosticCounter completeLayoutCounter) {
    textMeasurer.diagnostics().increment(completeLayoutCounter);
    ResolvedMeasurement resolved = resolvedMeasurement(key);
    TextMetrics metrics = resolved.metrics();
    List<ControlTextLayoutSnapshot.Line> lines = new ArrayList<>(metrics.lines().size());
    float y = 0;
    for (int index = 0; index < metrics.lines().size(); index++) {
      TextLineMetrics line = metrics.lines().get(index);
      lines.add(
          new ControlTextLayoutSnapshot.Line(
              line.characters().toString(),
              line.startIndex(),
              line.endIndex(),
              line.width(),
              line.height(),
              line.baseline(),
              line.runs(),
              y,
              resolved.lineCaretStops().get(index)));
      y += line.height();
    }
    return new ControlTextLayoutSnapshot(key, lines, metrics.width(), metrics.height());
  }

  private ResolvedMeasurement resolvedMeasurement(ControlTextLayoutSnapshot.Key key) {
    if (textMeasurer instanceof RangeTextMeasurerCapability capability) {
      return capability.measureRange(
          key.value(),
          0,
          key.value().length(),
          0,
          key.resolvedFonts(),
          key.fontSize(),
          key.lineHeight(),
          key.maxWidth(),
          key.wordWrap());
    }
    TextMetrics metrics = key.maxWidth() == Float.MAX_VALUE
        ? legacyRange(key, 0, key.value().length())
        : legacyParagraphs(key);
    List<FinalLineCaretStops> stops = new ArrayList<>(metrics.lines().size());
    for (TextLineMetrics line : metrics.lines()) {
      stops.add(legacyCaretStops(key, line));
    }
    return new ResolvedMeasurement(metrics, stops);
  }

  private TextMetrics legacyRange(ControlTextLayoutSnapshot.Key key, int start, int end) {
    try {
      return RangeTextMeasurerAdapter.measureRange(
          textMeasurer, key.value(), start, end, 0, key.resolvedFonts(), key.fontSize(),
          key.lineHeight(), key.maxWidth(), key.wordWrap());
    } catch (UnsupportedOperationException unsupported) {
      TextLineMetrics measured = textMeasurer.getTextLineMetrics(
          key.value().substring(start, end), key.resolvedFonts(), key.fontSize(), key.lineHeight());
      TextLineMetrics line = new TextLineMetrics(
          measured.characters(), start, end, end - start, measured.width(),
          measured.height(), measured.baseline(), measured.fontMetrics(),
          translateRuns(measured.runs(), start));
      return new TextMetrics(List.of(line), line.width(), line.height(), line.height(),
          line.fontMetrics());
    }
  }

  private TextMetrics legacyParagraphs(ControlTextLayoutSnapshot.Key key) {
    List<TextLineMetrics> lines = new ArrayList<>();
    float width = 0;
    float height = 0;
    int start = 0;
    while (true) {
      int separator = nextSeparator(key.value(), start);
      int end = separator < 0 ? key.value().length() : separator;
      TextMetrics paragraph = legacyRange(key, start, end);
      lines.addAll(paragraph.lines());
      width = Math.max(width, paragraph.width());
      height += paragraph.height();
      if (separator < 0) break;
      start = separator + 1;
      if (key.value().charAt(separator) == '\r'
          && start < key.value().length()
          && key.value().charAt(start) == '\n') {
        start++;
      }
    }
    TextLineMetrics first = lines.get(0);
    return new TextMetrics(lines, width, height, first.height(), first.fontMetrics());
  }

  private int nextSeparator(String value, int start) {
    for (int index = start; index < value.length(); index++) {
      char character = value.charAt(index);
      if (character == '\r' || character == '\n') return index;
    }
    return -1;
  }

  private List<ResolvedTextRun> translateRuns(List<ResolvedTextRun> runs, int origin) {
    if (origin == 0) return runs;
    return runs.stream()
        .map(run -> new ResolvedTextRun(
            run.sourceStart() + origin,
            run.sourceEnd() + origin,
            run.font(),
            run.glyphs().stream()
                .map(glyph -> new ResolvedGlyph(
                    glyph.sourceStart() + origin,
                    glyph.sourceEnd() + origin,
                    glyph.sourceCodePoint(),
                    glyph.renderedCodePoint(),
                    glyph.font(),
                    glyph.replacement()))
                .toList(),
            run.advance()))
        .toList();
  }

  private FinalLineCaretStops legacyCaretStops(
      ControlTextLayoutSnapshot.Key key, TextLineMetrics line) {
    List<Integer> boundaries = new ArrayList<>();
    List<Float> advances = new ArrayList<>();
    boundaries.add(line.startIndex());
    advances.add(0f);
    int boundary = line.startIndex();
    while (boundary < line.endIndex()) {
      boundary = key.value().offsetByCodePoints(boundary, 1);
      String prefix = key.value().substring(line.startIndex(), boundary);
      float advance =
          textMeasurer
              .getTextLineMetrics(prefix, key.resolvedFonts(), key.fontSize(), key.lineHeight())
              .width();
      boundaries.add(boundary);
      advances.add(advance);
    }
    int[] sourceBoundaries = boundaries.stream().mapToInt(Integer::intValue).toArray();
    float[] caretAdvances = new float[advances.size()];
    for (int index = 0; index < advances.size(); index++) {
      caretAdvances[index] = advances.get(index);
    }
    return new FinalLineCaretStops(sourceBoundaries, caretAdvances);
  }

  private static final class IdentityKey {
    private final Object value;

    private IdentityKey(Object value) {
      this.value = value;
    }

    @Override
    public boolean equals(Object other) {
      return other instanceof IdentityKey key && value == key.value;
    }

    @Override
    public int hashCode() {
      return System.identityHashCode(value);
    }
  }
}
