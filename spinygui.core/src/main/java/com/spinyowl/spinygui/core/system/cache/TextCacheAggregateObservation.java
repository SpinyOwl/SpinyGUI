package com.spinyowl.spinygui.core.system.cache;

import com.spinyowl.spinygui.core.system.font.FontResourceObservation;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.HashMap;

/**
 * Bounded retention evidence for one calculation owner.
 *
 * <p>Java cache values are counted from family statistics, native font ownership is supplied by
 * M3's resource observation, and M5 contributes one current snapshot weight per control. The
 * observation is a value only; it does not retain owners, nodes, entries, or history.
 */
public record TextCacheAggregateObservation(
    Map<String, BoundedTextCache.Stats> families,
    FontResourceObservation nativeResources,
    List<Long> currentSnapshotWeights,
    Map<String, Long> nativeByteWeights,
    Map<String, Long> nativeEntryCounts) {
  public TextCacheAggregateObservation(
      Map<String, BoundedTextCache.Stats> families,
      FontResourceObservation nativeResources,
      List<Long> currentSnapshotWeights) {
    this(
        families,
        nativeResources,
        currentSnapshotWeights,
        Map.of("core-font-bytes", nativeResources.ownerByteCapacity()),
        Map.of());
  }

  public TextCacheAggregateObservation {
    families = Map.copyOf(Objects.requireNonNull(families, "families"));
    nativeResources = Objects.requireNonNull(nativeResources, "nativeResources");
    currentSnapshotWeights = List.copyOf(Objects.requireNonNull(currentSnapshotWeights, "currentSnapshotWeights"));
    nativeByteWeights = Map.copyOf(Objects.requireNonNull(nativeByteWeights, "nativeByteWeights"));
    nativeEntryCounts = Map.copyOf(Objects.requireNonNull(nativeEntryCounts, "nativeEntryCounts"));
    if (nativeByteWeights.values().stream().anyMatch(weight -> weight == null || weight < 0)
        || nativeEntryCounts.values().stream().anyMatch(count -> count == null || count < 0)) {
      throw new IllegalArgumentException("Native resource values must be non-negative");
    }
    if (currentSnapshotWeights.stream().anyMatch(weight -> weight == null || weight < 0)) {
      throw new IllegalArgumentException("Snapshot weights must be non-negative");
    }
  }

  public long javaEntries() {
    return families.values().stream().mapToLong(BoundedTextCache.Stats::entries).sum();
  }

  public long javaRetainedWeight() {
    return families.values().stream().mapToLong(BoundedTextCache.Stats::retainedWeight).sum();
  }

  public long nativeWeight() {
    return nativeByteWeights.values().stream().mapToLong(Long::longValue).sum();
  }

  /** Native resource cardinality, reported separately from byte retention. */
  public long nativeEntryCount() {
    return nativeEntryCounts.values().stream().mapToLong(Long::longValue).sum();
  }

  public long snapshotWeight() {
    return currentSnapshotWeights.stream().mapToLong(Long::longValue).sum();
  }

  public long retainedWeight() {
    return javaRetainedWeight() + nativeWeight() + snapshotWeight();
  }
}
