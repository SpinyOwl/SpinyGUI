package com.spinyowl.spinygui.core.system.cache;

/** Explicit cache-mode and bound configuration for text calculation owners. */
public record TextCacheConfiguration(
    boolean enabled,
    int fontChainEntries,
    long fontChainWeight,
    int metricsEntries,
    long metricsWeight,
    int preparedEntries,
    long preparedWeight,
    int resolvedEntries,
    long resolvedWeight,
    int wrappedEntries,
    long wrappedWeight,
    int glyphEntries,
    long glyphWeight,
    int advanceEntries,
    long advanceWeight,
    int kerningEntries,
    long kerningWeight) {
  public TextCacheConfiguration {
    if (fontChainEntries < 1 || metricsEntries < 1 || preparedEntries < 1
        || resolvedEntries < 1 || wrappedEntries < 1
        || glyphEntries < 1 || advanceEntries < 1 || kerningEntries < 1) {
      throw new IllegalArgumentException("Cache entry bounds must be positive");
    }
    if (fontChainWeight < 1 || metricsWeight < 1 || preparedWeight < 1
        || resolvedWeight < 1 || wrappedWeight < 1
        || glyphWeight < 1 || advanceWeight < 1 || kerningWeight < 1) {
      throw new IllegalArgumentException("Cache weight bounds must be positive");
    }
  }

  /** Source-compatible constructor retaining the original primitive-cache configuration. */
  public TextCacheConfiguration(
      boolean enabled,
      int glyphEntries,
      long glyphWeight,
      int advanceEntries,
      long advanceWeight,
      int kerningEntries,
      long kerningWeight) {
    this(enabled, 64, 4096, 128, 65536, 64, 256 * 1024L,
        256, 256 * 1024L, 128, 512 * 1024L,
        glyphEntries, glyphWeight, advanceEntries, advanceWeight, kerningEntries, kerningWeight);
  }

  public static TextCacheConfiguration disabled() {
    return new TextCacheConfiguration(false, 64, 4096, 128, 65536, 64, 256 * 1024L,
        256, 256 * 1024L, 128, 512 * 1024L, 4096, 4096, 2048, 16384, 2048, 16384);
  }

  public static TextCacheConfiguration boundedDefaults() {
    return new TextCacheConfiguration(true, 64, 4096, 128, 65536, 64, 256 * 1024L,
        256, 256 * 1024L, 128, 512 * 1024L, 4096, 4096, 2048, 16384, 2048, 16384);
  }
}
