package com.spinyowl.spinygui.core.system.cache;

import java.util.Objects;

/** Exact line-affecting identity for a wrapped layout. Width is intentionally confined here. */
public record WrappedLayoutKey(
    ResolvedPrimitiveKey primitiveKey,
    float maxWidth,
    float firstLineOffset,
    String verticalMetrics,
    String wrapMode,
    String lineBreakingPolicy) {
  public WrappedLayoutKey {
    primitiveKey = Objects.requireNonNull(primitiveKey, "primitiveKey");
    verticalMetrics = Objects.requireNonNull(verticalMetrics, "verticalMetrics");
    wrapMode = Objects.requireNonNull(wrapMode, "wrapMode");
    lineBreakingPolicy = Objects.requireNonNull(lineBreakingPolicy, "lineBreakingPolicy");
    if (Float.isNaN(maxWidth) || Float.isNaN(firstLineOffset)) throw new IllegalArgumentException("NaN layout input");
  }
}
