package com.spinyowl.spinygui.core.style.types.grid;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import org.junit.jupiter.api.Test;

class GridTrackValueTest {

  @Test
  void fractionRejectsNegativeAndNonFiniteValues() {
    assertThrows(IllegalArgumentException.class, () -> GridFraction.fr(-1));
    assertThrows(IllegalArgumentException.class, () -> GridFraction.fr(Float.NaN));
    assertThrows(IllegalArgumentException.class, () -> GridFraction.fr(Float.POSITIVE_INFINITY));
  }

  @Test
  void trackSizesUseValueEquality() {
    assertEquals(GridTrackSize.fixed(Length.pixel(12)), GridTrackSize.fixed(Length.pixel(12)));
    assertEquals(GridTrackSize.flexible(GridFraction.fr(1)), GridTrackSize.flexible(GridFraction.fr(1)));
    assertEquals(
        GridTrackSize.minmax(GridTrackSize.fixed(Length.pixel(10)), GridTrackSize.flexible(GridFraction.fr(1))),
        GridTrackSize.minmax(GridTrackSize.fixed(Length.pixel(10)), GridTrackSize.flexible(GridFraction.fr(1))));
  }

  @Test
  void invalidMinmaxBoundsFailClearly() {
    assertThrows(
        IllegalArgumentException.class,
        () -> GridTrackSize.minmax(GridTrackSize.flexible(GridFraction.fr(1)), GridTrackSize.AUTO));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            GridTrackSize.minmax(
                GridTrackSize.fixed(Length.pixel(20)), GridTrackSize.fixed(Length.pixel(10))));
  }

  @Test
  void fitContentRejectsInvalidLimits() {
    assertThrows(IllegalArgumentException.class, () -> GridTrackSize.fitContent(Length.pixel(-1)));
    assertThrows(IllegalArgumentException.class, () -> GridTrackSize.fitContent(Length.pixel(Float.NaN)));
  }

  @Test
  void trackListsDefensivelyCopyInputs() {
    var source = new java.util.ArrayList<>(List.of(GridTrack.of(GridTrackSize.AUTO)));
    var list = GridTrackList.of(source);

    source.clear();

    assertEquals(1, list.tracks().size());
  }

  @Test
  void repeatExpandsFixedCountTracks() {
    var tracks = List.of(GridTrack.of(GridTrackSize.fixed(Length.pixel(10))));

    assertEquals(3, GridTrackRepeat.expand(3, tracks).size());
    assertThrows(IllegalArgumentException.class, () -> GridTrackRepeat.expand(0, tracks));
  }
}
