package com.spinyowl.spinygui.core.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class InlineSourceMappingTest {

  @Test
  void mapsCollapsedTabAndAtomicCrLfSpansInRenderedCoordinates() {
    InlineSourceMapping mapping =
        InlineSourceMapping.forPreparedText(
                "a\r\n\tb",
                "a b",
                new int[] {0, 1, 4},
                new int[] {1, 4, 5})
            .fragment(" ", 1, 2);

    assertEquals(new InlineSourceMapping.SourceSpan(1, 4), mapping.sourceSpanAt(0));
    assertEquals(
        new InlineSourceMapping.BoundarySpan(1, 1),
        mapping.sourceBoundariesForFragment(0));
    assertEquals(
        new InlineSourceMapping.BoundarySpan(4, 4),
        mapping.sourceBoundariesForFragment(1));
    assertThrows(IllegalArgumentException.class, () -> mapping.fragmentBoundariesForSource(2));

    InlineSourceMapping tab =
        InlineSourceMapping.forPreparedText(
                "a\tb",
                "a   b",
                new int[] {0, 1, 1, 1, 2},
                new int[] {1, 2, 2, 2, 3})
            .fragment("   ", 1, 4);
    assertEquals(new InlineSourceMapping.SourceSpan(1, 2), tab.sourceSpanAt(0));
    assertEquals(new InlineSourceMapping.SourceSpan(1, 2), tab.sourceSpanAt(2));
    assertEquals(
        new InlineSourceMapping.BoundarySpan(1, 2), tab.sourceBoundariesForFragment(1));
  }

  @Test
  void mapsReplacementAndSupplementaryOutputWithoutSplitBoundaries() {
    InlineSourceMapping replacement =
        InlineSourceMapping.forRenderedText(
            "x\uDBFF\uDFFFy", "x\uFFFDy", new int[] {0, 1, 3}, new int[] {1, 3, 4});
    assertEquals(new InlineSourceMapping.SourceSpan(1, 3), replacement.sourceSpanAt(1));

    InlineSourceMapping supplementary =
        InlineSourceMapping.forRenderedText(
            "x\uD83D\uDE00y",
            "x\uD83D\uDE00y",
            new int[] {0, 1, 1, 3},
            new int[] {1, 3, 3, 4});
    assertEquals(new InlineSourceMapping.SourceSpan(1, 3), supplementary.sourceSpanAt(1));
    assertEquals(new InlineSourceMapping.SourceSpan(1, 3), supplementary.sourceSpanAt(2));
    assertThrows(
        IllegalArgumentException.class, () -> supplementary.sourceBoundariesForFragment(2));
  }

  @Test
  void freezesContributionArraysAndHasExplicitValueSemantics() {
    int[] starts = {0, 1};
    int[] ends = {1, 2};
    InlineSourceMapping first =
        InlineSourceMapping.forRenderedText("ab", "ab", starts, ends);
    starts[0] = 1;
    ends[0] = 2;
    InlineSourceMapping second =
        InlineSourceMapping.forRenderedText("ab", "ab", new int[] {0, 1}, new int[] {1, 2});

    assertEquals(new InlineSourceMapping.SourceSpan(0, 1), first.sourceSpanAt(0));
    assertEquals(second, first);
    assertEquals(second.hashCode(), first.hashCode());
    assertThrows(IllegalStateException.class, InlineSourceMapping.unmapped()::sourceStart);
  }
}
