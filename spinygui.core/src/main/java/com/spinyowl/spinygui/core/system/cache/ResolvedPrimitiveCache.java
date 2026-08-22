package com.spinyowl.spinygui.core.system.cache;

import java.util.Objects;
import java.util.function.ToLongFunction;

/** Bounded owner-thread cache for width-independent resolved primitives. */
public final class ResolvedPrimitiveCache implements AutoCloseable {
  private final BoundedTextCache<ResolvedPrimitiveKey, ResolvedPrimitiveValue> delegate;

  public ResolvedPrimitiveCache(int maximumEntries, long maximumWeight, boolean enabled) {
    delegate = new BoundedTextCache<>(maximumEntries, maximumWeight, ResolvedPrimitiveCache::weight, enabled);
  }

  public ResolvedPrimitiveValue get(ResolvedPrimitiveKey key) { return delegate.get(Objects.requireNonNull(key, "key")); }

  public boolean put(ResolvedPrimitiveKey key, ResolvedPrimitiveValue value) {
    return delegate.put(Objects.requireNonNull(key, "key"), Objects.requireNonNull(value, "value"));
  }

  public void clear() { delegate.clear(); }
  public BoundedTextCache.Stats stats() { return delegate.stats(); }
  public boolean enabled() { return delegate.enabled(); }
  @Override public void close() { delegate.close(); }

  static long weight(ResolvedPrimitiveValue value) {
    return Math.max(1L, value.primitives().stream().mapToLong(p -> 40L + p.kerningInputs().size() * 24L).sum());
  }
}
