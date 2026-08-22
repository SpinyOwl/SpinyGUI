package com.spinyowl.spinygui.core.system.cache;

import java.util.Objects;

/** Bounded exact-width wrapped-layout cache. */
public final class WrappedLayoutCache implements AutoCloseable {
  private final BoundedTextCache<WrappedLayoutKey, WrappedLayoutValue> delegate;

  public WrappedLayoutCache(int maximumEntries, long maximumWeight, boolean enabled) {
    delegate = new BoundedTextCache<>(maximumEntries, maximumWeight, WrappedLayoutCache::weight, enabled);
  }

  public WrappedLayoutValue get(WrappedLayoutKey key) { return delegate.get(Objects.requireNonNull(key, "key")); }
  public boolean put(WrappedLayoutKey key, WrappedLayoutValue value) {
    return delegate.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
  }
  public void clear() { delegate.clear(); }
  public BoundedTextCache.Stats stats() { return delegate.stats(); }
  public boolean enabled() { return delegate.enabled(); }
  @Override public void close() { delegate.close(); }

  static long weight(WrappedLayoutValue value) {
    return Math.max(1L, value.lines().stream().mapToLong(line -> 24L + line.cumulativeCarets().size() * 4L).sum());
  }
}
