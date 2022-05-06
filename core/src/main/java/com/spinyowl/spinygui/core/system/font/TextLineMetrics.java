package com.spinyowl.spinygui.core.system.font;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.NONE)
@Builder
public final class TextLineMetrics {

  private CharSequence characters;

  /** Character count in the line. */
  private int charCount;

  /** Width of the line in pixels. */
  private float width;

  /** Height of the line in pixels. */
  private float height;

  public String toString() {
    return characters.toString();
  }
}
