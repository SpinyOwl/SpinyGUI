package com.spinyowl.spinygui.core.system.cache;

import java.util.List;
import java.util.Objects;

/** Immutable final layout value; consumer placement state is not retained. */
public record WrappedLayoutValue(List<Line> lines) {
  public WrappedLayoutValue { lines = List.copyOf(Objects.requireNonNull(lines, "lines")); }

  public record Line(int sourceStart, int sourceEnd, List<Float> cumulativeCarets) {
    public Line { cumulativeCarets = List.copyOf(Objects.requireNonNull(cumulativeCarets, "cumulativeCarets")); }
  }
}
