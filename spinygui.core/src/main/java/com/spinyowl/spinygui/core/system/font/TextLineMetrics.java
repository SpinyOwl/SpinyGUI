package com.spinyowl.spinygui.core.system.font;

import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Metrics for one measured line.
 *
 * <p>Source indices refer to the original text argument, while widths, heights, and baselines use
 * the line's measurement coordinate space. Layout position, viewport scroll, and presentation
 * transforms are applied by consumers and are not included here.
 */
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.NONE)
public final class TextLineMetrics {

  /** Line-local characters, excluding any separator recognized by the measurer. */
  private final CharSequence characters;

  /** Absolute UTF-16 start offset in the original measured text. */
  private final int startIndex;

  /** Absolute, exclusive-end UTF-16 offset in the original measured text. */
  private final int endIndex;

  /** Number of UTF-16 code units in the line; equivalent to {@code endIndex - startIndex}. */
  private final int charCount;

  /**
   * Line-local occupied horizontal extent in pixels. The first line includes the measurement's
   * initial x offset when one was supplied.
   */
  private final float width;

  /** Height of the line in pixels. */
  private final float height;

  /** Line-local baseline offset from the top of the line in pixels. */
  private final float baseline;

  /** Font metrics used to measure this line. */
  private final FontMetrics fontMetrics;

  /** Resolved runs contributing to this line, retaining absolute original UTF-16 ranges. */
  private final List<ResolvedTextRun> runs;

  /** Creates one deep immutable line snapshot. */
  @Builder
  public TextLineMetrics(
      CharSequence characters,
      int startIndex,
      int endIndex,
      int charCount,
      float width,
      float height,
      float baseline,
      FontMetrics fontMetrics,
      List<ResolvedTextRun> runs) {
    this.characters = characters == null ? "" : characters.toString();
    this.startIndex = startIndex;
    this.endIndex = endIndex;
    this.charCount = charCount;
    this.width = width;
    this.height = height;
    this.baseline = baseline;
    this.fontMetrics = fontMetrics;
    this.runs = runs == null ? List.of() : List.copyOf(runs);
  }

  public String toString() {
    return characters.toString();
  }
}
