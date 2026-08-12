package com.spinyowl.spinygui.core.diagnostic;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * Explicit, owner-thread diagnostic counts for one operation/sample at a time.
 *
 * <p>The disabled singleton is a stable no-op. Enabled sessions saturate rather than wrap and must
 * be reset by their owner before each sample.
 */
public final class DiagnosticSession {
  private static final Pattern COUNTER_ID =
      Pattern.compile("[a-z][a-z0-9]*(?:-[a-z0-9]+)*(?:\\.[a-z][a-z0-9]*(?:-[a-z0-9]+)*)*");
  private static final DiagnosticSession DISABLED =
      new DiagnosticSession(null, List.of(), Map.of(), new long[0], new boolean[0]);

  private final Thread owner;
  private final List<String> counterIds;
  private final Map<String, Integer> counterIndices;
  private final long[] values;
  private final boolean[] saturated;

  private DiagnosticSession(
      Thread owner,
      List<String> counterIds,
      Map<String, Integer> counterIndices,
      long[] values,
      boolean[] saturated) {
    this.owner = owner;
    this.counterIds = counterIds;
    this.counterIndices = counterIndices;
    this.values = values;
    this.saturated = saturated;
  }

  public static DiagnosticSession disabled() {
    return DISABLED;
  }

  public static DiagnosticSession enabled(Collection<? extends DiagnosticCounter> vocabulary) {
    Objects.requireNonNull(vocabulary, "vocabulary");
    TreeMap<String, DiagnosticCounter> byId = new TreeMap<>();
    for (DiagnosticCounter counter : vocabulary) {
      Objects.requireNonNull(counter, "diagnostic counter");
      String id = Objects.requireNonNull(counter.id(), "diagnostic counter id");
      if (!COUNTER_ID.matcher(id).matches()) {
        throw new IllegalArgumentException("Invalid diagnostic counter id: " + id);
      }
      if (counter.unit() == null) {
        throw new IllegalArgumentException("Diagnostic counter unit is required: " + id);
      }
      if (counter.description() == null || counter.description().isBlank()) {
        throw new IllegalArgumentException("Diagnostic counter description is required: " + id);
      }
      if (byId.putIfAbsent(id, counter) != null) {
        throw new IllegalArgumentException("Duplicate diagnostic counter id: " + id);
      }
    }
    if (byId.isEmpty()) {
      throw new IllegalArgumentException("Enabled diagnostics require a non-empty vocabulary");
    }

    List<String> ids = List.copyOf(byId.keySet());
    Map<String, Integer> indices = new LinkedHashMap<>();
    for (int index = 0; index < ids.size(); index++) indices.put(ids.get(index), index);
    return new DiagnosticSession(
        Thread.currentThread(),
        ids,
        Collections.unmodifiableMap(indices),
        new long[ids.size()],
        new boolean[ids.size()]);
  }

  public boolean enabled() {
    return owner != null;
  }

  public void increment(DiagnosticCounter counter) {
    add(counter, 1);
  }

  public void add(DiagnosticCounter counter, long amount) {
    if (!enabled()) return;
    requireOwner();
    Objects.requireNonNull(counter, "counter");
    if (amount < 0) throw new IllegalArgumentException("Diagnostic increments must not be negative");
    Integer index = counterIndices.get(counter.id());
    if (index == null) {
      throw new IllegalArgumentException("Counter is not registered in this session: " + counter.id());
    }
    long current = values[index];
    if (Long.MAX_VALUE - current < amount) {
      values[index] = Long.MAX_VALUE;
      saturated[index] = true;
    } else {
      values[index] = current + amount;
    }
  }

  public void reset() {
    if (!enabled()) return;
    requireOwner();
    java.util.Arrays.fill(values, 0);
    java.util.Arrays.fill(saturated, false);
  }

  public DiagnosticSnapshot snapshot() {
    if (!enabled()) return DiagnosticSnapshot.EMPTY;
    requireOwner();
    Map<String, Long> captured = new LinkedHashMap<>();
    Set<String> capturedSaturation = new TreeSet<>();
    for (int index = 0; index < counterIds.size(); index++) {
      String id = counterIds.get(index);
      captured.put(id, values[index]);
      if (saturated[index]) capturedSaturation.add(id);
    }
    return new DiagnosticSnapshot(captured, capturedSaturation);
  }

  private void requireOwner() {
    if (Thread.currentThread() != owner) {
      throw new IllegalStateException(
          "Diagnostic sessions are confined to their creating UI/sample thread");
    }
  }
}
