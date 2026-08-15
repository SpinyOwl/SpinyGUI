package com.spinyowl.spinygui.core.system.font.internal;

import com.spinyowl.spinygui.core.system.font.TextMetrics;
import java.util.List;
import java.util.Objects;

/** Immutable final text metrics paired one-to-one with final line-local caret stops. */
public record ResolvedMeasurement(
    TextMetrics metrics, List<FinalLineCaretStops> lineCaretStops) {

  public ResolvedMeasurement {
    Objects.requireNonNull(metrics, "metrics");
    Objects.requireNonNull(lineCaretStops, "lineCaretStops");
    lineCaretStops = List.copyOf(lineCaretStops);
    if (metrics.lines().size() != lineCaretStops.size()) {
      throw new IllegalArgumentException(
          "Each final text line must have exactly one final caret-stop value");
    }
  }
}
