package com.spinyowl.spinygui.core.system.font.internal;

import com.spinyowl.spinygui.core.font.Font;
import java.util.List;
import java.util.Objects;

/**
 * Immutable internal request for preparing one half-open range of a shared {@link String} source.
 *
 * <p>The source is retained by identity and is never sliced. Source indices are absolute UTF-16
 * offsets; {@link #absoluteIndex(int)} translates a range-local UTF-16 offset when a compatibility
 * boundary needs that conversion.
 */
public record PreparedRange(
    String source,
    int start,
    int end,
    float offsetX,
    List<Font> fonts,
    float fontSize,
    float lineHeight,
    float maxWidth,
    boolean wordWrap) {

  public PreparedRange {
    Objects.requireNonNull(source, "source");
    Objects.requireNonNull(fonts, "fonts");
    validateSourceRange(source, start, end);
    validateMeasurementInputs(offsetX, fontSize, lineHeight, maxWidth);
    fonts = List.copyOf(fonts);
  }

  /** Number of UTF-16 code units in this request. */
  public int length() {
    return end - start;
  }

  /** Converts a range-local UTF-16 offset to an absolute offset in {@link #source()}. */
  public int absoluteIndex(int rangeLocalIndex) {
    if (rangeLocalIndex < 0 || rangeLocalIndex > length()) {
      throw new IllegalArgumentException(
          "Range-local index %d is outside [0, %d]".formatted(rangeLocalIndex, length()));
    }
    return start + rangeLocalIndex;
  }

  /** Validates a half-open source range without scanning or copying its contents. */
  public static void validateSourceRange(String source, int start, int end) {
    Objects.requireNonNull(source, "source");
    if (start < 0 || start > end || end > source.length()) {
      throw new IllegalArgumentException(
          "Invalid source range [%d, %d) for length %d"
              .formatted(start, end, source.length()));
    }
    validateSourceBoundary(source, start, "start");
    validateSourceBoundary(source, end, "end");
  }

  private static void validateSourceBoundary(String source, int boundary, String label) {
    if (boundary == 0 || boundary == source.length()) {
      return;
    }
    char before = source.charAt(boundary - 1);
    char after = source.charAt(boundary);
    if (Character.isHighSurrogate(before) && Character.isLowSurrogate(after)) {
      throw new IllegalArgumentException(
          "%s boundary %d splits a valid surrogate pair".formatted(label, boundary));
    }
    if (before == '\r' && after == '\n') {
      throw new IllegalArgumentException(
          "%s boundary %d splits a CRLF separator".formatted(label, boundary));
    }
  }

  private static void validateMeasurementInputs(
      float offsetX, float fontSize, float lineHeight, float maxWidth) {
    if (!Float.isFinite(fontSize) || fontSize <= 0) {
      throw new IllegalArgumentException("fontSize must be finite and positive");
    }
    if (!Float.isFinite(lineHeight) || lineHeight < 0) {
      throw new IllegalArgumentException("lineHeight must be finite and nonnegative");
    }
    if (!Float.isFinite(offsetX) || offsetX < 0) {
      throw new IllegalArgumentException("offsetX must be finite and nonnegative");
    }
    if (Float.isNaN(maxWidth)
        || maxWidth < 0
        || maxWidth == Float.NEGATIVE_INFINITY) {
      throw new IllegalArgumentException(
          "maxWidth must be finite and nonnegative or positive infinity");
    }
  }
}
