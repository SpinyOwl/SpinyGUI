package com.spinyowl.spinygui.core.system.font;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.NONE)
@Builder
public final class TextLineMetrics {

  private CharSequence characters;

  /** Start index in the original measured text. */
  private int startIndex;

  /** End index in the original measured text, exclusive. */
  private int endIndex;

  /** Character count in the line. */
  private int charCount;

  /** Width of the line in pixels. */
  private float width;

  /** Height of the line in pixels. */
  private float height;

  /** Baseline offset from the top of the line in pixels. */
  private float baseline;

  /** Font metrics used to measure this line. */
  private FontMetrics fontMetrics;

  /** Resolved runs contributing to this line, retaining original UTF-16 ranges. */
  @Builder.Default private List<ResolvedTextRun> runs = List.of();

  public String toString() {
    return characters.toString();
  }
}
