package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.lwjgl.stb.STBTruetype.stbtt_FindGlyphIndex;
import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.FontMetrics;
import com.spinyowl.spinygui.core.system.font.ResolvedGlyph;
import com.spinyowl.spinygui.core.system.font.ResolvedTextRun;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import com.spinyowl.spinygui.core.system.font.TextLineMetrics;
import com.spinyowl.spinygui.core.system.font.TextMeasurer;
import com.spinyowl.spinygui.core.system.font.TextMetrics;
import com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops;
import com.spinyowl.spinygui.core.system.font.internal.ResolvedMeasurement;
import com.spinyowl.spinygui.core.util.IOUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

class FontServiceImplMeasurementContractTest {
  private static final float EPSILON = 0.000001f;
  private static final float FONT_SIZE = 16f;
  private static final float LINE_HEIGHT = 1.2f;
  private static final int MISSING_CODE_POINT = 0x10FFFF;
  private static final int REPLACEMENT_CODE_POINT = 0xFFFD;
  private static final Font MATERIAL_ICONS =
      new Font("Material Icons", "fonts/MaterialIcons-Regular.ttf");
  private static final Font NOTO_EMOJI = new Font("Noto Emoji", "fonts/NotoEmoji-Regular.ttf");

  private FontServiceImpl fontService;

  @BeforeEach
  void setUp() {
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
    fontService.installSemanticOwner();
    fontService.loadFont(MATERIAL_ICONS.path());
    fontService.loadFont(NOTO_EMOJI.path());
  }

