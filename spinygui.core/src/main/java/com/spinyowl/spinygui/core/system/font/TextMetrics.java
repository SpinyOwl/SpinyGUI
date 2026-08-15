package com.spinyowl.spinygui.core.system.font;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

/**
 * Aggregate measurement in text-local coordinates.
 *
 * <p>Line and run positions are independent of layout placement, viewport scrolling, and
 * presentation transforms. Source ranges remain offsets into the original measured text.
 */
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.NONE)
@ToString
public final class TextMetrics {

  /** Measured lines in source order. */
  private final List<TextLineMetrics> lines;

  /**
   * Maximum occupied line extent; when a first line is produced, its supplied x offset
   * contributes to this value.
   */
  private final float width;

  /** Sum of measured line heights. */
  private final float height;

  /** Primary-face line height used by each line. */
  private final float lineHeight;

  /** Primary-face metrics used by this measurement. */
  private final FontMetrics fontMetrics;

  /**
   * Creates one immutable aggregate snapshot.
   *
   * <p>The supplied line list is copied exactly once and is never exposed as mutable storage.
   */
  @Builder
  public TextMetrics(
      @Singular List<TextLineMetrics> lines,
      float width,
      float height,
      float lineHeight,
      FontMetrics fontMetrics) {
    this.lines = List.copyOf(lines);
    this.width = width;
    this.height = height;
    this.lineHeight = lineHeight;
    this.fontMetrics = fontMetrics;
  }
}
