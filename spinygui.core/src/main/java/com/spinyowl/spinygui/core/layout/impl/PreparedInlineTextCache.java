package com.spinyowl.spinygui.core.layout.impl;

import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.system.cache.BoundedTextCache;
import java.util.Objects;

/** Pass-owned bounded reuse for M4 preparation values; it never retains a DOM node. */
final class PreparedInlineTextCache implements AutoCloseable {
  private final boolean enabled;
  private final BoundedTextCache<Key, PreparedInlineText> cache;

  PreparedInlineTextCache() {
    this(true);
  }

  PreparedInlineTextCache(boolean enabled) {
    this(enabled, 64, 256 * 1024L);
  }

  PreparedInlineTextCache(boolean enabled, int maximumEntries, long maximumWeight) {
    this.enabled = enabled;
    cache =
        new BoundedTextCache<>(
            maximumEntries, maximumWeight, PreparedInlineTextCache::weight, enabled);
  }

  PreparedInlineText getOrPrepare(String source, ResolvedStyle style, DiagnosticSession diagnostics) {
    if (!enabled) {
      return PreparedInlineText.prepare(source, style, diagnostics);
    }
    Key key = Key.of(source, style);
    PreparedInlineText cached = cache.get(key);
    if (cached != null) return cached;
    PreparedInlineText prepared = PreparedInlineText.prepare(source, style, diagnostics);
    cache.put(key, prepared);
    return prepared;
  }

  BoundedTextCache.Stats stats() {
    return cache.stats();
  }

  void clear() {
    cache.clear();
  }

  void resetDiagnostics() {
    cache.resetDiagnostics();
  }

  @Override
  public void close() {
    cache.close();
  }

  private static long weight(PreparedInlineText value) {
    long units = value.units().size();
    return Math.max(
        1,
        (long) value.source().length() * 2 + (long) value.text().length() * 2 + units * 24);
  }

  private record Key(String source, WhiteSpace whiteSpace, int tabSize) {
    private Key {
      source = Objects.requireNonNull(source, "source");
      whiteSpace = Objects.requireNonNull(whiteSpace, "whiteSpace");
      if (tabSize < 1) throw new IllegalArgumentException("tabSize must be positive");
    }

    private static Key of(String source, ResolvedStyle style) {
      int tabSize = Math.max(1, style.tabSize() == null ? 4 : style.tabSize());
      return new Key(source, style.whiteSpace(), tabSize);
    }
  }
}
