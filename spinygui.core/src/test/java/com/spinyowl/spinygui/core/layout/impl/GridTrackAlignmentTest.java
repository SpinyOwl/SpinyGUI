package com.spinyowl.spinygui.core.layout.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GridTrackAlignmentTest {

  @ParameterizedTest
  @CsvSource({
      "flex-start, 0, 10, 0, 30",
      "flex-end, 50, 10, 50, 80",
      "center, 25, 10, 25, 55",
      "space-between, 0, 60, 0, 80",
      "space-around, 12.5, 35, 12.5, 67.5",
      "space-evenly, 16.666667, 26.666668, 16.666667, 63.333336"
  })
  void resolvesPositiveFreeSpaceForSupportedContentAlignment(
      String value, float offset, float adjustedGap, float firstTrack, float secondTrack) {
    GridTrackAlignment.Geometry geometry = GridTrackAlignment.resolve(100, 40, 2, 10, value);

    assertEquals(offset, geometry.offset(), .0001f);
    assertEquals(adjustedGap, geometry.gap(), .0001f);
    assertEquals(firstTrack, geometry.offset(), .0001f);
    assertEquals(secondTrack, geometry.offset() + 20 + geometry.gap(), .0001f);
  }

  @ParameterizedTest
  @CsvSource({"stretch", "baseline", "unknown"})
  void fallsBackToStartForUnsupportedOrBaselineValues(String value) {
    GridTrackAlignment.Geometry geometry = GridTrackAlignment.resolve(100, 40, 2, 10, value);

    assertEquals(0, geometry.offset(), .0001f);
    assertEquals(10, geometry.gap(), .0001f);
  }

  @ParameterizedTest
  @CsvSource({"50, 0", "40, 0", "30, 0"})
  void retainsCurrentStartGeometryWhenFreeSpaceIsNotPositive(float available, float expectedOffset) {
    GridTrackAlignment.Geometry geometry = GridTrackAlignment.resolve(available, 40, 2, 10, "flex-end");

    assertEquals(expectedOffset, geometry.offset(), .0001f);
    assertEquals(10, geometry.gap(), .0001f);
  }

  @org.junit.jupiter.api.Test
  void spaceBetweenWithOneTrackRetainsStartGeometry() {
    GridTrackAlignment.Geometry geometry = GridTrackAlignment.resolve(100, 20, 1, 10, "space-between");

    assertEquals(0, geometry.offset(), .0001f);
    assertEquals(10, geometry.gap(), .0001f);
  }
}
