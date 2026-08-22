package com.spinyowl.spinygui.core.system.cache;

import java.util.List;
import java.util.Objects;

/** Immutable width-independent identity for a resolved primitive sequence. */
public record ResolvedPrimitiveKey(
    String source,
    List<String> semanticFonts,
    float fontSize,
    String measurementConfiguration,
    String resolutionPolicy) {
  public ResolvedPrimitiveKey {
    source = Objects.requireNonNull(source, "source");
    semanticFonts = List.copyOf(Objects.requireNonNull(semanticFonts, "semanticFonts"));
    measurementConfiguration = Objects.requireNonNull(measurementConfiguration, "measurementConfiguration");
    resolutionPolicy = Objects.requireNonNull(resolutionPolicy, "resolutionPolicy");
    if (!Float.isFinite(fontSize) || fontSize < 0) throw new IllegalArgumentException("fontSize must be finite and non-negative");
  }
}