  @Test
  void wordWrapTrue_usesWordBoundary() {
    float maxWidth = fontService.measureText("aa ", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();

    TextMetrics metrics =
        fontService.measureText(
            "aa aa", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, true);

    assertEquals(List.of("aa", "aa"), lineCharacters(metrics));
  }

  @Test
  void wordWrapFalse_usesCharacterBoundary() {
    float maxWidth = fontService.measureText("aa ", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();

    TextMetrics metrics =
        fontService.measureText(
            "aa aa", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, false);

    assertEquals(List.of("aa ", "aa"), lineCharacters(metrics));
  }

  @Test
  void wordWrapTrue_withoutWordBoundary_fallsBackToCharacterBoundary() {
    float maxWidth = fontService.measureText("aa", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();

    TextMetrics metrics =
        fontService.measureText(
            "aaaa", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, true);

    assertEquals(List.of("aa", "aa"), lineCharacters(metrics));
  }

  @Test
  void sourceGlyph_usesFirstFaceContainingSourceCodePoint() {
    TextMetrics metrics =
        fontService.measureText(
            "A\u96ea",
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            FONT_SIZE,
            LINE_HEIGHT);

    List<ResolvedTextRun> runs = metrics.lines().get(0).runs();
    assertEquals(Font.ROBOTO_REGULAR, runs.get(0).font());
    assertEquals(Font.NOTO_SANS_CJK_SC_REGULAR, runs.get(1).font());
    assertFalse(runs.get(0).replacementMarker());
    assertFalse(runs.get(1).replacementMarker());
  }

  @Test
  void replacementFixtures_haveKnownSourceAndReplacementCoverage() {
    assertAll(
        () -> assertEquals(0, glyphIndex(MATERIAL_ICONS, MISSING_CODE_POINT)),
        () -> assertEquals(0, glyphIndex(MATERIAL_ICONS, REPLACEMENT_CODE_POINT)),
        () -> assertEquals(0, glyphIndex(Font.ROBOTO_REGULAR, MISSING_CODE_POINT)),
        () -> assertNotEquals(0, glyphIndex(Font.ROBOTO_REGULAR, REPLACEMENT_CODE_POINT)));
  }

  @Test
  void missingSourceAndReplacement_usesPrimaryNotdefAndRetainsSourceEvidence() {
    String text = new String(Character.toChars(MISSING_CODE_POINT));

    TextMetrics metrics =
        fontService.measureText(text, List.of(MATERIAL_ICONS), FONT_SIZE, LINE_HEIGHT);

    ResolvedTextRun run = onlyRun(metrics);
    ResolvedGlyph glyph = onlyGlyph(run);
    TextCaretMetrics end =
        fontService.getTextCaretMetrics(
            text, List.of(MATERIAL_ICONS), FONT_SIZE, Float.POSITIVE_INFINITY);
    float midpoint = metrics.width() / 2f;
    TextCaretMetrics below =
        fontService.getTextCaretMetrics(
            text, List.of(MATERIAL_ICONS), FONT_SIZE, Math.nextDown(midpoint));
    TextCaretMetrics tie =
        fontService.getTextCaretMetrics(text, List.of(MATERIAL_ICONS), FONT_SIZE, midpoint);
    TextCaretMetrics above =
        fontService.getTextCaretMetrics(
            text, List.of(MATERIAL_ICONS), FONT_SIZE, Math.nextUp(midpoint));
    assertAll(
        () -> assertEquals(MATERIAL_ICONS, run.font()),
        () -> assertTrue(run.replacementMarker()),
        () -> assertEquals(0, glyph.sourceStart()),
        () -> assertEquals(2, glyph.sourceEnd()),
        () -> assertEquals(MISSING_CODE_POINT, glyph.sourceCodePoint()),
        () -> assertEquals(REPLACEMENT_CODE_POINT, glyph.renderedCodePoint()),
        () -> assertEquals(MATERIAL_ICONS, glyph.font()),
        () -> assertTrue(glyph.replacement()),
        () -> assertEquals(0, below.charIndex()),
        () -> assertEquals(0, below.x(), EPSILON),
        () -> assertEquals(2, tie.charIndex()),
        () -> assertEquals(metrics.width(), tie.x(), EPSILON),
        () -> assertEquals(2, above.charIndex()),
        () -> assertEquals(metrics.width(), above.x(), EPSILON),
        () -> assertNotEquals(1, below.charIndex()),
        () -> assertNotEquals(1, tie.charIndex()),
        () -> assertNotEquals(1, above.charIndex()),
        () -> assertEquals(2, end.charIndex()),
        () -> assertEquals(metrics.width(), end.x(), EPSILON));
  }

  @Test
  void missingSource_usesFirstFaceContainingReplacementGlyph() {
    String text = new String(Character.toChars(MISSING_CODE_POINT));
    float replacementAdvance =
        fontService
            .measureText("\uFFFD", List.of(Font.ROBOTO_REGULAR), FONT_SIZE, LINE_HEIGHT)
            .width();

    TextMetrics metrics =
        fontService.measureText(
            text, List.of(MATERIAL_ICONS, Font.ROBOTO_REGULAR), FONT_SIZE, LINE_HEIGHT);

    ResolvedTextRun run = onlyRun(metrics);
    ResolvedGlyph glyph = onlyGlyph(run);
    TextCaretMetrics end =
        fontService.getTextCaretMetrics(
            text,
            List.of(MATERIAL_ICONS, Font.ROBOTO_REGULAR),
            FONT_SIZE,
            Float.POSITIVE_INFINITY);
    assertAll(
        () -> assertEquals(Font.ROBOTO_REGULAR, run.font()),
        () -> assertTrue(run.replacementMarker()),
        () -> assertEquals(0, glyph.sourceStart()),
        () -> assertEquals(2, glyph.sourceEnd()),
        () -> assertEquals(MISSING_CODE_POINT, glyph.sourceCodePoint()),
        () -> assertEquals(REPLACEMENT_CODE_POINT, glyph.renderedCodePoint()),
        () -> assertEquals(Font.ROBOTO_REGULAR, glyph.font()),
        () -> assertTrue(glyph.replacement()),
        () -> assertEquals(16, run.advance(), EPSILON),
        () -> assertEquals(replacementAdvance, run.advance(), EPSILON),
        () -> assertEquals(metrics.width(), run.advance(), EPSILON),
        () -> assertEquals(2, end.charIndex()));
  }

  @Test
  void emptyFontChain_isEquivalentToDefaultFontChainIncludingRunEvidence() {
    TextMetrics expected =
        fontService.measureText("abc", List.of(Font.DEFAULT), FONT_SIZE, LINE_HEIGHT);

    TextMetrics actual = fontService.measureText("abc", List.of(), FONT_SIZE, LINE_HEIGHT);

    assertEquals(expected, actual);
    assertFalse(actual.lines().get(0).runs().isEmpty());
  }

  @Test
  void lfSeparator_isExcludedAndTrailingSeparatorCreatesEmptyFinalLine() {
    TextMetrics metrics =
        fontService.measureText("a\n", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);

    assertEquals(List.of("a:0-1", ":2-2"), lineSignatures(metrics));
  }

  @Test
  void emptyText_preservesOneEmptyLineAndPrimaryMetrics() {
    TextMetrics metrics =
        fontService.measureText("", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    TextCaretMetrics caret =
        fontService.getTextCaretMetrics("", Font.DEFAULT, FONT_SIZE, Float.POSITIVE_INFINITY);

    assertAll(
        () -> assertEquals(List.of(":0-0"), lineSignatures(metrics)),
        () -> assertEquals(0, metrics.width(), EPSILON),
        () -> assertEquals(metrics.lineHeight(), metrics.height(), EPSILON),
        () -> assertEquals(metrics.fontMetrics(), metrics.lines().get(0).fontMetrics()),
        () -> assertEquals(0, caret.charIndex()),
        () -> assertEquals(0, caret.x(), EPSILON));
  }

  @Test
  void crSeparator_isExcludedAndTrailingSeparatorCreatesEmptyFinalLine() {
    TextMetrics metrics =
        fontService.measureText("a\r", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);

    assertEquals(List.of("a:0-1", ":2-2"), lineSignatures(metrics));
  }

  @Test
  void crlfSeparator_isAtomicAndExcludedFromLineRanges() {
    TextMetrics metrics =
        fontService.measureText("a\r\nb", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);

    assertEquals(List.of("a:0-1", "b:3-4"), lineSignatures(metrics));
  }

  @Test
  void trailingCrlfSeparator_isAtomicAndCreatesEmptyFinalLine() {
    TextMetrics metrics =
        fontService.measureText("a\r\n", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);

    assertEquals(List.of("a:0-1", ":3-3"), lineSignatures(metrics));
  }

  @Test
  void fallbackGlyph_doesNotChangePrimaryFaceVerticalMetrics() {
    TextMetrics primary =
        fontService.measureText("", Font.ROBOTO_REGULAR, FONT_SIZE, LINE_HEIGHT);

    TextMetrics fallback =
        fontService.measureText(
            "\u96ea", List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR), FONT_SIZE,
            LINE_HEIGHT);

    assertEquals(primary.fontMetrics(), fallback.fontMetrics());
    assertEquals(primary.fontMetrics(), fallback.lines().get(0).fontMetrics());
    assertEquals(primary.fontMetrics().baseline(), fallback.lines().get(0).baseline());
  }

  @Test
  void validNumericBoundaries_acceptZeroLineHeightPositiveOffsetAndInfiniteWidth() {
    TextMetrics metrics =
        fontService.measureText(
            "abc", 2.5f, Font.DEFAULT, Float.MIN_NORMAL, 0, Float.POSITIVE_INFINITY, false);

    assertEquals(1, metrics.lines().size());
  }

  @Test
  void narrowPositiveWidth_placesOneCodePointPerLineAndMakesProgress() {
    TextMetrics metrics =
        fontService.measureText("abc", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, 0.1f, true);

    assertEquals(List.of("a", "b", "c"), lineCharacters(metrics));
  }

  @Test
  void supplementaryCodePoint_remainsAtomicAcrossWrappedLineRunGlyphAndCaretBoundaries() {
    String text = "A\uD83D\uDE00B";

    TextMetrics metrics =
        fontService.measureText(text, 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, 0.1f, false);
    TextLineMetrics middleLine = metrics.lines().get(1);
    ResolvedTextRun middleRun = middleLine.runs().get(0);
    ResolvedGlyph middleGlyph = middleRun.glyphs().get(0);
    TextCaretMetrics end =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, Float.POSITIVE_INFINITY);

    assertAll(
        () -> assertEquals(List.of("A:0-1", "\uD83D\uDE00:1-3", "B:3-4"), lineSignatures(metrics)),
        () -> assertEquals(2, middleLine.charCount()),
        () -> assertEquals(1, middleRun.sourceStart()),
        () -> assertEquals(3, middleRun.sourceEnd()),
        () -> assertEquals(1, middleGlyph.sourceStart()),
        () -> assertEquals(3, middleGlyph.sourceEnd()),
        () -> assertEquals(4, end.charIndex()));
  }

  @Test
  void supplementarySourceGlyphCaretMidpoint_returnsOnlyWholeUtf16Boundaries() {
    int codePoint = 0x1F600;
    String text = new String(Character.toChars(codePoint));
    assertNotEquals(0, glyphIndex(NOTO_EMOJI, codePoint));

    TextMetrics metrics =
        fontService.measureText(text, List.of(NOTO_EMOJI), FONT_SIZE, LINE_HEIGHT);
    ResolvedTextRun run = onlyRun(metrics);
    ResolvedGlyph glyph = onlyGlyph(run);
    float midpoint = metrics.width() / 2f;
    TextCaretMetrics below =
        fontService.getTextCaretMetrics(
            text, List.of(NOTO_EMOJI), FONT_SIZE, Math.nextDown(midpoint));
    TextCaretMetrics tie =
        fontService.getTextCaretMetrics(text, List.of(NOTO_EMOJI), FONT_SIZE, midpoint);
    TextCaretMetrics above =
        fontService.getTextCaretMetrics(
            text, List.of(NOTO_EMOJI), FONT_SIZE, Math.nextUp(midpoint));

    assertAll(
        () -> assertEquals(NOTO_EMOJI, run.font()),
        () -> assertFalse(run.replacementMarker()),
        () -> assertEquals(0, glyph.sourceStart()),
        () -> assertEquals(2, glyph.sourceEnd()),
        () -> assertEquals(codePoint, glyph.sourceCodePoint()),
        () -> assertEquals(codePoint, glyph.renderedCodePoint()),
        () -> assertFalse(glyph.replacement()),
        () -> assertEquals(0, below.charIndex()),
        () -> assertEquals(0, below.x(), EPSILON),
        () -> assertEquals(2, tie.charIndex()),
        () -> assertEquals(metrics.width(), tie.x(), EPSILON),
        () -> assertEquals(2, above.charIndex()),
        () -> assertEquals(metrics.width(), above.x(), EPSILON),
        () -> assertNotEquals(1, below.charIndex()),
        () -> assertNotEquals(1, tie.charIndex()),
        () -> assertNotEquals(1, above.charIndex()));
  }

  @Test
  void firstLineOffset_reducesFiniteWrappingCapacityAndOnlyAffectsOccupiedExtent() {
    float initialOffset = 4.5f;
    float maxWidth =
        fontService.measureText("aa", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();

    TextMetrics withoutOffset =
        fontService.measureText(
            "aa", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, false);
    TextMetrics withOffset =
        fontService.measureText(
            "aa",
            initialOffset,
            Font.DEFAULT,
            FONT_SIZE,
            LINE_HEIGHT,
            maxWidth,
            false);
    TextLineMetrics first = withOffset.lines().get(0);
    TextLineMetrics second = withOffset.lines().get(1);
    float firstAdvance = first.runs().get(0).advance();
    float secondAdvance = second.runs().get(0).advance();
    TextCaretMetrics caret =
        fontService.getTextCaretMetrics("a", Font.DEFAULT, FONT_SIZE, Float.POSITIVE_INFINITY);

    assertAll(
        () -> assertEquals(List.of("aa"), lineCharacters(withoutOffset)),
        () -> assertEquals(List.of("a", "a"), lineCharacters(withOffset)),
        () -> assertEquals(initialOffset + firstAdvance, first.width(), EPSILON),
        () -> assertEquals(secondAdvance, second.width(), EPSILON),
        () -> assertEquals(firstAdvance, secondAdvance, EPSILON),
        () -> assertEquals(first.width(), withOffset.width(), EPSILON),
        () -> assertEquals(first.fontMetrics().baseline(), first.baseline(), EPSILON),
        () -> assertEquals(second.fontMetrics().baseline(), second.baseline(), EPSILON),
        () -> assertEquals(firstAdvance, caret.x(), EPSILON));
  }

  @Test
  void caretMidpoint_belowStopsBeforeAndExactTieAdvancesToFollowingBoundary() {
    float advance = fontService.measureText("a", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();
    float midpoint = advance / 2f;

    TextCaretMetrics below =
        fontService.getTextCaretMetrics("a", Font.DEFAULT, FONT_SIZE, Math.nextDown(midpoint));
    TextCaretMetrics tie =
        fontService.getTextCaretMetrics("a", Font.DEFAULT, FONT_SIZE, midpoint);
    TextCaretMetrics above =
        fontService.getTextCaretMetrics("a", Font.DEFAULT, FONT_SIZE, Math.nextUp(midpoint));

    assertAll(
        () -> assertEquals(0, below.charIndex()),
        () -> assertEquals(0, below.x(), EPSILON),
        () -> assertEquals(1, tie.charIndex()),
        () -> assertEquals(advance, tie.x(), EPSILON),
        () -> assertEquals(1, above.charIndex()),
        () -> assertEquals(advance, above.x(), EPSILON));
  }

  @Test
  void zeroAdvanceCaretTie_advancesFollowingStopAndPreservesFirstAndLastBoundaries() {
    String text = "a\u0301b";
    float firstAdvance = fontService.measureText("a", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();
    float throughZeroAdvance =
        fontService.measureText("a\u0301", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();

    TextCaretMetrics below =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, Math.nextDown(firstAdvance));
    TextCaretMetrics tie =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, firstAdvance);
    TextCaretMetrics above =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, Math.nextUp(firstAdvance));
    TextCaretMetrics start = fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, 0);
    TextCaretMetrics end =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, Float.POSITIVE_INFINITY);

    assertAll(
        () -> assertEquals(firstAdvance, throughZeroAdvance, EPSILON),
        () -> assertEquals(1, below.charIndex()),
        () -> assertEquals(2, tie.charIndex()),
        () -> assertEquals(2, above.charIndex()),
        () -> assertEquals(0, start.charIndex()),
        () -> assertEquals(0, start.x(), EPSILON),
        () -> assertEquals(text.length(), end.charIndex()),
        () -> assertEquals(
            fontService.measureText(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width(),
            end.x(),
            EPSILON));
  }

  @Test
  void caretAndLineEntryPoints_delegateToOneCompleteMeasurementWithExactCounters() {
    String text = "A\uD83D\uDE00B";
    TextCaretMetrics expectedCaret =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, 1);
    TextLineMetrics expectedLine =
        fontService.getTextLineMetrics(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl service = instrumentedFontService(diagnostics);

    TextCaretMetrics listCaretResult =
        service.getTextCaretMetrics(text, List.of(Font.DEFAULT), FONT_SIZE, 1);
    assertEquals(expectedCaret, listCaretResult);
    DiagnosticSnapshot listCaret = diagnostics.snapshot();
    assertSingleMeasurementWork(listCaret, 3, 1);
    assertExactApiEntries(
        listCaret,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES, 1L));
    assertAll(
        () ->
            assertEquals(
                1,
                listCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES)),
        () ->
            assertEquals(
                0,
                listCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES)));

