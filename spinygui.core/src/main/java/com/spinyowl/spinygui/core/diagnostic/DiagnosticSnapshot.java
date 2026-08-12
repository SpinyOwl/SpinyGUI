package com.spinyowl.spinygui.core.diagnostic;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

/** Immutable values captured from one diagnostic session at one sample boundary. */
public final class DiagnosticSnapshot {
  static final DiagnosticSnapshot EMPTY = new DiagnosticSnapshot(Map.of(), Set.of(), true);

  private final SortedMap<String, Long> values;
  private final Set<String> saturatedCounterIds;
  private final boolean unknownCountersReadAsZero;

  DiagnosticSnapshot(Map<String, Long> values, Set<String> saturatedCounterIds) {
    this(values, saturatedCounterIds, false);
  }

  private DiagnosticSnapshot(
      Map<String, Long> values,
      Set<String> saturatedCounterIds,
      boolean unknownCountersReadAsZero) {
    this.values = Collections.unmodifiableSortedMap(new TreeMap<>(values));
    this.saturatedCounterIds =
        Collections.unmodifiableSet(new TreeSet<>(saturatedCounterIds));
    this.unknownCountersReadAsZero = unknownCountersReadAsZero;
  }

  public SortedMap<String, Long> values() {
    return values;
  }

  public Set<String> saturatedCounterIds() {
    return saturatedCounterIds;
  }

  public long value(DiagnosticCounter counter) {
    Objects.requireNonNull(counter, "counter");
    if (unknownCountersReadAsZero) return 0;
    Long value = values.get(counter.id());
    if (value == null) throw outsideVocabulary(counter);
    return value;
  }

  public boolean saturated(DiagnosticCounter counter) {
    Objects.requireNonNull(counter, "counter");
    if (unknownCountersReadAsZero) return false;
    if (!values.containsKey(counter.id())) throw outsideVocabulary(counter);
    return saturatedCounterIds.contains(counter.id());
  }

  private static IllegalArgumentException outsideVocabulary(DiagnosticCounter counter) {
    return new IllegalArgumentException(
        "Counter is outside this snapshot's declared vocabulary: " + counter.id());
  }
}
