package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FontServiceImplTest {
  private FontServiceImpl fontService;

  @BeforeEach
  void setUp() {
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
  }

  @Test
  void measureText_whenSingleLine_returnsWidthAndVerticalMetrics() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);

    assertEquals(1, metrics.lines().size());
    assertTrue(metrics.width() > 0);
    assertTrue(metrics.height() > 0);
    assertTrue(metrics.fontMetrics().ascent() > 0);
    assertTrue(metrics.fontMetrics().descent() > 0);
    assertEquals(metrics.fontMetrics().baseline(), metrics.lines().get(0).baseline());
  }

  @Test
  void compatibilityWrappers_matchUnifiedApi() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);
    TextLineMetrics line = fontService.getTextLineMetrics("abc", Font.DEFAULT, 16, 1.2f);

    assertEquals(metrics.lines().get(0), line);
    assertEquals(metrics.fontMetrics(), fontService.getFontMetrics(Font.DEFAULT, 16, 1.2f));
  }

  @Test
  void measureText_whenMaxWidthIsNearZero_returnsEmptyMetrics() {
    TextMetrics metrics = fontService.measureText("abc", 0, Font.DEFAULT, 16, 1.2f, 0, true);

    assertTrue(metrics.lines().isEmpty());
    assertEquals(0, metrics.width());
    assertEquals(0, metrics.height());
  }

  @Test
  void measureText_whenWrapped_preservesLineOrder() {
    TextMetrics metrics = fontService.measureText("aa aa", 0, Font.DEFAULT, 16, 1.2f, 20, true);

    assertFalse(metrics.lines().isEmpty());
    assertEquals("aa", metrics.lines().get(0).characters().toString());
  }

  @Test
  void measureText_whenWhitespace_reportsAdvance() {
    TextMetrics metrics = fontService.measureText(" ", Font.DEFAULT, 16, 1.2f);

    assertTrue(metrics.width() > 0);
  }

  @Test
  void measureText_roundsGlyphAdvancesLikeNanoVgFontStash() {
    TextMetrics metrics = fontService.measureText("Horizontal auto", Font.DEFAULT, 16, 1.2f);

    assertEquals(Math.round(metrics.width()), metrics.width());
  }

  @Test
  void measureText_usesStbMappingEmScaleLikeNanoVgFontStash() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.0f);

    assertTrue(metrics.fontMetrics().ascent() + metrics.fontMetrics().descent() > 16);
    assertEquals(metrics.fontMetrics().ascent(), metrics.fontMetrics().baseline());
  }

  @Test
  void getTextCaretMetrics_whenOffsetBeforeLine_returnsStartCaret() {
    TextCaretMetrics caret = fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, -1);

    assertEquals(0, caret.charIndex());
    assertEquals(0, caret.x());
  }

  @Test
  void getTextCaretMetrics_whenOffsetPastLine_returnsEndCaretAtMeasuredWidth() {
    TextMetrics metrics = fontService.measureText("abc", Font.DEFAULT, 16, 1.2f);

    TextCaretMetrics caret = fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, 10_000);

    assertEquals(3, caret.charIndex());
    assertEquals(metrics.width(), caret.x());
  }

  @Test
  void getTextCaretMetrics_whenOffsetPassesFirstGlyph_returnsNextCaretStop() {
    TextMetrics firstGlyph = fontService.measureText("a", Font.DEFAULT, 16, 1.2f);

    TextCaretMetrics caret =
        fontService.getTextCaretMetrics("abc", Font.DEFAULT, 16, firstGlyph.width() + 1);

    assertTrue(caret.charIndex() >= 1);
    assertTrue(caret.x() >= firstGlyph.width());
  }

  @Test
  void measureText_usesBundledCjkFallbackForMissingRobotoGlyphs() {
    String text = "R\u00f8gue \u96ea Seed";

    TextMetrics metrics = fontService.measureText(text, Font.DEFAULT, 16, 1.2f);
    TextCaretMetrics caret = fontService.getTextCaretMetrics(text, Font.DEFAULT, 16, Float.MAX_VALUE);

    assertTrue(fontService.measureText("\u96ea", Font.DEFAULT, 16, 1.2f).width() > 0);
    assertEquals(metrics.width(), caret.x());
    assertEquals(text.length(), caret.charIndex());
  }

  @Test
  void measureText_resolvesOrderedFamilyRunsAndRetainsSourceRanges() {
    TextMetrics metrics =
        fontService.measureText(
            "R\u00f8gue \u96ea Seed",
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            16,
            1.2f);

    List<ResolvedTextRun> runs = metrics.lines().get(0).runs();
    assertTrue(runs.stream().anyMatch(run -> run.font().equals(Font.ROBOTO_REGULAR)));
    assertTrue(runs.stream().anyMatch(run -> run.font().equals(Font.NOTO_SANS_CJK_SC_REGULAR)));
    assertEquals(0, runs.get(0).sourceStart());
    assertEquals("R\u00f8gue ", "R\u00f8gue \u96ea Seed".substring(0, runs.get(1).sourceStart()));
    assertEquals("\u96ea", "R\u00f8gue \u96ea Seed".substring(6, 7));
  }

  @Test
  void measureText_keepsSupplementaryCodePointAtomicAndMarksMissingGlyphs() {
    String text = "A\uD83D\uDE00\uDBFF\uDFFF";
    TextMetrics metrics =
        fontService.measureText(
            text, List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR), 16, 1.2f);

    List<ResolvedTextRun> runs = metrics.lines().get(0).runs();
    assertTrue(runs.stream().flatMap(run -> run.glyphs().stream())
        .anyMatch(glyph -> glyph.sourceStart() == 1 && glyph.sourceEnd() == 3));
    assertTrue(runs.stream().flatMap(run -> run.glyphs().stream())
        .anyMatch(glyph -> glyph.sourceStart() == 3 && glyph.sourceEnd() == 5 && glyph.replacement()));
    assertTrue(metrics.width() > 0);
  }

  @Test
  void diagnostics_exposeDuplicateResolutionAndQuadraticCurrentRunCopyingWithoutChangingOutput() {
    String text = "aaaa";
    TextMetrics expected = fontService.measureText(text, Font.DEFAULT, 16, 1.2f);
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);

    TextMetrics actual = instrumented.measureText(text, Font.DEFAULT, 16, 1.2f);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(expected, actual);
    assertEquals(8, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(8, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(16, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES));
    assertEquals(15, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS));
    assertEquals(
        1,
        snapshot.value(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES));
    assertEquals(
        1,
        snapshot.value(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES));
  }

  @Test
  void diagnostics_glyphSlotCopiesAndRelocationsIndependentlyMatchQuadraticCurrentTotals() {
    assertQuadraticRunAssemblyCounts(1, 0, 0);
    assertQuadraticRunAssemblyCounts(4, 15, 6);
    assertQuadraticRunAssemblyCounts(8, 63, 28);
    assertQuadraticRunAssemblyCounts(16, 255, 120);
  }

  @Test
  void disabledDiagnosticsUseStableNoOpResultsAcrossMeasurement() {
    FontServiceImpl disabled = new FontServiceImpl(new FontStorageImpl(), false);
    DiagnosticSnapshot before = disabled.diagnostics().snapshot();

    disabled.measureText("unchanged", Font.DEFAULT, 16, 1.2f);

    assertSame(DiagnosticSession.disabled(), disabled.diagnostics());
    assertSame(before, disabled.diagnostics().snapshot());
  }

  private DiagnosticSession diagnostics() {
    return DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
  }

  private void assertQuadraticRunAssemblyCounts(
      int glyphCount, long expectedCopies, long expectedMoves) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);

    instrumented.measureText("a".repeat(glyphCount), Font.DEFAULT, 16, 1.2f);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(expectedCopies, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(expectedMoves, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(
        glyphCount - 1,
        snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(
        glyphCount - 1,
        snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
  }
}
