package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;

class PreparedInlineTextTest {

  private static final String POLICY_SOURCE = "a\r\n \t\f\u000B\uD83D\uDE00b";

  @ParameterizedTest(name = "{0}")
  @MethodSource("whitespacePolicies")
  void prepare_mapsEveryValidBoundaryForEveryWhitespacePolicy(
      WhiteSpace policy, String expectedText, int[] expectedStarts, int[] expectedEnds) {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));

    PreparedInlineText prepared =
        PreparedInlineText.prepare(POLICY_SOURCE, style(policy, 3), diagnostics);

    assertEquals(expectedText, prepared.text());
    assertEquals(1, diagnostics.snapshot().value(TextDiagnosticCounter.NORMALIZATION_SCANS));
    for (int sourceBoundary : validBoundaries(POLICY_SOURCE)) {
      assertEquals(
          preparedBoundariesForSource(sourceBoundary, expectedStarts, expectedEnds),
          prepared.preparedBoundariesForSource(sourceBoundary),
          "source boundary " + sourceBoundary);
    }
    for (int preparedBoundary : validBoundaries(expectedText)) {
      assertEquals(
          sourceBoundariesForPrepared(
              preparedBoundary, POLICY_SOURCE.length(), expectedStarts, expectedEnds),
          prepared.sourceBoundariesForPrepared(preparedBoundary),
          "prepared boundary " + preparedBoundary);
    }
    assertThrows(
        IllegalArgumentException.class, () -> prepared.preparedBoundariesForSource(2));
    assertThrows(
        IllegalArgumentException.class, () -> prepared.preparedBoundariesForSource(8));
    int preparedSurrogateBoundary = expectedText.indexOf('\uD83D') + 1;
    assertThrows(
        IllegalArgumentException.class,
        () -> prepared.sourceBoundariesForPrepared(preparedSurrogateBoundary));
  }

  @Test
  void prepare_mapsCrLfCollapseTabExpansionAndSpecialWhitespaceInOneScan() {
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    PreparedInlineText normal =
        PreparedInlineText.prepare("a\r\n\t\fb", style(WhiteSpace.NORMAL, 4), diagnostics);

    assertEquals("a b", normal.text());
    assertEquals(new PreparedInlineText.BoundarySpan(1, 2), normal.preparedBoundariesForSource(3));
    assertEquals(new PreparedInlineText.BoundarySpan(5, 5), normal.sourceBoundariesForPrepared(2));
    assertThrows(IllegalArgumentException.class, () -> normal.preparedBoundariesForSource(2));
    assertEquals(1, diagnostics.snapshot().value(TextDiagnosticCounter.NORMALIZATION_SCANS));
    assertEquals(
        3,
        diagnostics
            .snapshot()
            .value(TextDiagnosticCounter.INLINE_PREPARED_CODE_POINTS_APPENDED));

    PreparedInlineText pre =
        PreparedInlineText.prepare("a\t\f\u000Bb", style(WhiteSpace.PRE, 3), diagnostics);
    assertEquals("a   \f\u000Bb", pre.text());
    assertEquals(3, pre.units().size());
    assertEquals(new PreparedInlineText.BoundarySpan(1, 2), pre.sourceBoundariesForPrepared(2));

    PreparedInlineText preLine =
        PreparedInlineText.prepare("a \t\f\u000B\nb", style(WhiteSpace.PRE_LINE, 2), diagnostics);
    assertEquals("a \nb", preLine.text());
  }

  @Test
  void prepare_preservesSupplementaryCodePointsAsAtomicRanges() {
    PreparedInlineText prepared =
        PreparedInlineText.prepare(
            "a\uD83D\uDE00b", style(WhiteSpace.PRE, 4), DiagnosticSession.disabled());

    assertEquals("a\uD83D\uDE00b", prepared.text());
    assertEquals(1, prepared.units().size());
    assertEquals(0, prepared.units().get(0).preparedStart());
    assertEquals(4, prepared.units().get(0).preparedEnd());
    assertThrows(IllegalArgumentException.class, () -> prepared.preparedBoundariesForSource(2));
    assertThrows(IllegalArgumentException.class, () -> prepared.sourceBoundariesForPrepared(2));
  }

  @Test
  void prepare_exposesCollapsedAndExpandedBoundaryBiases() {
    PreparedInlineText collapsed =
        PreparedInlineText.prepare(
            " \t ", style(WhiteSpace.NORMAL, 4), DiagnosticSession.disabled());
    assertEquals(" ", collapsed.text());
    assertEquals(
        new PreparedInlineText.BoundarySpan(0, 1),
        collapsed.preparedBoundariesForSource(1));
    assertEquals(
        new PreparedInlineText.BoundarySpan(0, 0),
        collapsed.sourceBoundariesForPrepared(0));
    assertEquals(
        new PreparedInlineText.BoundarySpan(3, 3),
        collapsed.sourceBoundariesForPrepared(1));

    PreparedInlineText expanded =
        PreparedInlineText.prepare("\t", style(WhiteSpace.PRE, 3), DiagnosticSession.disabled());
    assertEquals("   ", expanded.text());
    assertEquals(
        new PreparedInlineText.BoundarySpan(0, 1),
        expanded.sourceBoundariesForPrepared(1));
    assertEquals(
        new PreparedInlineText.BoundarySpan(0, 1),
        expanded.sourceBoundariesForPrepared(2));
  }

  @Test
  void subrange_rejectsOutOfUnitAndSurrogateSplittingBoundaries() {
    PreparedInlineText prepared =
        PreparedInlineText.prepare(
            "ab\uD83D\uDE00", style(WhiteSpace.NORMAL, 4), DiagnosticSession.disabled());
    PreparedInlineText.Unit unit = prepared.units().get(0);

    assertThrows(IllegalArgumentException.class, () -> prepared.subrange(unit, -1, 1));
    assertThrows(IllegalArgumentException.class, () -> prepared.subrange(unit, 0, 3));
    assertEquals(0, prepared.subrange(unit, 0, 0).sourceStart());
    assertEquals(0, prepared.subrange(unit, 0, 0).sourceEnd());
    assertEquals(0, prepared.subrange(unit, 0, 2).sourceStart());
    assertEquals(2, prepared.subrange(unit, 0, 2).sourceEnd());
  }

  @Test
  void cacheIsWarmAcrossPassesBoundedByChurnAndIndependentlyClearable() {
    ResolvedStyle style = style(WhiteSpace.NORMAL, 4);
    DiagnosticSession diagnostics = DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    PreparedInlineTextCache cache = new PreparedInlineTextCache(true, 2, 256);

    PreparedInlineText first = cache.getOrPrepare("same", style, diagnostics);
    PreparedInlineText warm = cache.getOrPrepare("same", style, diagnostics);
    assertEquals(first, warm);
    assertEquals(1, cache.stats().hits());
    assertEquals(1, cache.stats().entries());

    cache.getOrPrepare("one", style, diagnostics);
    cache.getOrPrepare("two", style, diagnostics);
    assertEquals(2, cache.stats().entries());
    cache.clear();
    assertEquals(0, cache.stats().entries());
    cache.close();
  }

  @Test
  void disabledCacheRetainsNoPreparedValues() {
    PreparedInlineTextCache cache = new PreparedInlineTextCache(false, 2, 256);
    ResolvedStyle style = style(WhiteSpace.NORMAL, 4);
    DiagnosticSession diagnostics = DiagnosticSession.enabled(List.of(TextDiagnosticCounter.values()));
    cache.getOrPrepare("same", style, diagnostics);
    cache.getOrPrepare("same", style, diagnostics);
    assertEquals(0, cache.stats().entries());
    assertEquals(0, cache.stats().admissions());
  }

  private ResolvedStyle style(WhiteSpace whiteSpace, Integer tabSize) {
    ResolvedStyle style = new ResolvedStyle();
    style.whiteSpace(whiteSpace);
    style.tabSize(tabSize);
    return style;
  }

  private static Stream<Arguments> whitespacePolicies() {
    int[] collapsedStarts = {0, 1, 7, 7, 9};
    int[] collapsedEnds = {1, 7, 9, 9, 10};
    int[] preLineStarts = {0, 1, 3, 7, 7, 9};
    int[] preLineEnds = {1, 3, 7, 9, 9, 10};
    int[] preservedStarts = {0, 1, 3, 4, 4, 4, 5, 6, 7, 7, 9};
    int[] preservedEnds = {1, 3, 4, 5, 5, 5, 6, 7, 9, 9, 10};
    return Stream.of(
        Arguments.of(WhiteSpace.NORMAL, "a \uD83D\uDE00b", collapsedStarts, collapsedEnds),
        Arguments.of(WhiteSpace.NOWRAP, "a \uD83D\uDE00b", collapsedStarts, collapsedEnds),
        Arguments.of(WhiteSpace.PRE_LINE, "a\n \uD83D\uDE00b", preLineStarts, preLineEnds),
        Arguments.of(WhiteSpace.PRE, "a\n    \f\u000B\uD83D\uDE00b", preservedStarts, preservedEnds),
        Arguments.of(
            WhiteSpace.PRE_WRAP,
            "a\n    \f\u000B\uD83D\uDE00b",
            preservedStarts,
            preservedEnds));
  }

  private static List<Integer> validBoundaries(String text) {
    List<Integer> result = new ArrayList<>();
    for (int boundary = 0; boundary <= text.length(); boundary++) {
      boolean splitsSurrogate =
          boundary > 0
              && boundary < text.length()
              && Character.isHighSurrogate(text.charAt(boundary - 1))
              && Character.isLowSurrogate(text.charAt(boundary));
      boolean splitsCrLf =
          boundary > 0
              && boundary < text.length()
              && text.charAt(boundary - 1) == '\r'
              && text.charAt(boundary) == '\n';
      if (!splitsSurrogate && !splitsCrLf) result.add(boundary);
    }
    return result;
  }

  private static PreparedInlineText.BoundarySpan preparedBoundariesForSource(
      int sourceBoundary, int[] starts, int[] ends) {
    int before = 0;
    int after = 0;
    for (int index = 0; index < starts.length; index++) {
      if (ends[index] <= sourceBoundary) before = index + 1;
      if (starts[index] < sourceBoundary) after = index + 1;
    }
    return new PreparedInlineText.BoundarySpan(before, Math.max(before, after));
  }

  private static PreparedInlineText.BoundarySpan sourceBoundariesForPrepared(
      int preparedBoundary, int sourceLength, int[] starts, int[] ends) {
    int before = preparedBoundary == 0 ? 0 : ends[preparedBoundary - 1];
    int after = preparedBoundary == starts.length ? sourceLength : starts[preparedBoundary];
    return new PreparedInlineText.BoundarySpan(Math.min(before, after), Math.max(before, after));
  }
}
