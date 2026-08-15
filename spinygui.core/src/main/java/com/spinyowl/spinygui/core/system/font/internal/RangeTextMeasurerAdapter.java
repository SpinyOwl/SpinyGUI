package com.spinyowl.spinygui.core.system.font.internal;

import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Source-compatible range dispatch with a zero-copy capability path and allocating legacy fallback. */
public final class RangeTextMeasurerAdapter {
  private RangeTextMeasurerAdapter() {}

  public static TextMetrics measureRange(
      TextMeasurer measurer,
      String source,
      int start,
      int end,
      float offsetX,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    Objects.requireNonNull(measurer, "measurer");
    PreparedRange request =
        new PreparedRange(
            source, start, end, offsetX, fonts, fontSize, lineHeight, maxWidth, wordWrap);
    if (measurer instanceof RangeTextMeasurerCapability capability) {
      return capability
          .measureRange(
              request.source(),
              request.start(),
              request.end(),
              request.offsetX(),
              request.fonts(),
              request.fontSize(),
              request.lineHeight(),
              request.maxWidth(),
              request.wordWrap())
          .metrics();
    }

    measurer.diagnostics().increment(TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS);
    String rangeText = request.source().substring(request.start(), request.end());
    TextMetrics local =
        measurer.measureText(
            rangeText,
            request.offsetX(),
            request.fonts(),
            request.fontSize(),
            request.lineHeight(),
            request.maxWidth(),
            request.wordWrap());
    return translate(local, request.start());
  }

  private static TextMetrics translate(TextMetrics local, int origin) {
    List<TextLineMetrics> lines = new ArrayList<>(local.lines().size());
    for (TextLineMetrics line : local.lines()) {
      List<ResolvedTextRun> runs = new ArrayList<>(line.runs().size());
      for (ResolvedTextRun run : line.runs()) {
        List<ResolvedGlyph> glyphs =
            run.glyphs().stream()
                .map(
                    glyph ->
                        new ResolvedGlyph(
                            glyph.sourceStart() + origin,
                            glyph.sourceEnd() + origin,
                            glyph.sourceCodePoint(),
                            glyph.renderedCodePoint(),
                            glyph.font(),
                            glyph.replacement()))
                .toList();
        runs.add(
            new ResolvedTextRun(
                run.sourceStart() + origin,
                run.sourceEnd() + origin,
                run.font(),
                glyphs,
                run.advance()));
      }
      lines.add(
          new TextLineMetrics(
              line.characters(),
              line.startIndex() + origin,
              line.endIndex() + origin,
              line.charCount(),
              line.width(),
              line.height(),
              line.baseline(),
              line.fontMetrics(),
              runs));
    }
    return new TextMetrics(
        lines,
        local.width(),
        local.height(),
        local.lineHeight(),
        local.fontMetrics());
  }
}
