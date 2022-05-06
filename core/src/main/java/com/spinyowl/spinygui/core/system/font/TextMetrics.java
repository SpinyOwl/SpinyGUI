package com.spinyowl.spinygui.core.system.font;

import com.google.common.collect.ImmutableSet;
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

  @Singular private final ImmutableSet<TextLineMetrics> lines;

  private float height;
  private float fullLineHeight;
}