    diagnostics.reset();
    TextCaretMetrics fontCaretResult =
        service.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, 1);
    assertEquals(expectedCaret, fontCaretResult);
    DiagnosticSnapshot fontCaret = diagnostics.snapshot();
    assertSingleMeasurementWork(fontCaret, 3, 2);
    assertExactApiEntries(
        fontCaret,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES,
            1L));
    assertAll(
        () ->
            assertEquals(
                1,
                fontCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES)),
        () ->
            assertEquals(
                1,
                fontCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES)));

    diagnostics.reset();
    TextLineMetrics listLineResult =
        service.getTextLineMetrics(text, List.of(Font.DEFAULT), FONT_SIZE, LINE_HEIGHT);
    assertEquals(expectedLine, listLineResult);
    DiagnosticSnapshot listLine = diagnostics.snapshot();
    assertSingleMeasurementWork(listLine, 3, 3);
    assertExactApiEntries(
        listLine,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));
    assertAll(
        () ->
            assertEquals(
                1,
                listLine.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES)),
        () ->
            assertEquals(
                1,
                listLine.value(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES)),
        () ->
            assertEquals(
                1,
                listLine.value(
                    TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES)));

    diagnostics.reset();
    TextLineMetrics fontLineResult =
        service.getTextLineMetrics(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    assertEquals(expectedLine, fontLineResult);
    DiagnosticSnapshot fontLine = diagnostics.snapshot();
    assertSingleMeasurementWork(fontLine, 3, 3);
    assertExactApiEntries(
        fontLine,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));
    assertAll(
        () ->
            assertEquals(
                1,
                fontLine.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_ENTRIES)),
        () ->
            assertEquals(
                1,
                fontLine.value(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES)),
        () ->
            assertEquals(
                1,
                fontLine.value(
                    TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES)));

    diagnostics.reset();
    TextMeasurer legacy = new LegacyDelegatingTextMeasurer(service);
    TextCaretMetrics defaultCaretResult =
        legacy.getTextCaretMetrics(text, List.of(Font.DEFAULT), FONT_SIZE, 1);
    assertEquals(expectedCaret, defaultCaretResult);
    DiagnosticSnapshot defaultCaret = diagnostics.snapshot();
    assertSingleMeasurementWork(defaultCaret, 3, 3);
    assertExactApiEntries(
        defaultCaret,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES,
            2L,
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES,
            1L));
    assertAll(
        () ->
            assertEquals(
                2,
                defaultCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES)),
        () ->
            assertEquals(
                1,
                defaultCaret.value(
                    TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES)));
  }

  @Test
  void everyTextMeasurerMetricEntryPointDelegatesOnceWithExactCountersAndParity() {
    String text = "A\uD83D\uDE00B";
    List<Font> fonts = List.of(Font.DEFAULT);
    float offsetX = 2.5f;
    float maxWidth = Float.POSITIVE_INFINITY;
    TextMetrics expectedSimple =
        fontService.measureText(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    TextMetrics expectedFull =
        fontService.measureText(
            text, offsetX, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, false);
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl service = instrumentedFontService(diagnostics);

    TextMetrics listSimple = service.measureText(text, fonts, FONT_SIZE, LINE_HEIGHT);
    assertEquals(expectedSimple, listSimple);
    DiagnosticSnapshot listSimpleSnapshot = diagnostics.snapshot();
    assertSingleMeasurementWork(listSimpleSnapshot, 3, 2);
    assertExactApiEntries(
        listSimpleSnapshot,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    TextMetrics listFull =
        service.measureText(
            text, offsetX, fonts, FONT_SIZE, LINE_HEIGHT, maxWidth, false);
    assertEquals(expectedFull, listFull);
    DiagnosticSnapshot listFullSnapshot = diagnostics.snapshot();
    assertSingleMeasurementWork(listFullSnapshot, 3, 1);
    assertExactApiEntries(
        listFullSnapshot,
        Map.of(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES, 1L));

    diagnostics.reset();
    TextMetrics fontSimple = service.measureText(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    assertEquals(expectedSimple, fontSimple);
    DiagnosticSnapshot fontSimpleSnapshot = diagnostics.snapshot();
    assertSingleMeasurementWork(fontSimpleSnapshot, 3, 2);
    assertExactApiEntries(
        fontSimpleSnapshot,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    TextMetrics fontFull =
        service.measureText(
            text, offsetX, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, false);
    assertEquals(expectedFull, fontFull);
    DiagnosticSnapshot fontFullSnapshot = diagnostics.snapshot();
    assertSingleMeasurementWork(fontFullSnapshot, 3, 2);
    assertExactApiEntries(
        fontFullSnapshot,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    TextMetrics compatibility =
        service.getTextMetrics(
            text, offsetX, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, maxWidth, false);
    assertEquals(expectedFull, compatibility);
    DiagnosticSnapshot compatibilitySnapshot = diagnostics.snapshot();
    assertSingleMeasurementWork(compatibilitySnapshot, 3, 3);
    assertExactApiEntries(
        compatibilitySnapshot,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_METRICS_FONT_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            1L));
  }

  @Test
  void textMeasurerListDefaultsDelegateThroughFiveAbstractMethodsWithExactCounters() {
    String text = "A\uD83D\uDE00B";
    List<Font> fonts = List.of(Font.DEFAULT);
    TextMetrics expected = fontService.measureText(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    TextLineMetrics expectedLine = expected.lines().get(0);
    TextCaretMetrics expectedCaret =
        fontService.getTextCaretMetrics(text, Font.DEFAULT, FONT_SIZE, 1);
    DiagnosticSession diagnostics = diagnostics();
    TextMeasurer defaults =
        new DefaultOnlyDelegatingTextMeasurer(instrumentedFontService(diagnostics));

    assertEquals(expected, defaults.measureText(text, fonts, FONT_SIZE, LINE_HEIGHT));
    DiagnosticSnapshot simple = diagnostics.snapshot();
    assertSingleMeasurementWork(simple, 3, 4);
    assertExactApiEntries(
        simple,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            2L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    assertEquals(
        expected,
        defaults.measureText(
            text, 0, fonts, FONT_SIZE, LINE_HEIGHT, Float.MAX_VALUE, false));
    DiagnosticSnapshot full = diagnostics.snapshot();
    assertSingleMeasurementWork(full, 3, 3);
    assertExactApiEntries(
        full,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            2L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    assertEquals(
        expectedLine, defaults.getTextLineMetrics(text, fonts, FONT_SIZE, LINE_HEIGHT));
    DiagnosticSnapshot line = diagnostics.snapshot();
    assertSingleMeasurementWork(line, 3, 5);
    assertExactApiEntries(
        line,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_LINE_METRICS_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_ENTRIES,
            1L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES,
            2L,
            TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES,
            1L));

    diagnostics.reset();
    assertEquals(expectedCaret, defaults.getTextCaretMetrics(text, fonts, FONT_SIZE, 1));
    DiagnosticSnapshot caret = diagnostics.snapshot();
    assertSingleMeasurementWork(caret, 3, 3);
    assertExactApiEntries(
        caret,
        Map.of(
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_LIST_ENTRIES,
            2L,
            TextDiagnosticCounter.TEXT_MEASURER_GET_TEXT_CARET_METRICS_FONT_ENTRIES,
            1L));
  }

  @Test
  void invalidNumericInputs_areRejected() {
    assertAll(
        () -> assertInvalid(0, 10, 0, 1),
        () -> assertInvalid(0, 10, -1, 1),
        () -> assertInvalid(0, 10, Float.NaN, 1),
        () -> assertInvalid(0, 10, Float.POSITIVE_INFINITY, 1),
        () -> assertInvalid(0, 10, 1, -1),
        () -> assertInvalid(0, 10, 1, Float.NaN),
        () -> assertInvalid(0, 10, 1, Float.POSITIVE_INFINITY),
        () -> assertInvalid(-1, 10, 1, 1),
        () -> assertInvalid(Float.NaN, 10, 1, 1),
        () -> assertInvalid(Float.NEGATIVE_INFINITY, 10, 1, 1),
        () -> assertInvalid(Float.POSITIVE_INFINITY, 10, 1, 1),
        () -> assertInvalid(0, -1, 1, 1),
        () -> assertInvalid(0, Float.NaN, 1, 1),
        () -> assertInvalid(0, Float.NEGATIVE_INFINITY, 1, 1));
  }

  @Test
  void zeroWidth_isValidAndMakesProgress() {
    TextMetrics metrics =
        fontService.measureText("abc", 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, 0, true);

    assertEquals(List.of("a", "b", "c"), lineCharacters(metrics));
  }

  @Test
  void fractionalKerningFallbackAndMultilineAccumulation_matchNanoVgFontStashOrder() {
    TextMetrics metrics =
        fontService.measureText(
            "AV\u96ea\nVA",
            0,
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            13.25f,
            1.17f,
            Float.POSITIVE_INFINITY,
            false);

    FontMetrics font = metrics.fontMetrics();
    assertAll(
        () -> assertEquals(13.884033f, font.ascent(), EPSILON),
        () -> assertEquals(3.5906982f, font.descent(), EPSILON),
        () -> assertEquals(0, font.lineGap(), EPSILON),
        () -> assertEquals(17.474731f, font.lineHeight(), EPSILON),
        () -> assertEquals(13.884033f, font.baseline(), EPSILON),
        () -> assertEquals(30, metrics.width(), EPSILON),
        () -> assertEquals(34.949463f, metrics.height(), EPSILON),
        () -> assertEquals(17.474731f, metrics.lineHeight(), EPSILON),
        () -> assertEquals(List.of("AV\u96ea:0-3", "VA:4-6"), lineSignatures(metrics)),
        () -> assertEquals(30, metrics.lines().get(0).width(), EPSILON),
        () -> assertEquals(17, metrics.lines().get(1).width(), EPSILON),
        () -> assertEquals(17, metrics.lines().get(0).runs().get(0).advance(), EPSILON),
        () -> assertEquals(13, metrics.lines().get(0).runs().get(1).advance(), EPSILON));
  }

  @Test
  void pixelRoundedVerticalMetrics_roundComponentsBeforeLineCountAccumulation() {
    FontServiceImpl rounded = new FontServiceImpl(new FontStorageImpl(), true);
    rounded.installSemanticOwner();

    TextMetrics metrics =
        rounded.measureText(
            "AV\nVA", 0, Font.DEFAULT, 13.25f, 1.17f, Float.POSITIVE_INFINITY, false);

    assertAll(
        () -> assertEquals(14, metrics.fontMetrics().ascent(), EPSILON),
        () -> assertEquals(4, metrics.fontMetrics().descent(), EPSILON),
        () -> assertEquals(0, metrics.fontMetrics().lineGap(), EPSILON),
        () -> assertEquals(17, metrics.fontMetrics().lineHeight(), EPSILON),
        () -> assertEquals(14, metrics.fontMetrics().baseline(), EPSILON),
        () -> assertEquals(17, metrics.lines().get(0).height(), EPSILON),
        () -> assertEquals(14, metrics.lines().get(0).baseline(), EPSILON),
        () -> assertEquals(34, metrics.height(), EPSILON));
  }

  @Test
  void finalLineCaretStops_pairAbsoluteCodePointBoundariesWithRebasedAdvances() {
    String text = "A\uD83D\uDE00\n";
    TextMetrics metrics =
        fontService.measureText(text, 0, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT, 0.1f, false);

    assertEquals(
        List.of(
            new CharacterizedCaretStops(
                List.of(0, 1), List.of(0f, measuredAdvance("A"))),
            new CharacterizedCaretStops(
                List.of(1, 3), List.of(0f, measuredAdvance("\uD83D\uDE00"))),
            new CharacterizedCaretStops(List.of(4), List.of(0f))),
        metrics.lines().stream().map(this::characterizeCaretStops).toList());
  }

  @Test
  void currentPublicTextMeasurerAbstractSurface_remainsSourceCompatible() {
    Set<String> abstractMethods =
        Arrays.stream(TextMeasurer.class.getDeclaredMethods())
            .filter(method -> Modifier.isAbstract(method.getModifiers()))
            .map(this::methodSignature)
            .collect(Collectors.toSet());

    assertEquals(
        Set.of(
            "getTextCaretMetrics(String,Font,float,float)",
            "getTextLineMetrics(String,Font,float,float)",
            "getTextMetrics(String,float,Font,float,float,float,boolean)",
            "measureText(String,Font,float,float)",
            "measureText(String,float,Font,float,float,float,boolean)"),
        abstractMethods);
  }

  @Test
  void currentImmutableBoundaries_copyRunGlyphsAndRejectPublishedTopLevelMutation() {
    ResolvedGlyph glyph = new ResolvedGlyph(0, 1, 'a', 'a', Font.DEFAULT, false);
    List<ResolvedGlyph> sourceGlyphs = new ArrayList<>(List.of(glyph));
    ResolvedTextRun run = new ResolvedTextRun(0, 1, Font.DEFAULT, sourceGlyphs, 1);
    sourceGlyphs.clear();
    TextMetrics measured =
        fontService.measureText("a", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);

    assertAll(
        () -> assertEquals(List.of(glyph), run.glyphs()),
        () -> assertThrows(UnsupportedOperationException.class, () -> run.glyphs().clear()),
        () -> assertThrows(UnsupportedOperationException.class, () -> measured.lines().clear()));
  }

  @Test
  void rangeCapability_hasWholeStringParityAndAbsoluteNestedSourceTranslation() throws Exception {
    Class<?> capability = rangeCapability();
    Method measureRange = rangeMethod(capability);
    String source = "xxA\u96eazz";
    List<Font> fonts = List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR);
    TextMetrics expected =
        fontService.measureText(
            "A\u96ea", 2.5f, fonts, 13.25f, 1.17f,
            Float.POSITIVE_INFINITY,
            false);

    assertTrue(capability.isInstance(fontService));
    Object resolved =
        invokeRange(
            measureRange, fontService, source, 2, 4, 2.5f, fonts, 13.25f, 1.17f,
            Float.POSITIVE_INFINITY, false);
    TextMetrics actual = resolvedMetrics(resolved);

    assertTranslatedMeasurement(expected, actual, 2);
  }

  @Test
  void rangeCapability_validatesBoundsAndPreservesEmptyNumericAndFontChainContracts()
      throws Exception {
    Method measureRange = rangeMethod(rangeCapability());
    List<Font> fonts = List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR);

    assertRangeRejected(measureRange, fontService, "abc", -1, 2, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(measureRange, fontService, "abc", 2, 1, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(measureRange, fontService, "abc", 0, 4, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "x\uD83D\uDE00y", 2, 4, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "x\uD83D\uDE00y", 0, 2, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "a\r\nb", 2, 4, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "a\r\nb", 0, 2, 0, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, -1, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, Float.NaN, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, Float.POSITIVE_INFINITY, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, Float.NEGATIVE_INFINITY, fonts, FONT_SIZE, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, 0, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, -1, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, Float.NaN, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, Float.POSITIVE_INFINITY, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, Float.NEGATIVE_INFINITY, LINE_HEIGHT, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, -1, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, Float.NaN, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, Float.POSITIVE_INFINITY, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, Float.NEGATIVE_INFINITY, 10, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, LINE_HEIGHT, -1, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, LINE_HEIGHT, Float.NaN, false);
    assertRangeRejected(
        measureRange, fontService, "abc", 0, 3, 0, fonts, FONT_SIZE, LINE_HEIGHT, Float.NEGATIVE_INFINITY, false);

    Object emptyResolved =
        invokeRange(
            measureRange,
            fontService,
            "xxzz",
            2,
            2,
            0,
            List.of(),
            FONT_SIZE,
            LINE_HEIGHT,
            Float.POSITIVE_INFINITY,
            false);
    TextMetrics empty = resolvedMetrics(emptyResolved);
    TextMetrics defaultEmpty =
        fontService.measureText("", List.of(Font.DEFAULT), FONT_SIZE, LINE_HEIGHT);
    TextMetrics expectedZeroWidth =
        fontService.measureText("abc", 0, fonts, FONT_SIZE, LINE_HEIGHT, 0, true);
    TextMetrics actualZeroWidth =
        resolvedMetrics(
            invokeRange(
                measureRange,
                fontService,
                "xxabczz",
                2,
                5,
                0,
                fonts,
                FONT_SIZE,
                LINE_HEIGHT,
                0,
                true));
    TextMetrics expectedMinimumSizeAndZeroLineHeight =
        fontService.measureText(
            "A", 0, fonts, Float.MIN_NORMAL, 0, Float.POSITIVE_INFINITY, false);
    TextMetrics actualMinimumSizeAndZeroLineHeight =
        resolvedMetrics(
            invokeRange(
                measureRange,
                fontService,
                "xxAzz",
                2,
                3,
                0,
                fonts,
                Float.MIN_NORMAL,
                0,
                Float.POSITIVE_INFINITY,
                false));

    assertAll(
        () -> assertEquals(List.of(":2-2"), lineSignatures(empty)),
        () -> assertEquals(1, empty.lines().size()),
        () -> assertTrue(empty.lines().get(0).runs().isEmpty()));
    assertTranslatedMeasurement(defaultEmpty, empty, 2);
    assertTranslatedMeasurement(expectedZeroWidth, actualZeroWidth, 2);
    assertTranslatedMeasurement(
        expectedMinimumSizeAndZeroLineHeight, actualMinimumSizeAndZeroLineHeight, 2);
  }

  @Test
  void wholeStringDirectCapabilityAndAdapterBranchesHaveExactNestedParity() throws Exception {
    Class<?> capability = rangeCapability();
    Method direct = rangeMethod(capability);
    Method adapter = rangeAdapterMethod();
    String selected =
        "A\u96ea" + new String(Character.toChars(MISSING_CODE_POINT)) + "B\uD83D\uDE00";
    String source = "xx" + selected + "zz";
    int start = 2;
    int end = start + selected.length();
    float offsetX = 2.5f;
    List<Font> fonts =
        new ArrayList<>(
            List.of(
                MATERIAL_ICONS,
                Font.ROBOTO_REGULAR,
                Font.NOTO_SANS_CJK_SC_REGULAR,
                NOTO_EMOJI));
    TextMetrics wholeString =
        fontService.measureText(
            selected,
            offsetX,
            fonts,
            13.25f,
            1.17f,
            Float.POSITIVE_INFINITY,
            false);
    DiagnosticSession directDiagnostics = diagnostics();
    FontServiceImpl directService = instrumentedFontService(directDiagnostics);

    Object directResolved =
        invokeRange(
            direct,
            directService,
            source,
            start,
            end,
            offsetX,
            fonts,
            13.25f,
            1.17f,
            Float.POSITIVE_INFINITY, false);
    TextMetrics directMetrics = resolvedMetrics(directResolved);
    assertRangeWorkCounters(directDiagnostics.snapshot(), 5, 0);

    DiagnosticSession productionAdapterDiagnostics = diagnostics();
    FontServiceImpl productionAdapterService =
        instrumentedFontService(productionAdapterDiagnostics);
    TextMetrics productionAdapterMetrics =
        (TextMetrics)
            invokeRange(
                adapter,
                null,
                productionAdapterService,
                source,
                start,
                end,
                offsetX,
                fonts,
                13.25f,
                1.17f,
                Float.POSITIVE_INFINITY,
                false);
    assertRangeWorkCounters(productionAdapterDiagnostics.snapshot(), 5, 0);

    DiagnosticSession legacyDiagnostics = diagnostics();
    LegacyDelegatingTextMeasurer legacy =
        new LegacyDelegatingTextMeasurer(instrumentedFontService(legacyDiagnostics));
    assertFalse(capability.isInstance(legacy));
    TextMetrics legacyMetrics =
        (TextMetrics)
            invokeRange(
                adapter,
                null,
                legacy,
                source,
                start,
                end,
                offsetX,
                fonts,
                13.25f,
                1.17f,
                Float.POSITIVE_INFINITY,
                false);

    assertAll(
        () -> assertTranslatedMeasurement(wholeString, directMetrics, start),
        () -> assertTranslatedMeasurement(wholeString, productionAdapterMetrics, start),
        () -> assertTranslatedMeasurement(wholeString, legacyMetrics, start),
        () -> assertEquals(directMetrics, productionAdapterMetrics),
        () -> assertEquals(directMetrics, legacyMetrics));
    assertCaretStops(
        resolvedCaretStops(directResolved).get(0),
        List.of(2, 3, 4, 6, 7, 9),
        offsetX,
        directMetrics.lines().get(0));
    assertRangeWorkCounters(legacyDiagnostics.snapshot(), 5, 1, 1);

    int wholeStringHash = wholeString.hashCode();
    int directHash = directMetrics.hashCode();
    int productionAdapterHash = productionAdapterMetrics.hashCode();
    int legacyHash = legacyMetrics.hashCode();
    String wholeStringText = wholeString.toString();
    String directString = directMetrics.toString();
    String productionAdapterString = productionAdapterMetrics.toString();
    String legacyString = legacyMetrics.toString();
    assertDeeplyUnmodifiable(wholeString);
    assertDeeplyUnmodifiable(directMetrics);
    assertDeeplyUnmodifiable(productionAdapterMetrics);
    assertDeeplyUnmodifiable(legacyMetrics);
    assertEveryListMutationRejected(resolvedCaretStops(directResolved));
    fonts.clear();

    assertAll(
        () -> assertTranslatedMeasurement(wholeString, directMetrics, start),
        () -> assertTranslatedMeasurement(wholeString, productionAdapterMetrics, start),
        () -> assertTranslatedMeasurement(wholeString, legacyMetrics, start),
        () -> assertEquals(directMetrics, productionAdapterMetrics),
        () -> assertEquals(directMetrics, legacyMetrics),
        () -> assertEquals(wholeStringHash, wholeString.hashCode()),
        () -> assertEquals(directHash, directMetrics.hashCode()),
        () -> assertEquals(productionAdapterHash, productionAdapterMetrics.hashCode()),
        () -> assertEquals(legacyHash, legacyMetrics.hashCode()),
        () -> assertEquals(wholeStringText, wholeString.toString()),
        () -> assertEquals(directString, directMetrics.toString()),
        () -> assertEquals(productionAdapterString, productionAdapterMetrics.toString()),
        () -> assertEquals(legacyString, legacyMetrics.toString()));
  }

  @Test
  void finalLineCaretRepresentation_isRebasedPerLineAndLookupIsLogarithmicWithoutRemeasurement()
      throws Exception {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl service = instrumentedFontService(diagnostics);
    float textAdvance = service.measureText("AV", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();
    diagnostics.reset();
    Object resolved =
        invokeRange(
            rangeMethod(rangeCapability()),
            service,
            "xxAVAVzz",
            2,
            6,
            2,
            List.of(Font.DEFAULT),
            FONT_SIZE,
            LINE_HEIGHT,
            textAdvance + 2,
            false);
    TextMetrics metrics = resolvedMetrics(resolved);
    List<?> lineCaretStops = resolvedCaretStops(resolved);

    assertAll(
        () -> assertEquals(metrics.lines().size(), lineCaretStops.size()),
        () -> assertEquals(2, lineCaretStops.size()),
        () ->
            assertFalse(
                Arrays.stream(resolved.getClass().getDeclaredFields())
                    .anyMatch(field -> field.getType().isArray()),
                "resolved measurement must not retain a source-global caret array"));
    assertCaretStops(lineCaretStops.get(0), List.of(2, 3, 4), 2, metrics.lines().get(0));
    assertCaretStops(lineCaretStops.get(1), List.of(4, 5, 6), 0, metrics.lines().get(1));
    assertEquals(
        caretStopAdvance(lineCaretStops.get(0), 2), caretStopAdvance(lineCaretStops.get(1), 2));
    assertEquals(
        "com.spinyowl.spinygui.core.system.font.internal.FinalLineCaretStops",
        lineCaretStops.get(0).getClass().getName());
    assertFalse(
        Arrays.stream(lineCaretStops.get(0).getClass().getDeclaredMethods())
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .anyMatch(method -> method.getReturnType().isArray()),
        "internal mutable arrays must not escape");

    Object secondLineStops = lineCaretStops.get(1);
    DiagnosticSession disabled = DiagnosticSession.disabled();
    float secondLineEndAdvance = caretStopAdvance(secondLineStops, 2);
    FinalLineCaretStops leadingZeroStops =
        new FinalLineCaretStops(
            new int[] {10, 11, 12, 13}, new float[] {0, 0, 0, 5});
    assertAll(
        () -> assertEquals(4, caretAt(secondLineStops, -1, disabled).charIndex()),
        () -> assertEquals(4, caretAt(secondLineStops, 0, disabled).charIndex()),
        () ->
            assertEquals(
                6, caretAt(secondLineStops, secondLineEndAdvance, disabled).charIndex()),
        () ->
            assertEquals(
                6, caretAt(secondLineStops, secondLineEndAdvance + 1, disabled).charIndex()),
        () -> assertEquals(10, caretAt(leadingZeroStops, -1, disabled).charIndex()),
        () -> assertEquals(12, caretAt(leadingZeroStops, 0, disabled).charIndex()),
        () -> assertEquals(13, caretAt(leadingZeroStops, 5, disabled).charIndex()),
        () -> assertEquals(13, caretAt(leadingZeroStops, 6, disabled).charIndex()));

    diagnostics.reset();
    float midpoint =
        (caretStopAdvance(secondLineStops, 0) + caretStopAdvance(secondLineStops, 1)) / 2f;
    TextCaretMetrics tie = caretAt(secondLineStops, midpoint, diagnostics);
    DiagnosticSnapshot lookup = diagnostics.snapshot();
    long comparisons =
        lookup.values().getOrDefault("core.text.caret-stop-search-comparisons", 0L);

    assertAll(
        () -> assertEquals(5, tie.charIndex()),
        () -> assertEquals(0, lookup.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(0, lookup.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () -> assertEquals(0, lookup.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () -> assertTrue(lookup.values().containsKey("core.text.caret-stop-search-comparisons")),
        () -> assertTrue(comparisons > 0),
        () -> assertTrue(comparisons <= 3, "two-glyph line lookup must remain logarithmic"));

    FinalLineCaretStops sourceStops =
        new FinalLineCaretStops(
            new int[] {10, 11, 13, 14}, new float[] {0, 2, 5, 7});
    diagnostics.reset();
    TextCaretMetrics sourceInterior = caretAtSourceIndex(sourceStops, 12, diagnostics);
    DiagnosticSnapshot sourceLookup = diagnostics.snapshot();
    long sourceComparisons =
        sourceLookup.values().getOrDefault("core.text.caret-stop-search-comparisons", 0L);
    assertAll(
        () -> assertEquals(10, caretAtSourceIndex(sourceStops, 9, disabled).charIndex()),
        () -> assertEquals(10, caretAtSourceIndex(sourceStops, 10, disabled).charIndex()),
        () -> assertEquals(11, sourceInterior.charIndex()),
        () -> assertEquals(2, sourceInterior.x(), EPSILON),
        () -> assertEquals(13, caretAtSourceIndex(sourceStops, 13, disabled).charIndex()),
        () -> assertEquals(14, caretAtSourceIndex(sourceStops, 15, disabled).charIndex()),
        () -> assertEquals(0, sourceLookup.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(0, sourceLookup.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () -> assertEquals(0, sourceLookup.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () -> assertTrue(sourceComparisons > 0),
        () -> assertTrue(sourceComparisons <= 2));

    String longText = "a".repeat(1024);
    diagnostics.reset();
    Object longResolved =
        invokeRange(
            rangeMethod(rangeCapability()),
            service,
            longText,
            0,
            longText.length(),
            0,
            List.of(Font.DEFAULT),
            FONT_SIZE,
            LINE_HEIGHT,
            Float.POSITIVE_INFINITY,
            false);
    Object longStops = resolvedCaretStops(longResolved).get(0);
    diagnostics.reset();
    caretAt(longStops, caretStopAdvance(longStops, 1024) / 2f, diagnostics);
    DiagnosticSnapshot longLookup = diagnostics.snapshot();
    long longComparisons =
        longLookup.values().getOrDefault("core.text.caret-stop-search-comparisons", 0L);
    assertAll(
        () -> assertEquals(0, longLookup.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(0, longLookup.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () -> assertEquals(0, longLookup.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () ->
            assertTrue(
                longLookup.values().containsKey("core.text.caret-stop-search-comparisons")),
        () -> assertTrue(longComparisons > 0),
        () ->
            assertTrue(
                longComparisons <= 13,
                "1025 caret stops require logarithmic rather than linear lookup"));

    diagnostics.reset();
    TextCaretMetrics sourceCaret = caretAtSourceIndex(longStops, 777, diagnostics);
    DiagnosticSnapshot longSourceLookup = diagnostics.snapshot();
    long longSourceComparisons =
        longSourceLookup
            .values()
            .getOrDefault("core.text.caret-stop-search-comparisons", 0L);
    assertAll(
        () -> assertEquals(777, sourceCaret.charIndex()),
        () -> assertEquals(caretStopAdvance(longStops, 777), sourceCaret.x(), EPSILON),
        () ->
            assertEquals(
                0, longSourceLookup.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () ->
            assertEquals(
                0, longSourceLookup.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () ->
            assertEquals(
                0, longSourceLookup.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS)),
        () -> assertTrue(longSourceComparisons > 0),
        () ->
            assertTrue(
                longSourceComparisons <= 11,
                "1025 source boundaries require logarithmic rather than linear lookup"));
  }

  @Test
  void textLineMetrics_constructorAndBuilderAreDeepCanonicalSnapshots() {
    FontMetrics fontMetrics = new FontMetrics(1, 1, 0, 2, 1);
    ResolvedGlyph glyph = new ResolvedGlyph(0, 1, 'a', 'a', Font.DEFAULT, false);
    List<ResolvedGlyph> sourceGlyphs = new ArrayList<>(List.of(glyph));
    ResolvedTextRun run = new ResolvedTextRun(0, 1, Font.DEFAULT, sourceGlyphs, 1);
    StringBuilder constructorCharacters = new StringBuilder("a");
    List<ResolvedTextRun> constructorRuns = new ArrayList<>(List.of(run));
    TextLineMetrics fromConstructor =
        new TextLineMetrics(constructorCharacters, 0, 1, 1, 1, 2, 1, fontMetrics, constructorRuns);
    StringBuilder builderCharacters = new StringBuilder("a");
    List<ResolvedTextRun> builderRuns = new ArrayList<>(List.of(run));
    TextLineMetrics.TextLineMetricsBuilder lineBuilder =
        TextLineMetrics.builder()
            .characters(builderCharacters)
            .startIndex(0)
            .endIndex(1)
            .charCount(1)
            .width(1)
            .height(2)
            .baseline(1)
            .fontMetrics(fontMetrics)
            .runs(builderRuns);
    TextLineMetrics fromBuilder = lineBuilder.build();
    int runHash = run.hashCode();
    String runString = run.toString();
    int constructorHash = fromConstructor.hashCode();
    int builderHash = fromBuilder.hashCode();
    String constructorString = fromConstructor.toString();
    String builderString = fromBuilder.toString();

    sourceGlyphs.clear();
    constructorCharacters.append('b');
    constructorRuns.clear();
    builderCharacters.append('b');
    builderRuns.clear();
    lineBuilder.characters("different").runs(List.of()).build();
    assertEveryListMutationRejected(run.glyphs());
    assertEveryListMutationRejected(fromConstructor.runs());
    assertEveryListMutationRejected(fromBuilder.runs());

    assertAll(
        () -> assertTrue(fromConstructor.characters() instanceof String),
        () -> assertTrue(fromBuilder.characters() instanceof String),
        () -> assertEquals("a", fromConstructor.characters().toString()),
        () -> assertEquals("a", fromBuilder.characters().toString()),
        () -> assertEquals(List.of(run), fromConstructor.runs()),
        () -> assertEquals(List.of(run), fromBuilder.runs()),
        () -> assertEquals(runHash, run.hashCode()),
        () -> assertEquals(runString, run.toString()),
        () -> assertEquals(fromConstructor, fromBuilder),
        () -> assertEquals(constructorHash, fromConstructor.hashCode()),
        () -> assertEquals(builderHash, fromBuilder.hashCode()),
        () -> assertEquals(constructorHash, builderHash),
        () -> assertEquals(constructorString, fromConstructor.toString()),
        () -> assertEquals(builderString, fromBuilder.toString()),
        () -> assertEquals(constructorString, builderString));
  }

  @Test
  void textMetrics_constructorAndBuilderAreDeepCanonicalSnapshots() {
    FontMetrics fontMetrics = new FontMetrics(1, 1, 0, 2, 1);
    TextLineMetrics line =
        TextLineMetrics.builder()
            .characters("a")
            .startIndex(0)
            .endIndex(1)
            .charCount(1)
            .fontMetrics(fontMetrics)
            .build();
    List<TextLineMetrics> constructorLines = new ArrayList<>(List.of(line));
    TextMetrics fromConstructor = new TextMetrics(constructorLines, 1, 2, 2, fontMetrics);
    List<TextLineMetrics> builderLines = new ArrayList<>(List.of(line));
    TextMetrics fromBuilder =
        TextMetrics.builder()
            .lines(builderLines)
            .width(1)
            .height(2)
            .lineHeight(2)
            .fontMetrics(fontMetrics)
            .build();
    TextMetrics.TextMetricsBuilder singularBuilder =
        TextMetrics.builder()
            .line(line)
            .width(1)
            .height(2)
            .lineHeight(2)
            .fontMetrics(fontMetrics);
    TextMetrics fromSingularBuilder = singularBuilder.build();
    int constructorHash = fromConstructor.hashCode();
    int builderHash = fromBuilder.hashCode();
    int singularBuilderHash = fromSingularBuilder.hashCode();
    String constructorString = fromConstructor.toString();
    String builderString = fromBuilder.toString();
    String singularBuilderString = fromSingularBuilder.toString();
    constructorLines.clear();
    builderLines.clear();
    singularBuilder.clearLines().build();
    assertEveryListMutationRejected(fromConstructor.lines());
    assertEveryListMutationRejected(fromBuilder.lines());
    assertEveryListMutationRejected(fromSingularBuilder.lines());

    assertAll(
        () -> assertEquals(List.of(line), fromConstructor.lines()),
        () -> assertEquals(List.of(line), fromBuilder.lines()),
        () -> assertEquals(List.of(line), fromSingularBuilder.lines()),
        () -> assertEquals(fromConstructor, fromBuilder),
        () -> assertEquals(fromConstructor, fromSingularBuilder),
        () -> assertEquals(constructorHash, fromConstructor.hashCode()),
        () -> assertEquals(builderHash, fromBuilder.hashCode()),
        () -> assertEquals(singularBuilderHash, fromSingularBuilder.hashCode()),
        () -> assertEquals(constructorHash, builderHash),
        () -> assertEquals(constructorHash, singularBuilderHash),
        () -> assertEquals(constructorString, fromConstructor.toString()),
        () -> assertEquals(builderString, fromBuilder.toString()),
        () -> assertEquals(singularBuilderString, fromSingularBuilder.toString()),
        () -> assertEquals(constructorString, builderString),
        () -> assertEquals(constructorString, singularBuilderString));
  }

  @Test
  void internalResultConstructorsAndPrivatePreparationDoNotExposeMutableStorage() {
    int[] sourceBoundaries = {0, 1};
    float[] advances = {0, 2};
    FinalLineCaretStops stops = new FinalLineCaretStops(sourceBoundaries, advances);
    TextMetrics metrics = fontService.measureText("A", Font.DEFAULT, FONT_SIZE, LINE_HEIGHT);
    List<FinalLineCaretStops> sourceStops = new ArrayList<>(List.of(stops));
    ResolvedMeasurement resolved = new ResolvedMeasurement(metrics, sourceStops);
    int resolvedHash = resolved.hashCode();
    String resolvedString = resolved.toString();

    sourceBoundaries[1] = 99;
    advances[1] = 99;
    sourceStops.clear();
    assertEveryListMutationRejected(resolved.lineCaretStops());

    List<Font> sourceFonts = new ArrayList<>(List.of(Font.DEFAULT));
    FontServiceImpl.PrivatePreparedMeasurement prepared =
        fontService.preparePrivateMeasurement(
            "AV", 0, 2, 0, sourceFonts, FONT_SIZE, LINE_HEIGHT, Float.POSITIVE_INFINITY, false);
    sourceFonts.clear();
    assertEveryListMutationRejected(prepared.request().fonts());
    assertEveryListMutationRejected(prepared.sequence().primitives());
    assertEveryListMutationRejected(prepared.sequence().runRanges());
    assertEveryListMutationRejected(prepared.lines());
    for (FontServiceImpl.PrivatePreWrapLine line : prepared.lines()) {
      assertEveryListMutationRejected(line.caretBoundaries());
      assertEveryListMutationRejected(line.rawAdvanceSlots());
      assertEveryListMutationRejected(line.rebasedAdvanceSlots());
    }

    assertAll(
        () -> assertEquals(1, stops.sourceBoundary(1)),
        () -> assertEquals(2, stops.advance(1), EPSILON),
        () -> assertEquals(List.of(stops), resolved.lineCaretStops()),
        () -> assertEquals(resolvedHash, resolved.hashCode()),
        () -> assertEquals(resolvedString, resolved.toString()),
        () -> assertEquals(List.of(Font.DEFAULT), prepared.request().fonts()));
  }

  private void assertDeeplyUnmodifiable(TextMetrics metrics) {
    assertEveryListMutationRejected(metrics.lines());
    for (TextLineMetrics line : metrics.lines()) {
      assertTrue(line.characters() instanceof String);
      assertEveryListMutationRejected(line.runs());
      for (ResolvedTextRun run : line.runs()) {
        assertEveryListMutationRejected(run.glyphs());
      }
    }
  }

  private <T> void assertEveryListMutationRejected(List<T> values) {
    assertFalse(values.isEmpty(), "immutability fixture requires a non-empty collection");
    T existing = values.get(0);
    assertAll(
        () -> assertThrows(UnsupportedOperationException.class, () -> values.add(existing)),
        () -> assertThrows(UnsupportedOperationException.class, () -> values.add(0, existing)),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.addAll(List.of(existing))),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.addAll(0, List.of(existing))),
        () -> assertThrows(UnsupportedOperationException.class, () -> values.set(0, existing)),
        () -> assertThrows(UnsupportedOperationException.class, () -> values.remove(0)),
        () -> assertThrows(UnsupportedOperationException.class, () -> values.remove(existing)),
        () -> assertThrows(UnsupportedOperationException.class, values::clear),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.removeAll(List.of(existing))),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.retainAll(List.of())),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.removeIf(ignored -> true)),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.replaceAll(value -> value)),
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.sort((left, right) -> 0)),
        () -> {
          Iterator<T> iterator = values.iterator();
          iterator.next();
          assertThrows(UnsupportedOperationException.class, iterator::remove);
        },
        () -> {
          ListIterator<T> iterator = values.listIterator();
          assertThrows(UnsupportedOperationException.class, () -> iterator.add(existing));
        },
        () -> {
          ListIterator<T> iterator = values.listIterator();
          iterator.next();
          assertThrows(UnsupportedOperationException.class, () -> iterator.set(existing));
        },
        () -> {
          ListIterator<T> iterator = values.listIterator();
          iterator.next();
          assertThrows(UnsupportedOperationException.class, iterator::remove);
        },
        () ->
            assertThrows(
                UnsupportedOperationException.class, () -> values.subList(0, 1).clear()));
  }

  private void assertInvalid(float offsetX, float maxWidth, float fontSize, float lineHeight) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fontService.measureText(
                "abc", offsetX, Font.DEFAULT, fontSize, lineHeight, maxWidth, false));
  }

  private Class<?> rangeCapability() throws ClassNotFoundException {
    return Class.forName(
        "com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerCapability");
  }

  private Method rangeMethod(Class<?> capability) throws NoSuchMethodException {
    return capability.getMethod(
        "measureRange",
        String.class,
        int.class,
        int.class,
        float.class,
        List.class,
        float.class,
        float.class,
        float.class,
        boolean.class);
  }

  private Method rangeAdapterMethod() throws ReflectiveOperationException {
    return Class.forName(
            "com.spinyowl.spinygui.core.system.font.internal.RangeTextMeasurerAdapter")
        .getMethod(
            "measureRange",
            TextMeasurer.class,
            String.class,
            int.class,
            int.class,
            float.class,
            List.class,
            float.class,
            float.class,
            float.class,
            boolean.class);
  }

  private Object invokeRange(Method method, Object receiver, Object... arguments)
      throws ReflectiveOperationException {
    return method.invoke(receiver, arguments);
  }

  private TextMetrics resolvedMetrics(Object resolved) throws ReflectiveOperationException {
    return (TextMetrics) resolved.getClass().getMethod("metrics").invoke(resolved);
  }

  private List<?> resolvedCaretStops(Object resolved) throws ReflectiveOperationException {
    return (List<?>) resolved.getClass().getMethod("lineCaretStops").invoke(resolved);
  }

  private void assertRangeRejected(
      Method method,
      Object receiver,
      String source,
      int start,
      int end,
      float offsetX,
      List<Font> fonts,
      float fontSize,
      float lineHeight,
      float maxWidth,
      boolean wordWrap) {
    InvocationTargetException thrown =
        assertThrows(
            InvocationTargetException.class,
            () ->
                invokeRange(
                    method,
                    receiver,
                    source,
                    start,
                    end,
                    offsetX,
                    fonts,
                    fontSize,
                    lineHeight,
                    maxWidth,
                    wordWrap));
    assertTrue(thrown.getCause() instanceof IllegalArgumentException);
  }

  private void assertTranslatedMeasurement(TextMetrics expected, TextMetrics actual, int origin) {
    assertAll(
        () -> assertEquals(expected.width(), actual.width(), EPSILON),
        () -> assertEquals(expected.height(), actual.height(), EPSILON),
        () -> assertEquals(expected.lineHeight(), actual.lineHeight(), EPSILON),
        () -> assertEquals(expected.fontMetrics(), actual.fontMetrics()),
        () -> assertEquals(expected.lines().size(), actual.lines().size()));
    for (int lineIndex = 0; lineIndex < expected.lines().size(); lineIndex++) {
      TextLineMetrics expectedLine = expected.lines().get(lineIndex);
      TextLineMetrics actualLine = actual.lines().get(lineIndex);
      assertAll(
          () -> assertEquals(expectedLine.characters().toString(), actualLine.characters().toString()),
          () -> assertEquals(expectedLine.startIndex() + origin, actualLine.startIndex()),
          () -> assertEquals(expectedLine.endIndex() + origin, actualLine.endIndex()),
          () -> assertEquals(expectedLine.charCount(), actualLine.charCount()),
          () -> assertEquals(expectedLine.width(), actualLine.width(), EPSILON),
          () -> assertEquals(expectedLine.height(), actualLine.height(), EPSILON),
          () -> assertEquals(expectedLine.baseline(), actualLine.baseline(), EPSILON),
          () -> assertEquals(expectedLine.fontMetrics(), actualLine.fontMetrics()),
          () -> assertEquals(expectedLine.runs().size(), actualLine.runs().size()));
      for (int runIndex = 0; runIndex < expectedLine.runs().size(); runIndex++) {
        ResolvedTextRun expectedRun = expectedLine.runs().get(runIndex);
        ResolvedTextRun actualRun = actualLine.runs().get(runIndex);
        assertAll(
            () -> assertEquals(expectedRun.sourceStart() + origin, actualRun.sourceStart()),
            () -> assertEquals(expectedRun.sourceEnd() + origin, actualRun.sourceEnd()),
            () -> assertEquals(expectedRun.font(), actualRun.font()),
            () -> assertEquals(expectedRun.advance(), actualRun.advance(), EPSILON),
            () -> assertEquals(expectedRun.glyphs().size(), actualRun.glyphs().size()));
        for (int glyphIndex = 0; glyphIndex < expectedRun.glyphs().size(); glyphIndex++) {
          ResolvedGlyph expectedGlyph = expectedRun.glyphs().get(glyphIndex);
          ResolvedGlyph actualGlyph = actualRun.glyphs().get(glyphIndex);
          assertAll(
              () -> assertEquals(expectedGlyph.sourceStart() + origin, actualGlyph.sourceStart()),
              () -> assertEquals(expectedGlyph.sourceEnd() + origin, actualGlyph.sourceEnd()),
              () -> assertEquals(expectedGlyph.sourceCodePoint(), actualGlyph.sourceCodePoint()),
              () -> assertEquals(expectedGlyph.renderedCodePoint(), actualGlyph.renderedCodePoint()),
              () -> assertEquals(expectedGlyph.font(), actualGlyph.font()),
              () -> assertEquals(expectedGlyph.replacement(), actualGlyph.replacement()));
        }
      }
    }
  }

  private DiagnosticSession diagnostics() {
    return DiagnosticSession.enabled(Arrays.asList(TextDiagnosticCounter.values()));
  }

  private FontServiceImpl instrumentedFontService(DiagnosticSession diagnostics) {
    FontServiceImpl service =
        new FontServiceImpl(new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    service.installSemanticOwner();
    service.loadFont(MATERIAL_ICONS.path());
    service.loadFont(NOTO_EMOJI.path());
    return service;
  }

  private void assertRangeWorkCounters(
      DiagnosticSnapshot snapshot, long codePointCount, long publicListEntries) {
    assertRangeWorkCounters(snapshot, codePointCount, publicListEntries, 0);
  }

  private void assertRangeWorkCounters(
      DiagnosticSnapshot snapshot,
      long codePointCount,
      long publicListEntries,
      long temporaryStrings) {
    long publicApiEntries =
        Arrays.stream(TextDiagnosticCounter.values())
            .filter(counter -> counter.name().startsWith("TEXT_MEASURER_"))
            .mapToLong(snapshot::value)
            .sum();
    assertAll(
        () -> assertEquals(1, snapshot.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS)),
        () ->
            assertEquals(
                publicListEntries,
                snapshot.value(
                    TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_LIST_FULL_ENTRIES)),
        () -> assertEquals(publicListEntries, publicApiEntries),
        () ->
            assertEquals(
                0,
                snapshot.value(TextDiagnosticCounter.TEXT_MEASURER_MEASURE_TEXT_FONT_FULL_ENTRIES)),
        () -> assertEquals(1, snapshot.value(TextDiagnosticCounter.RANGE_PREPARATIONS)),
        () ->
            assertEquals(
                temporaryStrings, snapshot.value(TextDiagnosticCounter.RANGE_TEMPORARY_STRINGS)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(
                    TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED)));
  }

  private void assertSingleMeasurementWork(
      DiagnosticSnapshot snapshot, long codePointCount, long publicApiEntries) {
    long observedApiEntries =
        Arrays.stream(TextDiagnosticCounter.values())
            .filter(counter -> counter.name().startsWith("TEXT_MEASURER_"))
            .mapToLong(snapshot::value)
            .sum();
    assertAll(
        () -> assertEquals(1, snapshot.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS)),
        () -> assertEquals(1, snapshot.value(TextDiagnosticCounter.RANGE_PREPARATIONS)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS)),
        () ->
            assertEquals(
                codePointCount,
                snapshot.value(
                    TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED)),
        () -> assertEquals(publicApiEntries, observedApiEntries));
  }

  private void assertExactApiEntries(
      DiagnosticSnapshot snapshot, Map<TextDiagnosticCounter, Long> expectedEntries) {
    for (TextDiagnosticCounter counter : TextDiagnosticCounter.values()) {
      if (counter.name().startsWith("TEXT_MEASURER_")) {
        assertEquals(expectedEntries.getOrDefault(counter, 0L), snapshot.value(counter), counter.id());
      }
    }
  }

  private void assertCaretStops(
      Object stops, List<Integer> expectedBoundaries, float initialOffset, TextLineMetrics line)
      throws ReflectiveOperationException {
    int size = (int) stops.getClass().getMethod("size").invoke(stops);
    assertEquals(expectedBoundaries.size(), size);
    float previousAdvance = Float.NEGATIVE_INFINITY;
    for (int index = 0; index < size; index++) {
      assertEquals(expectedBoundaries.get(index), caretStopBoundary(stops, index));
      float advance = caretStopAdvance(stops, index);
      assertTrue(Float.isFinite(advance));
      assertTrue(advance >= previousAdvance);
      previousAdvance = advance;
    }
    assertEquals(0, caretStopAdvance(stops, 0), EPSILON);
    assertEquals(line.width() - initialOffset, caretStopAdvance(stops, size - 1), EPSILON);
  }

  private int caretStopBoundary(Object stops, int index) throws ReflectiveOperationException {
    return (int) stops.getClass().getMethod("sourceBoundary", int.class).invoke(stops, index);
  }

  private float caretStopAdvance(Object stops, int index) throws ReflectiveOperationException {
    return (float) stops.getClass().getMethod("advance", int.class).invoke(stops, index);
  }

  private TextCaretMetrics caretAt(
      Object stops, float offsetX, DiagnosticSession diagnostics)
      throws ReflectiveOperationException {
    return (TextCaretMetrics)
        stops
            .getClass()
            .getMethod("caretAt", float.class, DiagnosticSession.class)
            .invoke(stops, offsetX, diagnostics);
  }

  private TextCaretMetrics caretAtSourceIndex(
      Object stops, int sourceIndex, DiagnosticSession diagnostics)
      throws ReflectiveOperationException {
    return (TextCaretMetrics)
        stops
            .getClass()
            .getMethod("caretAtSourceIndex", int.class, DiagnosticSession.class)
            .invoke(stops, sourceIndex, diagnostics);
  }

  private CharacterizedCaretStops characterizeCaretStops(TextLineMetrics line) {
    String characters = line.characters().toString();
    List<Integer> boundaries = new ArrayList<>();
    List<Float> advances = new ArrayList<>();
    boundaries.add(line.startIndex());
    advances.add(0f);
    for (int localEnd = 0; localEnd < characters.length(); ) {
      localEnd += Character.charCount(characters.codePointAt(localEnd));
      boundaries.add(line.startIndex() + localEnd);
      advances.add(measuredAdvance(characters.substring(0, localEnd)));
    }
    return new CharacterizedCaretStops(List.copyOf(boundaries), List.copyOf(advances));
  }

  private float measuredAdvance(String text) {
    return fontService.measureText(text, Font.DEFAULT, FONT_SIZE, LINE_HEIGHT).width();
  }

  private String methodSignature(Method method) {
    return method.getName()
        + "("
        + Arrays.stream(method.getParameterTypes())
            .map(Class::getSimpleName)
            .collect(Collectors.joining(","))
        + ")";
  }

  private int glyphIndex(Font font, int codePoint) {
    ByteBuffer data = IOUtil.resourceAsByteBuffer(font.path());
    try (MemoryStack stack = MemoryStack.stackPush()) {
      STBTTFontinfo fontInfo = STBTTFontinfo.malloc(stack);
      assertTrue(stbtt_InitFont(fontInfo, data));
      return stbtt_FindGlyphIndex(fontInfo, codePoint);
    }
  }

  private ResolvedTextRun onlyRun(TextMetrics metrics) {
    List<ResolvedTextRun> runs = metrics.lines().get(0).runs();
    assertEquals(1, runs.size());
    return runs.get(0);
  }

  private ResolvedGlyph onlyGlyph(ResolvedTextRun run) {
    assertEquals(1, run.glyphs().size());
    return run.glyphs().get(0);
  }

  private List<String> lineCharacters(TextMetrics metrics) {
    return metrics.lines().stream().map(TextLineMetrics::toString).toList();
  }

  private List<String> lineSignatures(TextMetrics metrics) {
    return metrics.lines().stream()
        .map(line -> "%s:%d-%d".formatted(line, line.startIndex(), line.endIndex()))
        .toList();
  }

  /** Exercises the four inherited list defaults through the unchanged five-method abstract seam. */
  private static final class DefaultOnlyDelegatingTextMeasurer implements TextMeasurer {
    private final FontServiceImpl delegate;

    private DefaultOnlyDelegatingTextMeasurer(FontServiceImpl delegate) {
      this.delegate = delegate;
    }

    @Override
    public DiagnosticSession diagnostics() {
      return delegate.diagnostics();
    }

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      return delegate.measureText(text, font, fontSize, lineHeight);
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
      return delegate.measureText(
          text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
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
      return delegate.getTextMetrics(
          text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return delegate.getTextLineMetrics(text, font, fontSize, lineHeight);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      return delegate.getTextCaretMetrics(text, font, fontSize, offsetX);
    }
  }

  private static final class LegacyDelegatingTextMeasurer implements TextMeasurer {
    private final FontServiceImpl delegate;

    private LegacyDelegatingTextMeasurer(FontServiceImpl delegate) {
      this.delegate = delegate;
    }

    @Override
    public DiagnosticSession diagnostics() {
      return delegate.diagnostics();
    }

    @Override
    public TextMetrics measureText(
        String text, List<Font> fonts, float fontSize, float lineHeight) {
      return delegate.measureText(text, fonts, fontSize, lineHeight);
    }

    @Override
    public TextMetrics measureText(
        String text,
        float offsetX,
        List<Font> fonts,
        float fontSize,
        float lineHeight,
        float maxWidth,
        boolean wordWrap) {
      return delegate.measureText(
          text, offsetX, fonts, fontSize, lineHeight, maxWidth, wordWrap);
    }

    @Override
    public TextMetrics measureText(String text, Font font, float fontSize, float lineHeight) {
      return delegate.measureText(text, font, fontSize, lineHeight);
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
      return delegate.measureText(
          text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
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
      return delegate.getTextMetrics(
          text, offsetX, font, fontSize, lineHeight, maxWidth, wordWrap);
    }

    @Override
    public TextLineMetrics getTextLineMetrics(
        String text, Font font, float fontSize, float lineHeight) {
      return delegate.getTextLineMetrics(text, font, fontSize, lineHeight);
    }

    @Override
    public TextCaretMetrics getTextCaretMetrics(
        String text, Font font, float fontSize, float offsetX) {
      return delegate.getTextCaretMetrics(text, font, fontSize, offsetX);
    }
  }

  private record CharacterizedCaretStops(List<Integer> sourceBoundaries, List<Float> advances) {}
}
