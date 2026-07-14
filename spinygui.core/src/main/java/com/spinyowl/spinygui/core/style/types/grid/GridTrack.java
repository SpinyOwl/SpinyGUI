package com.spinyowl.spinygui.core.style.types.grid;

import java.util.List;
import java.util.Objects;

/** One expanded grid track with the named lines that precede it. */
public record GridTrack(List<String> lineNames, GridTrackSize size) {

  public GridTrack {
    Objects.requireNonNull(lineNames, "lineNames");
    Objects.requireNonNull(size, "size");
    lineNames = List.copyOf(lineNames);
    if (lineNames.stream().anyMatch(name -> name == null || name.isBlank())) {
      throw new IllegalArgumentException("Grid line names must be non-blank");
    }
  }

  public static GridTrack of(GridTrackSize size) {
    return new GridTrack(List.of(), size);
  }
}
