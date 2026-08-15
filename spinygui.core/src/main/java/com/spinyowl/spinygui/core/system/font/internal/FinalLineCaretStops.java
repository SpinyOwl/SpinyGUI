package com.spinyowl.spinygui.core.system.font.internal;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.TextDiagnosticCounter;
import com.spinyowl.spinygui.core.system.font.TextCaretMetrics;
import java.util.Arrays;
import java.util.Objects;

/** Immutable absolute UTF-16 boundaries paired with line-local cumulative advances. */
public final class FinalLineCaretStops {
  private final int[] sourceBoundaries;
  private final float[] advances;

  public FinalLineCaretStops(int[] sourceBoundaries, float[] advances) {
    Objects.requireNonNull(sourceBoundaries, "sourceBoundaries");
    Objects.requireNonNull(advances, "advances");
    if (sourceBoundaries.length == 0 || sourceBoundaries.length != advances.length) {
      throw new IllegalArgumentException(
          "Caret boundary and advance arrays must be non-empty and have equal length");
    }
    int previousBoundary = sourceBoundaries[0];
    float previousAdvance = advances[0];
    if (!Float.isFinite(previousAdvance) || previousAdvance != 0) {
      throw new IllegalArgumentException("The first caret advance must be finite zero");
    }
    for (int index = 1; index < sourceBoundaries.length; index++) {
      if (sourceBoundaries[index] <= previousBoundary) {
        throw new IllegalArgumentException("Caret source boundaries must increase strictly");
      }
      if (!Float.isFinite(advances[index]) || advances[index] < previousAdvance) {
        throw new IllegalArgumentException("Caret advances must be finite and nondecreasing");
      }
      previousBoundary = sourceBoundaries[index];
      previousAdvance = advances[index];
    }
    this.sourceBoundaries = Arrays.copyOf(sourceBoundaries, sourceBoundaries.length);
    this.advances = Arrays.copyOf(advances, advances.length);
  }

  public int size() {
    return sourceBoundaries.length;
  }

  public int sourceBoundary(int index) {
    return sourceBoundaries[index];
  }

  public float advance(int index) {
    return advances[index];
  }

  /** Binary midpoint lookup with an exact tie advancing to the following caret stop. */
  public TextCaretMetrics caretAt(float offsetX, DiagnosticSession diagnostics) {
    Objects.requireNonNull(diagnostics, "diagnostics");
    if (Float.isNaN(offsetX) || offsetX < 0 || sourceBoundaries.length == 1) {
      return new TextCaretMetrics(sourceBoundaries[0], 0);
    }
    int last = sourceBoundaries.length - 1;
    if (offsetX >= advances[last]) {
      return new TextCaretMetrics(sourceBoundaries[last], advances[last]);
    }

    int low = 0;
    int high = last - 1;
    while (low <= high) {
      int middle = (low + high) >>> 1;
      diagnostics.increment(TextDiagnosticCounter.CARET_STOP_SEARCH_COMPARISONS);
      float midpoint = (advances[middle] + advances[middle + 1]) / 2f;
      if (offsetX < midpoint) {
        high = middle - 1;
      } else {
        low = middle + 1;
      }
    }
    return new TextCaretMetrics(sourceBoundaries[low], advances[low]);
  }

  /**
   * Finds the line-local caret at or immediately before an absolute UTF-16 source index.
   *
   * <p>Indices outside the line clamp to its first or final stop. An index between two valid source
   * boundaries, including the interior of a supplementary code point, resolves to the preceding
   * boundary.
   */
  public TextCaretMetrics caretAtSourceIndex(int sourceIndex, DiagnosticSession diagnostics) {
    Objects.requireNonNull(diagnostics, "diagnostics");
    if (sourceBoundaries.length == 1 || sourceIndex <= sourceBoundaries[0]) {
      return new TextCaretMetrics(sourceBoundaries[0], advances[0]);
    }
    int last = sourceBoundaries.length - 1;
    if (sourceIndex >= sourceBoundaries[last]) {
      return new TextCaretMetrics(sourceBoundaries[last], advances[last]);
    }

    int low = 1;
    int high = last - 1;
    while (low <= high) {
      int middle = (low + high) >>> 1;
      diagnostics.increment(TextDiagnosticCounter.CARET_STOP_SEARCH_COMPARISONS);
      int boundary = sourceBoundaries[middle];
      if (sourceIndex < boundary) {
        high = middle - 1;
      } else if (sourceIndex > boundary) {
        low = middle + 1;
      } else {
        return new TextCaretMetrics(boundary, advances[middle]);
      }
    }
    return new TextCaretMetrics(sourceBoundaries[high], advances[high]);
  }
}
