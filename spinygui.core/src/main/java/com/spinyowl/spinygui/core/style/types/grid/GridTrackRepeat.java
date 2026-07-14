package com.spinyowl.spinygui.core.style.types.grid;

import java.util.ArrayList;
import java.util.List;

/** Utility for expanding supported fixed-count repeat() track definitions. */
public final class GridTrackRepeat {
  private GridTrackRepeat() {}

  public static List<GridTrack> expand(int count, List<GridTrack> tracks) {
    if (count <= 0) {
      throw new IllegalArgumentException("Grid repeat count must be positive");
    }
    if (tracks == null || tracks.isEmpty()) {
      throw new IllegalArgumentException("Grid repeat must contain at least one track");
    }
    List<GridTrack> expanded = new ArrayList<>(count * tracks.size());
    for (int i = 0; i < count; i++) {
      expanded.addAll(tracks);
    }
    return List.copyOf(expanded);
  }
}
