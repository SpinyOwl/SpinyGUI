package com.spinyowl.spinygui.core.system.font.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.spinyowl.spinygui.core.system.font.internal.PreparedRange;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FontServiceImplTest {
  private FontServiceImpl fontService;

  @BeforeEach
  void setUp() {
    fontService = new FontServiceImpl(new FontStorageImpl(), false);
    fontService.installSemanticOwner();
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
  void measureText_whenMaxWidthIsZero_placesOneCodePointPerLine() {
    TextMetrics metrics = fontService.measureText("abc", 0, Font.DEFAULT, 16, 1.2f, 0, true);

    assertEquals(List.of("a", "b", "c"), metrics.lines().stream().map(Object::toString).toList());
    assertTrue(metrics.lines().stream().allMatch(line -> line.width() > 0));
    assertEquals(metrics.lineHeight() * 3, metrics.height());
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
  void diagnostics_exposeSingleResolutionAndOneFinalMaterializationWithoutChangingOutput() {
    String text = "aaaa";
    TextMetrics expected = fontService.measureText(text, Font.DEFAULT, 16, 1.2f);
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    TextMetrics actual = instrumented.measureText(text, Font.DEFAULT, 16, 1.2f);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(expected, actual);
    assertEquals(4, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES));
    assertEquals(8, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(8, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        4,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
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
  void resolvedPrimitives_resolveEachGlyphOnceAndCountEveryFallbackProbe() {
    String missing = new String(Character.toChars(0x10FFFF));
    String source = "A\u96ea" + missing;
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    FontServiceImpl.ResolvedPrimitiveSequence sequence =
        instrumented.resolvePrimitives(
            source,
            0,
            source.length(),
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            16);
    List<FontServiceImpl.ResolvedPrimitive> primitives = sequence.primitives();
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(3, primitives.size());
    assertPrimitive(primitives.get(0), 0, 1, 'A', 'A', Font.ROBOTO_REGULAR, false);
    assertPrimitive(
        primitives.get(1),
        1,
        2,
        '\u96ea',
        '\u96ea',
        Font.NOTO_SANS_CJK_SC_REGULAR,
        false);
    assertPrimitive(
        primitives.get(2),
        2,
        4,
        0x10FFFF,
        0xFFFD,
        Font.ROBOTO_REGULAR,
        true);
    assertEquals(3, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_INDEX_PROBES));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS));

    FontServiceImpl.ResolvedPrimitive defaultPrimitive =
        fontService.resolvePrimitives("A", 0, 1, List.of(), 16).primitives().get(0);
    assertPrimitive(defaultPrimitive, 0, 1, 'A', 'A', Font.DEFAULT, false);
  }

  @Test
  void resolvedPrimitives_preserveCodePointAndAtomicCrlfEvidenceWithoutLayoutState() {
    String source = "A\uD83D\uDE00\r\nB";
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();
    FontServiceImpl.ResolvedPrimitiveSequence sequence =
        instrumented.resolvePrimitives(source, 0, source.length(), List.of(Font.DEFAULT), 16);
    List<FontServiceImpl.ResolvedPrimitive> primitives = sequence.primitives();
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(
        List.of(0, 1, 3, 4, 5),
        primitives.stream().map(FontServiceImpl.ResolvedPrimitive::sourceStart).toList());
    assertEquals(
        List.of(1, 3, 4, 5, 6),
        primitives.stream().map(FontServiceImpl.ResolvedPrimitive::sourceEnd).toList());
    assertEquals(
        List.of(
            FontServiceImpl.SeparatorKind.NONE,
            FontServiceImpl.SeparatorKind.NONE,
            FontServiceImpl.SeparatorKind.CRLF_START,
            FontServiceImpl.SeparatorKind.CRLF_END,
            FontServiceImpl.SeparatorKind.NONE),
        primitives.stream().map(FontServiceImpl.ResolvedPrimitive::separatorKind).toList());
    assertFalse(primitives.get(1).separator());
    assertTrue(primitives.get(2).separator());
    assertTrue(primitives.get(3).separator());
    assertNull(primitives.get(2).font());
    assertNull(primitives.get(2).fontInfo());
    assertNotNull(primitives.get(4).fontInfo());
    assertEquals(-1, primitives.get(4).previousGlyphIndex());
    assertEquals(5, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(3, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS));

    Set<String> primitiveFields =
        Arrays.stream(FontServiceImpl.ResolvedPrimitive.class.getRecordComponents())
            .map(RecordComponent::getName)
            .collect(Collectors.toSet());
    assertFalse(primitiveFields.stream().anyMatch(name -> name.contains("maxWidth")));
    assertFalse(primitiveFields.stream().anyMatch(name -> name.contains("offset")));
    assertFalse(primitiveFields.stream().anyMatch(name -> name.contains("final")));
    assertFalse(
        Arrays.stream(FontServiceImpl.ResolvedPrimitiveSequence.class.getDeclaredFields())
            .anyMatch(field -> field.getType().isArray()));
  }

  @Test
  void resolvedPrimitives_validateAtomicStartEndAndEmptyRangeBoundaries() {
    String source = "x\uD83D\uDE00\r\ny";

    FontServiceImpl.ResolvedPrimitiveSequence full =
        fontService.resolvePrimitives(
            source, 0, source.length(), List.of(Font.DEFAULT), 16);
    FontServiceImpl.ResolvedPrimitiveSequence beforeSupplementary =
        fontService.resolvePrimitives(source, 1, 1, List.of(Font.DEFAULT), 16);
    FontServiceImpl.ResolvedPrimitiveSequence afterSupplementary =
        fontService.resolvePrimitives(source, 3, 3, List.of(Font.DEFAULT), 16);
    FontServiceImpl.ResolvedPrimitiveSequence afterCrlf =
        fontService.resolvePrimitives(source, 5, 5, List.of(Font.DEFAULT), 16);
    FontServiceImpl.ResolvedPrimitiveSequence atEnd =
        fontService.resolvePrimitives(
            source, source.length(), source.length(), List.of(Font.DEFAULT), 16);
    FontServiceImpl.ResolvedPrimitiveSequence emptySource =
        fontService.resolvePrimitives("", 0, 0, List.of(Font.DEFAULT), 16);

    assertEquals(5, full.primitives().size());
    assertTrue(beforeSupplementary.primitives().isEmpty());
    assertTrue(afterSupplementary.primitives().isEmpty());
    assertTrue(afterCrlf.primitives().isEmpty());
    assertTrue(atEnd.primitives().isEmpty());
    assertTrue(emptySource.primitives().isEmpty());
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, 2, source.length(), List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, 0, 2, List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, 4, source.length(), List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, 0, 4, List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, -1, 0, List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () -> fontService.resolvePrimitives(source, 3, 2, List.of(Font.DEFAULT), 16));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fontService.resolvePrimitives(
                source, 0, source.length() + 1, List.of(Font.DEFAULT), 16));
  }

  @Test
  void resolvedPrimitives_captureRawBaseAdvanceAndRebasablePairKerning() {
    FontServiceImpl.ResolvedPrimitiveSequence sequence =
        fontService.resolvePrimitives("AV", 0, 2, List.of(Font.DEFAULT), 13.25f);
    FontServiceImpl.ResolvedPrimitive first = sequence.primitives().get(0);
    FontServiceImpl.ResolvedPrimitive second = sequence.primitives().get(1);
    TextMetrics measured = fontService.measureText("AV", Font.DEFAULT, 13.25f, 1.17f);

    assertTrue(first.rawBaseAdvance() > 0);
    assertTrue(first.baseAdvance() > 0);
    assertEquals(-1, first.previousGlyphIndex());
    assertNull(first.previousFontInfo());
    assertSame(first.fontInfo(), second.previousFontInfo());
    assertEquals(first.glyphIndex(), second.previousGlyphIndex());
    assertEquals(
        measured.width(),
        first.baseAdvance()
            + first.pairKerningAdvance()
            + second.baseAdvance()
            + second.pairKerningAdvance());
    assertEquals(
        second.pairKerningAdvance(),
        (float) (int) (second.rawPairKerningAdvance() * stbScale(second, 13.25f) + 0.5f));
  }

  @Test
  void privatePrimitiveAndRunRangeBuilders_keepLongSingleFaceStorageLinear() {
    assertPrivateResolutionBuilderCounts(1);
    assertPrivateResolutionBuilderCounts(16);
    assertPrivateResolutionBuilderCounts(256);
  }

  @Test
  void privateRunRanges_preserveFallbackReplacementAndSeparatorBoundariesWithoutPublicRuns() {
    String missing = new String(Character.toChars(0x10FFFF));
    String source = "A\u96ea" + missing + "\r\nB";
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    FontServiceImpl.ResolvedPrimitiveSequence sequence =
        instrumented.resolvePrimitives(
            source,
            0,
            source.length(),
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            16);
    List<FontServiceImpl.PrivateRunRange> ranges = sequence.runRanges();
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(4, ranges.size());
    assertPrivateRun(ranges.get(0), 0, 1, 0, 1, Font.ROBOTO_REGULAR, 1);
    assertPrivateRun(ranges.get(1), 1, 2, 1, 2, Font.NOTO_SANS_CJK_SC_REGULAR, 1);
    assertPrivateRun(ranges.get(2), 2, 3, 2, 4, Font.ROBOTO_REGULAR, 1);
    assertPrivateRun(ranges.get(3), 5, 6, 6, 7, Font.ROBOTO_REGULAR, 1);
    assertTrue(sequence.primitives().get(ranges.get(2).primitiveStart()).replacement());
    assertEquals(
        FontServiceImpl.SeparatorKind.CRLF_START,
        sequence.primitives().get(3).separatorKind());
    assertEquals(
        FontServiceImpl.SeparatorKind.CRLF_END,
        sequence.primitives().get(4).separatorKind());
    assertThrows(UnsupportedOperationException.class, () -> sequence.primitives().clear());
    assertThrows(UnsupportedOperationException.class, () -> sequence.runRanges().clear());
    assertFalse(
        Arrays.stream(FontServiceImpl.ResolvedPrimitiveSequence.class.getDeclaredFields())
            .anyMatch(field -> field.getType() == ResolvedTextRun.class));
    assertTrue(ranges.stream().noneMatch(ResolvedTextRun.class::isInstance));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(4, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(
        4,
        snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        0,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
  }

  @Test
  void privatePreparedMeasurement_buildsHardLinesFromResolvedRangesAndLocalSlots() {
    String source = "AV\r\n\u96eaB\n";

    FontServiceImpl.PrivatePreparedMeasurement prepared =
        fontService.preparePrivateMeasurement(
            source,
            0,
            source.length(),
            2.5f,
            List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
            16,
            1.2f,
            Float.POSITIVE_INFINITY,
            true);

    assertEquals(Font.ROBOTO_REGULAR, prepared.primaryFont());
    assertEquals(3, prepared.lines().size());
    assertEquals(prepared.fontMetrics().lineHeight() * 3, prepared.height());
    assertTrue(prepared.alreadyFinal());
    assertEquals(2.5f, prepared.offsetX());
    assertEquals(Float.POSITIVE_INFINITY, prepared.maxWidth());
    assertTrue(prepared.wordWrap());

    FontServiceImpl.PrivatePreWrapLine first = prepared.lines().get(0);
    assertPrivateLine(first, 0, 2, 0, 1, 0, 2, List.of(0, 1, 2));
    assertEquals(2, first.rawAdvanceSlots().size());
    assertEquals(2, first.rebasedAdvanceSlots().size());
    assertEquals(first.textAdvance() + 2.5f, first.width());
    assertEquals(first.fontMetrics(), prepared.fontMetrics());
    assertEquals(prepared.fontMetrics().baseline(), first.baseline());
    assertEquals(prepared.fontMetrics().lineHeight(), first.height());

    FontServiceImpl.PrivatePreWrapLine second = prepared.lines().get(1);
    assertPrivateLine(second, 4, 6, 1, 3, 4, 6, List.of(4, 5, 6));
    assertEquals(second.textAdvance(), second.width());
    assertEquals(second.rawAdvanceSlots().get(0), second.rebasedAdvanceSlots().get(0));

    FontServiceImpl.PrivatePreWrapLine trailing = prepared.lines().get(2);
    assertPrivateLine(trailing, 7, 7, 3, 3, 7, 7, List.of(7));
    assertTrue(trailing.rawAdvanceSlots().isEmpty());
    assertTrue(trailing.rebasedAdvanceSlots().isEmpty());
    assertEquals(0, trailing.width());
    assertEquals(Math.max(first.width(), second.width()), prepared.width());

    assertThrows(UnsupportedOperationException.class, () -> prepared.lines().clear());
    assertThrows(UnsupportedOperationException.class, () -> first.caretBoundaries().clear());
    assertThrows(UnsupportedOperationException.class, () -> first.rawAdvanceSlots().clear());
    assertThrows(UnsupportedOperationException.class, () -> first.rebasedAdvanceSlots().clear());
    assertFalse(Modifier.isPublic(FontServiceImpl.PrivatePreparedMeasurement.class.getModifiers()));
    assertFalse(Modifier.isPublic(FontServiceImpl.PrivatePreWrapLine.class.getModifiers()));
    assertTrue(prepared.lines().stream().noneMatch(TextLineMetrics.class::isInstance));
    assertTrue(prepared.sequence().runRanges().stream().noneMatch(ResolvedTextRun.class::isInstance));
    assertFalse(
        Arrays.stream(FontServiceImpl.PrivatePreparedMeasurement.class.getDeclaredFields())
            .anyMatch(field -> field.getType().isArray()));
    assertFalse(
        Arrays.stream(FontServiceImpl.PrivatePreWrapLine.class.getRecordComponents())
            .anyMatch(component -> component.getType().isArray()));
  }

  @Test
  void privatePreparedMeasurement_handlesEmptyRangesZeroWidthAndApprovedNumericLimits() {
    FontServiceImpl.PrivatePreparedMeasurement empty =
        fontService.preparePrivateMeasurement(
            "xy", 1, 1, 0, List.of(), Float.MIN_NORMAL, 0, 0, true);

    assertEquals(Font.DEFAULT, empty.primaryFont());
    assertEquals(1, empty.lines().size());
    assertPrivateLine(empty.lines().get(0), 0, 0, 0, 0, 1, 1, List.of(1));
    assertEquals(0, empty.width());
    assertEquals(empty.fontMetrics().lineHeight(), empty.height());
    assertTrue(empty.alreadyFinal());

    FontServiceImpl.PrivatePreparedMeasurement wordWrapOverWidth =
        fontService.preparePrivateMeasurement(
            "a", 0, 1, 0, List.of(Font.DEFAULT), 16, 1, 0, true);
    assertFalse(wordWrapOverWidth.alreadyFinal());
    assertEquals(1, wordWrapOverWidth.lines().size());
    assertTrue(wordWrapOverWidth.lines().get(0).width() > 0);

    FontServiceImpl.PrivatePreparedMeasurement characterWrapOverWidth =
        fontService.preparePrivateMeasurement(
            "a", 0, 1, Float.MAX_VALUE, List.of(Font.DEFAULT), 16, 1, 0, false);
    assertFalse(characterWrapOverWidth.alreadyFinal());

    FontServiceImpl.PrivatePreparedMeasurement wordWrapFits =
        fontService.preparePrivateMeasurement(
            "a", 0, 1, 0, List.of(Font.DEFAULT), 16, 1, Float.POSITIVE_INFINITY, true);
    FontServiceImpl.PrivatePreparedMeasurement characterWrapFits =
        fontService.preparePrivateMeasurement(
            "a", 0, 1, 0, List.of(Font.DEFAULT), 16, 1, Float.POSITIVE_INFINITY, false);
    assertTrue(wordWrapFits.alreadyFinal());
    assertTrue(characterWrapFits.alreadyFinal());

    FontServiceImpl.PrivatePreparedMeasurement finiteLimits =
        fontService.preparePrivateMeasurement(
            "",
            0,
            0,
            Float.MAX_VALUE,
            List.of(Font.DEFAULT),
            Float.MAX_VALUE,
            Float.MAX_VALUE,
            Float.POSITIVE_INFINITY,
            true);
    assertEquals(1, finiteLimits.lines().size());

    assertInvalidPrivateMeasurement(Float.NaN, 16, 1, 10);
    assertInvalidPrivateMeasurement(-1, 16, 1, 10);
    assertInvalidPrivateMeasurement(Float.POSITIVE_INFINITY, 16, 1, 10);
    assertInvalidPrivateMeasurement(0, 0, 1, 10);
    assertInvalidPrivateMeasurement(0, -1, 1, 10);
    assertInvalidPrivateMeasurement(0, Float.NaN, 1, 10);
    assertInvalidPrivateMeasurement(0, Float.POSITIVE_INFINITY, 1, 10);
    assertInvalidPrivateMeasurement(0, 16, -1, 10);
    assertInvalidPrivateMeasurement(0, 16, Float.NaN, 10);
    assertInvalidPrivateMeasurement(0, 16, Float.POSITIVE_INFINITY, 10);
    assertInvalidPrivateMeasurement(0, 16, 1, -1);
    assertInvalidPrivateMeasurement(0, 16, 1, Float.NaN);
    assertInvalidPrivateMeasurement(0, 16, 1, Float.NEGATIVE_INFINITY);
  }

  @Test
  void privatePreparedMeasurement_builderAndNativeCountsStayLinearAcrossFallbackShapes() {
    assertPrivatePreparationBuilderCounts("a".repeat(128), List.of(Font.DEFAULT), 1);
    assertPrivatePreparationBuilderCounts(
        "A\u96ea".repeat(64),
        List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
        128);
  }

  @Test
  void privateWrapping_selectsWordCharacterAndFallbackBoundariesWithoutPublishingResults() {
    float wordWidth =
        fontService
            .preparePrivateMeasurement(
                "aa ", 0, 3, 0, List.of(Font.DEFAULT), 16, 1.2f,
                Float.POSITIVE_INFINITY, false)
            .width();
    float pairWidth =
        fontService
            .preparePrivateMeasurement(
                "aa", 0, 2, 0, List.of(Font.DEFAULT), 16, 1.2f,
                Float.POSITIVE_INFINITY, false)
            .width();

    FontServiceImpl.PrivatePreparedMeasurement word =
        fontService.preparePrivateMeasurement(
            "aa aa", 0, 5, 0, List.of(Font.DEFAULT), 16, 1.2f, wordWidth, true);
    FontServiceImpl.PrivatePreparedMeasurement character =
        fontService.preparePrivateMeasurement(
            "aa aa", 0, 5, 0, List.of(Font.DEFAULT), 16, 1.2f, wordWidth, false);
    FontServiceImpl.PrivatePreparedMeasurement fallback =
        fontService.preparePrivateMeasurement(
            "aaaa", 0, 4, 0, List.of(Font.DEFAULT), 16, 1.2f, pairWidth, true);

    assertEquals(List.of("0-2", "3-5"), privateLineRanges(word));
    assertEquals(List.of("0-3", "3-5"), privateLineRanges(character));
    assertEquals(List.of("0-2", "2-4"), privateLineRanges(fallback));
    assertTrue(word.lines().stream().noneMatch(TextLineMetrics.class::isInstance));
    assertTrue(character.lines().stream().noneMatch(TextLineMetrics.class::isInstance));
    assertTrue(fallback.lines().stream().noneMatch(TextLineMetrics.class::isInstance));
  }

  @Test
  void privateWrapping_treatsLfCrAndCrlfAtomicallyIncludingTrailingSeparator() {
    String source = "a\rb\nc\r\n";

    FontServiceImpl.PrivatePreparedMeasurement prepared =
        fontService.preparePrivateMeasurement(
            source,
            0,
            source.length(),
            0,
            List.of(Font.DEFAULT),
            16,
            1.2f,
            Float.POSITIVE_INFINITY,
            true);

    assertEquals(List.of("0-1", "2-3", "4-5", "7-7"), privateLineRanges(prepared));
    assertEquals(
        List.of(List.of(0, 1), List.of(2, 3), List.of(4, 5), List.of(7)),
        prepared.lines().stream()
            .map(FontServiceImpl.PrivatePreWrapLine::caretBoundaries)
            .toList());
  }

  @Test
  void privateWrapping_handlesOffsetZeroWidthOversizedFirstAndInfiniteWidthWithAtomicSources() {
    int missingCodePoint = 0x10FFFF;
    String missing = new String(Character.toChars(missingCodePoint));
    String source = "A" + missing + "B";

    FontServiceImpl.PrivatePreparedMeasurement zeroWidth =
        fontService.preparePrivateMeasurement(
            source, 0, source.length(), 0, List.of(Font.DEFAULT), 16, 1.2f, 0, false);
    assertEquals(List.of("0-1", "1-3", "3-4"), privateLineRanges(zeroWidth));
    assertTrue(zeroWidth.lines().stream().allMatch(line -> line.width() > 0));
    FontServiceImpl.ResolvedPrimitive replacement = zeroWidth.sequence().primitives().get(1);
    assertEquals(1, replacement.sourceStart());
    assertEquals(3, replacement.sourceEnd());
    assertEquals(missingCodePoint, replacement.sourceCodePoint());
    assertTrue(replacement.replacement());

    FontServiceImpl.PrivatePreparedMeasurement narrow =
        fontService.preparePrivateMeasurement(
            "abc", 0, 3, 0, List.of(Font.DEFAULT), 16, 1.2f, Float.MIN_VALUE, true);
    assertEquals(List.of("0-1", "1-2", "2-3"), privateLineRanges(narrow));

    float oneGlyphWidth =
        fontService
            .preparePrivateMeasurement(
                "a", 0, 1, 0, List.of(Font.DEFAULT), 16, 1.2f,
                Float.POSITIVE_INFINITY, false)
            .width();
    FontServiceImpl.PrivatePreparedMeasurement offset =
        fontService.preparePrivateMeasurement(
            "ab", 0, 2, oneGlyphWidth, List.of(Font.DEFAULT), 16, 1.2f,
            oneGlyphWidth * 1.5f, false);
    assertEquals(List.of("0-1", "1-2"), privateLineRanges(offset));
    assertEquals(oneGlyphWidth + offset.lines().getFirst().textAdvance(), offset.width());

    FontServiceImpl.PrivatePreparedMeasurement infinite =
        fontService.preparePrivateMeasurement(
            source,
            0,
            source.length(),
            0,
            List.of(Font.DEFAULT),
            16,
            1.2f,
            Float.POSITIVE_INFINITY,
            true);
    assertEquals(List.of("0-4"), privateLineRanges(infinite));
  }

  @Test
  void privateWrapping_visitAndMovementCountsStayBoundedForDeferredAndSingleGlyphLines() {
    String deferredSource = "a " + "b".repeat(512);
    float deferredWidth =
        fontService
            .preparePrivateMeasurement(
                "a b", 0, 3, 0, List.of(Font.DEFAULT), 16, 1.2f,
                Float.POSITIVE_INFINITY, true)
            .width();
    assertPrivateWrapCounts(deferredSource, deferredWidth, true, false);
    assertPrivateWrapCounts("a".repeat(256), 0, false, true);
  }

  @Test
  void preparedRange_isImmutableValidatedAndTranslatesOnlyExplicitLocalIndices() {
    String source = "x\uD83D\uDE00\r\ny";
    List<Font> fonts = new ArrayList<>(List.of(Font.DEFAULT));

    PreparedRange request =
        new PreparedRange(source, 1, 5, 2.5f, fonts, 16, 1.2f, 100, true);
    fonts.clear();

    assertSame(source, request.source());
    assertEquals(List.of(Font.DEFAULT), request.fonts());
    assertEquals(4, request.length());
    assertEquals(1, request.absoluteIndex(0));
    assertEquals(5, request.absoluteIndex(request.length()));
    assertThrows(UnsupportedOperationException.class, () -> request.fonts().clear());
    assertThrows(IllegalArgumentException.class, () -> request.absoluteIndex(-1));
    assertThrows(
        IllegalArgumentException.class, () -> request.absoluteIndex(request.length() + 1));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PreparedRange(source, 2, 5, 0, List.of(), 16, 1, 100, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PreparedRange(source, 1, 4, 0, List.of(), 16, 1, 100, false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new PreparedRange(source, -1, 3, 0, List.of(), 16, 1, 100, false));
  }

  @Test
  void privateRangePreparation_reusesSharedSourceAndAttributesOnlyRangeLocalWork() {
    String source = "A\u96eaB\u96eaC\u96ea";
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();
    List<Font> fonts = List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR);

    List<FontServiceImpl.PrivatePreparedMeasurement> prepared =
        List.of(
            instrumented.prepareRange(
                new PreparedRange(source, 0, 2, 0, fonts, 16, 1.2f, 100, false)),
            instrumented.prepareRange(
                new PreparedRange(source, 2, 4, 0, fonts, 16, 1.2f, 100, false)),
            instrumented.prepareRange(
                new PreparedRange(source, 4, 6, 0, fonts, 16, 1.2f, 100, false)));
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(List.of(0, 2, 4), prepared.stream().map(item -> item.request().start()).toList());
    assertEquals(List.of(2, 4, 6), prepared.stream().map(item -> item.request().end()).toList());
    assertTrue(prepared.stream().allMatch(item -> item.request().source() == source));
    assertEquals(
        List.of(List.of(0, 1, 2), List.of(2, 3, 4), List.of(4, 5, 6)),
        prepared.stream()
            .map(item -> item.lines().getFirst().caretBoundaries())
            .toList());
    assertEquals(3, snapshot.value(TextDiagnosticCounter.RANGE_PREPARATIONS));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(6, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.COMPLETE_TEXT_MEASUREMENTS));
    assertEquals(
        0,
        Arrays.stream(TextDiagnosticCounter.values())
            .filter(counter -> counter.name().startsWith("TEXT_MEASURER_"))
            .mapToLong(snapshot::value)
            .sum());
    assertEquals(
        0,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
  }

  @Test
  void diagnostics_glyphSlotCopiesStayLinearForLongSingleFontRuns() {
    assertLinearRunAssemblyCounts(1);
    assertLinearRunAssemblyCounts(4);
    assertLinearRunAssemblyCounts(8);
    assertLinearRunAssemblyCounts(16);
  }

  @Test
  void disabledDiagnosticsUseStableNoOpResultsAcrossMeasurement() {
    FontServiceImpl disabled = new FontServiceImpl(new FontStorageImpl(), false);
    disabled.installSemanticOwner();
    DiagnosticSnapshot before = disabled.diagnostics().snapshot();

    disabled.measureText("unchanged", Font.DEFAULT, 16, 1.2f);

    assertSame(DiagnosticSession.disabled(), disabled.diagnostics());
    assertSame(before, disabled.diagnostics().snapshot());
  }

  private DiagnosticSession diagnostics() {
    return DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
  }

  private void assertPrimitive(
      FontServiceImpl.ResolvedPrimitive primitive,
      int sourceStart,
      int sourceEnd,
      int sourceCodePoint,
      int renderedCodePoint,
      Font font,
      boolean replacement) {
    assertEquals(sourceStart, primitive.sourceStart());
    assertEquals(sourceEnd, primitive.sourceEnd());
    assertEquals(sourceCodePoint, primitive.sourceCodePoint());
    assertEquals(renderedCodePoint, primitive.renderedCodePoint());
    assertEquals(font, primitive.font());
    assertEquals(replacement, primitive.replacement());
    assertNotNull(primitive.fontInfo());
    assertTrue(primitive.glyphIndex() >= 0);
  }

  private float stbScale(FontServiceImpl.ResolvedPrimitive primitive, float fontSize) {
    return org.lwjgl.stb.STBTruetype.stbtt_ScaleForMappingEmToPixels(
        primitive.fontInfo(), fontSize);
  }

  private void assertPrivateRun(
      FontServiceImpl.PrivateRunRange range,
      int primitiveStart,
      int primitiveEnd,
      int sourceStart,
      int sourceEnd,
      Font font,
      int glyphCount) {
    assertEquals(primitiveStart, range.primitiveStart());
    assertEquals(primitiveEnd, range.primitiveEnd());
    assertEquals(sourceStart, range.sourceStart());
    assertEquals(sourceEnd, range.sourceEnd());
    assertEquals(font, range.font());
    assertNotNull(range.fontInfo());
    assertEquals(glyphCount, range.glyphCount());
  }

  private void assertPrivateResolutionBuilderCounts(int glyphCount) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    FontServiceImpl.ResolvedPrimitiveSequence sequence =
        instrumented.resolvePrimitives(
            "a".repeat(glyphCount),
            0,
            glyphCount,
            List.of(Font.DEFAULT),
            16);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(glyphCount, sequence.primitives().size());
    assertEquals(1, sequence.runRanges().size());
    assertPrivateRun(
        sequence.runRanges().get(0),
        0,
        glyphCount,
        0,
        glyphCount,
        Font.DEFAULT,
        glyphCount);
    assertEquals(
        glyphCount, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(
        glyphCount, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(
        glyphCount,
        snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        0,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
  }

  private void assertPrivateLine(
      FontServiceImpl.PrivatePreWrapLine line,
      int primitiveStart,
      int primitiveEnd,
      int runRangeStart,
      int runRangeEnd,
      int sourceStart,
      int sourceEnd,
      List<Integer> caretBoundaries) {
    assertEquals(primitiveStart, line.primitiveStart());
    assertEquals(primitiveEnd, line.primitiveEnd());
    assertEquals(runRangeStart, line.runRangeStart());
    assertEquals(runRangeEnd, line.runRangeEnd());
    assertEquals(sourceStart, line.sourceStart());
    assertEquals(sourceEnd, line.sourceEnd());
    assertEquals(sourceEnd - sourceStart, line.charCount());
    assertEquals(caretBoundaries, line.caretBoundaries());
  }

  private List<String> privateLineRanges(
      FontServiceImpl.PrivatePreparedMeasurement prepared) {
    return prepared.lines().stream()
        .map(line -> line.sourceStart() + "-" + line.sourceEnd())
        .toList();
  }

  private void assertPrivateWrapCounts(
      String source,
      float maxWidth,
      boolean wordWrap,
      boolean expectOneGlyphPerLine) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    FontServiceImpl.PrivatePreparedMeasurement prepared =
        instrumented.preparePrivateMeasurement(
            source,
            0,
            source.length(),
            0,
            List.of(Font.DEFAULT),
            16,
            1.2f,
            maxWidth,
            wordWrap);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();
    int primitiveCount = source.codePointCount(0, source.length());

    assertEquals(primitiveCount, snapshot.value(TextDiagnosticCounter.WRAP_PRIMITIVE_VISITS));
    assertEquals(primitiveCount, snapshot.value(TextDiagnosticCounter.SOURCE_CODE_POINTS_SCANNED));
    assertEquals(primitiveCount, snapshot.value(TextDiagnosticCounter.LOGICAL_GLYPH_RESOLUTIONS));
    assertEquals(primitiveCount, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS));
    assertEquals(
        primitiveCount - 1,
        snapshot.value(TextDiagnosticCounter.NATIVE_KERNING_CALLS));
    assertEquals(primitiveCount, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(
        primitiveCount,
        snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        0,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertTrue(prepared.lines().stream().allMatch(line -> line.charCount() > 0));
    if (expectOneGlyphPerLine) {
      assertEquals(primitiveCount, prepared.lines().size());
    }
  }

  private void assertInvalidPrivateMeasurement(
      float offsetX, float fontSize, float lineHeight, float maxWidth) {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            fontService.preparePrivateMeasurement(
                "a",
                0,
                1,
                offsetX,
                List.of(Font.DEFAULT),
                fontSize,
                lineHeight,
                maxWidth,
                true));
  }

  private void assertPrivatePreparationBuilderCounts(
      String source, List<Font> fonts, int expectedRunCount) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    FontServiceImpl.PrivatePreparedMeasurement prepared =
        instrumented.preparePrivateMeasurement(
            source, 0, source.length(), 0, fonts, 16, 1.2f, Float.POSITIVE_INFINITY, true);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();
    int glyphCount = source.codePointCount(0, source.length());

    assertEquals(1, prepared.lines().size());
    assertEquals(glyphCount, prepared.lines().get(0).rawAdvanceSlots().size());
    assertEquals(glyphCount, prepared.lines().get(0).rebasedAdvanceSlots().size());
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(expectedRunCount, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.LINE_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.LINE_BUILDER_FREEZES));
    assertEquals(
        glyphCount + 1,
        snapshot.value(TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CARET_BOUNDARY_BUILDER_FREEZES));
    assertEquals(
        glyphCount * 2,
        snapshot.value(TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_APPENDS));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.ADVANCE_SLOT_BUILDER_FREEZES));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RESULT_BUILDER_FREEZES));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.RANGE_PREPARATIONS));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(
        glyphCount,
        snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        0,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.NATIVE_GLYPH_ADVANCE_CALLS));
    assertEquals(
        expectedRunCount == 1 ? glyphCount - 1 : 0,
        snapshot.value(TextDiagnosticCounter.NATIVE_KERNING_CALLS));
  }

  private void assertLinearRunAssemblyCounts(int glyphCount) {
    DiagnosticSession diagnostics = diagnostics();
    FontServiceImpl instrumented =
        new FontServiceImpl(
            new FontStorageImpl(), false, FontChainResolver.DEFAULT, diagnostics);
    instrumented.installSemanticOwner();

    instrumented.measureText("a".repeat(glyphCount), Font.DEFAULT, 16, 1.2f);
    DiagnosticSnapshot snapshot = diagnostics.snapshot();

    assertEquals(glyphCount * 2L, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_COPIED));
    assertEquals(0, snapshot.value(TextDiagnosticCounter.GLYPH_SLOTS_MOVED));
    assertEquals(glyphCount, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_APPENDS));
    assertEquals(1, snapshot.value(TextDiagnosticCounter.CHARACTER_BUILDER_FREEZES));
    assertEquals(
        glyphCount * 2L,
        snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_APPENDS));
    assertEquals(
        2,
        snapshot.value(TextDiagnosticCounter.GLYPH_SLOT_BUILDER_FREEZES));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_APPENDS));
    assertEquals(2, snapshot.value(TextDiagnosticCounter.RUN_BUILDER_FREEZES));
    assertEquals(
        glyphCount,
        snapshot.value(TextDiagnosticCounter.INITIAL_RESOLUTION_GLYPH_SLOTS_COPIED));
    assertEquals(
        glyphCount,
        snapshot.value(TextDiagnosticCounter.RANGE_MATERIALIZATION_GLYPH_SLOTS_COPIED));
  }
}
