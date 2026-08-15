package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FontServiceImplLinearScalingTest {
  private static final float FONT_SIZE = 16;
  private static final float LINE_HEIGHT = 1.2f;
  private static final int MISSING_CODE_POINT = 0x10FFFF;
  private static final Font NOTO_EMOJI =
      new Font("Noto Emoji", "fonts/NotoEmoji-Regular.ttf");
  private static final List<Font> FALLBACK_FONTS =
      List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR, NOTO_EMOJI);

  @Test
  void increasingWholeStringAdversariesHaveExactLinearCounterFormulas() {
    float wordWidth =
        disabledService().measureText("AV ", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();
    Map<String, List<WholeEvidence>> series = new LinkedHashMap<>();

    for (int units : List.of(8, 16, 32)) {
      for (WholeScenario scenario : wholeScenarios(units, wordWidth)) {
        WholeEvidence evidence = runWholeScenario(scenario);
        assertWholeFormulas(evidence);
        series.computeIfAbsent(scenario.name(), ignored -> new ArrayList<>()).add(evidence);
      }
    }

    for (Map.Entry<String, List<WholeEvidence>> entry : series.entrySet()) {
      assertEquals(3, entry.getValue().size(), entry.getKey());
      long previousCodePoints = 0;
      for (WholeEvidence evidence : entry.getValue()) {
        assertTrue(evidence.sourceCodePoints() > previousCodePoints, entry.getKey());
        assertEquals(
            evidence.sourceCodePoints(),
            counter(evidence.snapshot(), TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
        assertEquals(
            evidence.sourceCodePoints(),
            counter(evidence.snapshot(), TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS));
        assertEquals(
            evidence.glyphCount() + evidence.materializedGlyphCount(),
            counter(evidence.snapshot(), TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
        previousCodePoints = evidence.sourceCodePoints();
      }
    }
  }

  @Test
  void increasingSharedSourceRangeBatchesResolveAndMaterializeEachRequestedCodePointOnce() {
    for (int rangeCount : List.of(16, 32, 64)) {
      DiagnosticSession diagnostics = diagnostics();
      FontServiceImpl service = service(diagnostics);
      String source = "xx" + "A雪".repeat(rangeCount) + "zz";

      for (int rangeIndex = 0; rangeIndex < rangeCount; rangeIndex++) {
        int start = 2 + rangeIndex * 2;
        ResolvedMeasurement resolved =
            service.measureRange(
                source,
                start,
                start + 2,
                0,
                List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
                FONT_SIZE,
                LINE_HEIGHT,
                Float.POSITIVE_INFINITY,
                false);
        TextLineMetrics line = resolved.metrics().lines().getFirst();
        assertAll(
            () -> assertEquals(start, line.startIndex()),
            () -> assertEquals(start + 2, line.endIndex()),
            () -> assertEquals("A雪", line.toString()),
            () -> assertEquals(2, line.runs().size()),
            () -> assertEquals(1, resolved.lineCaretStops().size()));
      }

      DiagnosticSnapshot snapshot = diagnostics.snapshot();
      long glyphs = rangeCount * 2L;
      assertAll(
          () ->
              assertEquals(
                  rangeCount,
                  counter(snapshot, TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
          () ->
              assertEquals(
                  rangeCount, counter(snapshot, TextDiagnosticCounter.RANGE_PREPARATIONS)),
          () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS)),
          () ->
              assertEquals(
                  glyphs, counter(snapshot, TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
          () ->
              assertEquals(
                  glyphs, counter(snapshot, TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS)),
          () ->
              assertEquals(
                  glyphs, counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
          () ->
              assertEquals(
                  rangeCount * 3L,
                  counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES)),
          () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.NATIVE_KERNING_CALLS)),
          () ->
              assertEquals(
                  glyphs, counter(snapshot, TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS)),
          () ->
              assertEquals(
                  glyphs,
                  counter(
                      snapshot,
                      TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED)),
          () ->
              assertEquals(
                  glyphs,
                  counter(
                      snapshot,
                      TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED)),
          () ->
              assertEquals(
                  glyphs * 2, counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_COPIED)),
          () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_MOVED)),
          () ->
              assertEquals(
                  glyphs, counter(snapshot, TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount,
                  counter(snapshot, TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  rangeCount * 4L,
                  counter(snapshot, TextDiagnosticCounter.RUN_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount * 3L,
                  counter(snapshot, TextDiagnosticCounter.RUN_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  glyphs * 2,
                  counter(snapshot, TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount * 3L,
                  counter(snapshot, TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  rangeCount, counter(snapshot, TextDiagnosticCounter.LINE_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount, counter(snapshot, TextDiagnosticCounter.LINE_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  rangeCount * 3L,
                  counter(snapshot, TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount,
                  counter(snapshot, TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  glyphs * 2,
                  counter(snapshot, TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_APPENDS)),
          () ->
              assertEquals(
                  rangeCount * 2L,
                  counter(snapshot, TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_FREEZES)),
          () ->
              assertEquals(
                  rangeCount, counter(snapshot, TextDiagnosticCounter.RESULT_BUILDER_FREEZES)),
          () -> assertEquals(0, publicApiEntries(snapshot)));
    }
  }

  @Test
  void highCountCaretQueriesUseOnlyLogarithmicFinalStopComparisons() {
    int glyphCount = 1024;
    int queryCount = 2048;
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl service = service(diagnostics);
    String text = "a".repeat(glyphCount);
    ResolvedMeasurement resolved =
        service.measureRange(
            text,
            0,
            text.length(),
            0,
            List.of(Font.DEFAULT),
            FONT_SIZE,
            LINE_HEIGHT,
            Float.POSITIVE_INFINITY,
            false);
    FinalLineCaretStops stops = resolved.lineCaretStops().getFirst();
    float finalAdvance = stops.advance(stops.size() - 1);
    diagnostics.reset();

    for (int query = 0; query < queryCount; query++) {
      int slot = query % glyphCount;
      stops.caretAt(finalAdvance * slot / glyphCount, diagnostics);
      stops.caretAtSourceIndex(slot, diagnostics);
    }

    DiagnosticSnapshot snapshot = diagnostics.snapshot();
    long comparisons = counter(snapshot, TextDiagnosticCounter.CARET_STOP_SEARCH_COMPARISONS);
    int comparisonBoundPerLookup = ceilLog2(stops.size()) + 1;
    assertAll(
        () -> assertTrue(comparisons > queryCount),
        () -> assertTrue(comparisons <= queryCount * 2L * comparisonBoundPerLookup),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.RANGE_PREPARATIONS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.NATIVE_KERNING_CALLS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_COPIED)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_MOVED)),
        () -> assertEquals(0, publicApiEntries(snapshot)));
  }

  private WholeEvidence runWholeScenario(WholeScenario scenario) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl service = service(diagnostics);
    TextMetrics metrics =
        service.measureText(
            scenario.text(),
            scenario.offsetX(),
            scenario.fonts(),
            FONT_SIZE,
            LINE_HEIGHT,
            scenario.maxWidth(),
            scenario.wordWrap());
    long sourceCodePoints = scenario.text().codePointCount(0, scenario.text().length());
    long separatorCodePoints =
        scenario.text().codePoints()
            .filter(codePoint -> codePoint == '\r' || codePoint == '\n')
            .count();
    long glyphCount = sourceCodePoints - separatorCodePoints;
    long finalRuns =
        metrics.lines().stream().mapToLong(line -> line.runs().size()).sum();
    long materializedGlyphCount =
        metrics.lines().stream()
            .flatMap(line -> line.runs().stream())
            .mapToLong(run -> run.glyphs().size())
            .sum();
    return new WholeEvidence(
        scenario,
        metrics,
        diagnostics.snapshot(),
        sourceCodePoints,
        glyphCount,
        materializedGlyphCount,
        finalRuns);
  }

  private void assertWholeFormulas(WholeEvidence evidence) {
    WholeScenario scenario = evidence.scenario();
    DiagnosticSnapshot snapshot = evidence.snapshot();
    long sourceCodePoints = evidence.sourceCodePoints();
    long glyphCount = evidence.glyphCount();
    long materializedGlyphCount = evidence.materializedGlyphCount();
    long lineCount = evidence.metrics().lines().size();
    long finalRuns = evidence.finalRuns();
    long initialRuns = counter(snapshot, TextDiagnosticCounter.RUN_BUILDER_APPENDS) - finalRuns;
    long effectiveFontCount = Math.max(1, scenario.fonts().size());

    assertAll(
        () -> assertEquals(1, counter(snapshot, TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(1, counter(snapshot, TextDiagnosticCounter.RANGE_PREPARATIONS)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS)),
        () ->
            assertEquals(
                sourceCodePoints,
                counter(snapshot, TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () ->
            assertEquals(
                glyphCount,
                counter(snapshot, TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS)),
        () ->
            assertEquals(
                glyphCount,
                counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () ->
            assertTrue(
                counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES)
                    >= glyphCount),
        () ->
            assertTrue(
                counter(snapshot, TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES)
                    <= glyphCount * effectiveFontCount * 2),
        () ->
            assertTrue(
                counter(snapshot, TextDiagnosticCounter.NATIVE_KERNING_CALLS)
                    <= Math.max(0, glyphCount - 1)),
        () ->
            assertEquals(
                sourceCodePoints,
                counter(snapshot, TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS)),
        () -> assertEquals(1, counter(snapshot, TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES)),
        () ->
            assertEquals(
                sourceCodePoints,
                counter(snapshot, TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS)),
        () ->
            assertEquals(
                glyphCount,
                counter(
                    snapshot,
                    TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED)),
        () ->
            assertEquals(
                materializedGlyphCount,
                counter(
                    snapshot,
                    TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED)),
        () ->
            assertEquals(
                glyphCount + materializedGlyphCount,
                counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_COPIED)),
        () -> assertEquals(0, counter(snapshot, TextDiagnosticCounter.GLYPH_SLOTS_MOVED)),
        () ->
            assertEquals(
                glyphCount + materializedGlyphCount,
                counter(snapshot, TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS)),
        () ->
            assertEquals(
                finalRuns + 1,
                counter(snapshot, TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES)),
        () ->
            assertEquals(
                finalRuns + 1,
                counter(snapshot, TextDiagnosticCounter.RUN_BUILDER_FREEZES)),
        () ->
            assertEquals(
                lineCount, counter(snapshot, TextDiagnosticCounter.LINE_BUILDER_APPENDS)),
        () ->
            assertEquals(
                lineCount, counter(snapshot, TextDiagnosticCounter.LINE_BUILDER_FREEZES)),
        () ->
            assertEquals(
                materializedGlyphCount + lineCount,
                counter(snapshot, TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_APPENDS)),
        () ->
            assertEquals(
                lineCount,
                counter(snapshot, TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_FREEZES)),
        () ->
            assertEquals(
                materializedGlyphCount * 2,
                counter(snapshot, TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_APPENDS)),
        () ->
            assertEquals(
                lineCount * 2,
                counter(snapshot, TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_FREEZES)),
        () -> assertEquals(1, counter(snapshot, TextDiagnosticCounter.RESULT_BUILDER_FREEZES)),
        () -> assertEquals(1, publicApiEntries(snapshot)),
        () -> assertTrue(lineCount <= sourceCodePoints + 1),
        () -> assertTrue(initialRuns >= 1 && initialRuns <= glyphCount),
        () -> assertTrue(finalRuns >= 1 && finalRuns <= glyphCount));
    if (scenario.expectedInitialRuns() >= 0) {
      assertEquals(scenario.expectedInitialRuns(), initialRuns, scenario.name());
    }
    if (scenario.sameFaceSourceKerning()) {
      assertEquals(
          glyphCount - 1,
          counter(snapshot, TextDiagnosticCounter.NATIVE_KERNING_CALLS),
          scenario.name());
    }
    if (scenario.maxWidth() == 0) {
      assertEquals(glyphCount, lineCount, scenario.name() + " must make one-glyph progress");
    }
  }

  private List<WholeScenario> wholeScenarios(int units, float wordWidth) {
    String sameFace = "AV".repeat(units);
    String missing = new String(Character.toChars(MISSING_CODE_POINT)).repeat(units);
    return List.of(
        new WholeScenario(
            "character-long-unbreakable",
            sameFace,
            List.of(Font.DEFAULT),
            0,
            wordWidth,
            false,
            1,
            true),
        new WholeScenario(
            "word-long-unbreakable", sameFace, List.of(Font.DEFAULT), 0, wordWidth, true, 1, true),
        new WholeScenario(
            "word-boundaries",
            "AV ".repeat(units),
            List.of(Font.DEFAULT),
            0,
            wordWidth,
            true,
            1,
            true),
        new WholeScenario(
            "word-long-deferred-suffix",
            "A " + "V".repeat(units * 4),
            List.of(Font.DEFAULT),
            0,
            wordWidth,
            true,
            1,
            true),
        new WholeScenario(
            "zero-width-line-start-kerning", sameFace, List.of(Font.DEFAULT), 0, 0, false, 1, true),
        new WholeScenario(
            "first-line-offset",
            sameFace,
            List.of(Font.DEFAULT),
            wordWidth / 2,
            wordWidth,
            false,
            1,
            true),
        new WholeScenario(
            "explicit-newlines-and-surrogates",
            "A😀\nB\rC\r\n雪".repeat(units),
            FALLBACK_FONTS,
            0,
            Float.POSITIVE_INFINITY,
            false,
            -1,
            false),
        new WholeScenario(
            "alternating-fallback",
            "A雪".repeat(units),
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            0,
            Float.POSITIVE_INFINITY,
            false,
            units * 2,
            false),
        new WholeScenario(
            "missing-replacement",
            missing,
            List.of(Font.DEFAULT),
            0,
            Float.POSITIVE_INFINITY,
            false,
            1,
            true));
  }

  private FontServiceImpl disabledService() {
    return new FontServiceImpl(new FontStorageImpl(), false);
  }

  private FontServiceImpl service(DiagnosticSession diagnostics) {
    return new FontServiceImpl(
        new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
  }

  private DiagnosticSession diagnostics() {
    return DiagnosticSession.enabled(Arrays.asList(TextDiagnosticCounter.values()));
  }

  private long counter(DiagnosticSnapshot snapshot, TextDiagnosticCounter counter) {
    return snapshot.value(counter);
  }

  private long publicApiEntries(DiagnosticSnapshot snapshot) {
    return Arrays.stream(TextDiagnosticCounter.values())
        .filter(counter -> counter.name().startsWith("TEXT_MEASURER_"))
        .mapToLong(snapshot::value)
        .sum();
  }

  private int ceilLog2(int value) {
    return value <= 1 ? 0 : Integer.SIZE - Integer.numberOfLeadingZeros(value - 1);
  }

  private record WholeScenario(
      String name,
      String text,
      List<Font> fonts,
      float offsetX,
      float maxWidth,
      boolean wordWrap,
      int expectedInitialRuns,
      boolean sameFaceSourceKerning) {}

  private record WholeEvidence(
      WholeScenario scenario,
      TextMetrics metrics,
      DiagnosticSnapshot snapshot,
      long sourceCodePoints,
      long glyphCount,
      long materializedGlyphCount,
      long finalRuns) {}
}
