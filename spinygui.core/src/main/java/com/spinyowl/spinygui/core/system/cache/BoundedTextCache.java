package com.spinyowl.spinygui.core.system.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToLongFunction;

/**
 * Owner-thread, bounded cache primitive for text calculation values.
 *
 * <p>The cache deliberately owns no weak-key or background eviction policy. Entries are admitted
 * only when their measured weight fits the configured hard budget, and least-recently-used
 * entries are evicted synchronously on publication. A disabled instance performs no lookup or
 * retention, which makes it suitable for deterministic uncached comparison runs.
 */
public final class BoundedTextCache<K, V> implements AutoCloseable {
  private final Thread owner = Thread.currentThread();
  private final int maximumEntries;
  private final long maximumWeight;
  private final ToLongFunction<V> weigh;
  private final boolean enabled;
  private final LinkedHashMap<K, Entry<V>> entries = new LinkedHashMap<>(16, 0.75f, true);
  private long retainedWeight;
  private long hits;
  private long misses;
  private long admissions;
  private long rejections;
  private long evictions;
  private boolean closed;

  public BoundedTextCache(int maximumEntries, long maximumWeight, ToLongFunction<V> weigh) {
    this(maximumEntries, maximumWeight, weigh, true);
  }

  public BoundedTextCache(
      int maximumEntries, long maximumWeight, ToLongFunction<V> weigh, boolean enabled) {
    if (maximumEntries < 1) throw new IllegalArgumentException("maximumEntries must be positive");
    if (maximumWeight < 1) throw new IllegalArgumentException("maximumWeight must be positive");
    this.maximumEntries = maximumEntries;
    this.maximumWeight = maximumWeight;
    this.weigh = Objects.requireNonNull(weigh, "weigh");
    this.enabled = enabled;
  }

  /** Returns a cached value, counting a miss for both absence and disabled mode. */
  public V get(K key) {
    requireOwner();
    requireOpen();
    Objects.requireNonNull(key, "key");
    if (!enabled) return null;
    Entry<V> entry = entries.get(key);
    if (entry == null) {
      misses++;
      return null;
    }
    hits++;
    return entry.value;
  }

  /** Publishes an immutable value when it fits; oversized values are rejected and never retained. */
  public boolean put(K key, V value) {
    requireOwner();
    requireOpen();
    Objects.requireNonNull(key, "key");
    Objects.requireNonNull(value, "value");
    if (!enabled) return false;
    long weight = weigh.applyAsLong(value);
    if (weight < 0) throw new IllegalArgumentException("Cache weight must not be negative");
    if (weight > maximumWeight) {
      rejections++;
      return false;
    }
    Entry<V> previous = entries.remove(key);
    if (previous != null) retainedWeight -= previous.weight;
    entries.put(key, new Entry<>(value, weight));
    retainedWeight += weight;
    admissions++;
    trim();
    return entries.containsKey(key);
  }

  public void clear() {
    requireOwner();
    requireOpen();
    entries.clear();
    retainedWeight = 0;
  }

  public Stats stats() {
    requireOwner();
    return new Stats(hits, misses, admissions, rejections, evictions, entries.size(), retainedWeight);
  }

  /** Resets hit/miss/admission diagnostics without releasing retained values. */
  public void resetDiagnostics() {
    requireOwner();
    requireOpen();
    hits = 0;
    misses = 0;
    admissions = 0;
    rejections = 0;
    evictions = 0;
  }

  public boolean enabled() {
    return enabled;
  }

  @Override
  public void close() {
    requireOwner();
    if (closed) return;
    entries.clear();
    retainedWeight = 0;
    closed = true;
  }

  private void trim() {
    while (entries.size() > maximumEntries || retainedWeight > maximumWeight) {
      Map.Entry<K, Entry<V>> eldest = entries.entrySet().iterator().next();
      retainedWeight -= eldest.getValue().weight;
      entries.remove(eldest.getKey());
      evictions++;
    }
  }

  private void requireOpen() {
    if (closed) throw new IllegalStateException("Text cache is closed");
  }

  private void requireOwner() {
    if (Thread.currentThread() != owner) {
      throw new IllegalStateException("Text cache access is confined to its owner thread");
    }
  }

  private record Entry<V>(V value, long weight) {}

  public record Stats(
      long hits,
      long misses,
      long admissions,
      long rejections,
      long evictions,
      int entries,
      long retainedWeight) {}
}
