package com.spinyowl.spinygui.core.style.types.grid;

import java.util.List;
import java.util.Objects;

/** Expanded grid template tracks plus the final trailing line names. */
public record GridTrackList(List<GridTrack> tracks, List<String> trailingLineNames) {

  public static final GridTrackList NONE = new GridTrackList(List.of(), List.of());

  public GridTrackList {
    Objects.requireNonNull(tracks, "tracks");
    Objects.requireNonNull(trailingLineNames, "trailingLineNames");
    tracks = List.copyOf(tracks);
    trailingLineNames = List.copyOf(trailingLineNames);
    if (trailingLineNames.stream().anyMatch(name -> name == null || name.isBlank())) {
      throw new IllegalArgumentException("Grid line names must be non-blank");
    }
  }

  public static GridTrackList of(List<GridTrack> tracks) {
    return new GridTrackList(tracks, List.of());
  }
}
