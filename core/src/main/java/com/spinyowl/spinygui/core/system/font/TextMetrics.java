package com.spinyowl.spinygui.core.system.font;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Setter(AccessLevel.NONE)
@ToString
@Builder
public final class TextMetrics {

  @Singular private final ImmutableList<TextLineMetrics> lines;

  /** Height of whole text in pixels. */
  private float height;

  /** Height of the line in pixels. In common it is equal to font size multiplied by line height. */
  private float fullLineHeight;
}
