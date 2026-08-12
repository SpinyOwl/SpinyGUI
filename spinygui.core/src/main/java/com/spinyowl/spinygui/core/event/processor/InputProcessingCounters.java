package com.spinyowl.spinygui.core.event.processor;

import java.util.Objects;

/** Batch-level input decision counters; intended for one UI-thread processor owner. */
public final class InputProcessingCounters {
  private long unchangedBatches;
  private long knownEffectBatches;
  private long unknownFallbackBatches;

  public void record(InputProcessingBatch batch) {
    Objects.requireNonNull(batch, "batch");
    switch (batch.classification()) {
      case PROVEN_UNCHANGED -> unchangedBatches = increment(unchangedBatches);
      case KNOWN_EFFECT -> knownEffectBatches = increment(knownEffectBatches);
      case UNKNOWN_FALLBACK -> unknownFallbackBatches = increment(unknownFallbackBatches);
    }
  }

  public Snapshot snapshot() {
    return new Snapshot(unchangedBatches, knownEffectBatches, unknownFallbackBatches);
  }

  public void reset() {
    unchangedBatches = 0;
    knownEffectBatches = 0;
    unknownFallbackBatches = 0;
  }

  private static long increment(long value) {
    return value == Long.MAX_VALUE ? value : value + 1;
  }

  /** Immutable cumulative batch counts captured at one observation boundary. */
  public record Snapshot(
      long unchangedBatches, long knownEffectBatches, long unknownFallbackBatches) {
    public static final Snapshot EMPTY = new Snapshot(0, 0, 0);

    public Snapshot {
      if (unchangedBatches < 0 || knownEffectBatches < 0 || unknownFallbackBatches < 0) {
        throw new IllegalArgumentException("Input processing counters must not be negative");
      }
    }
  }
}
