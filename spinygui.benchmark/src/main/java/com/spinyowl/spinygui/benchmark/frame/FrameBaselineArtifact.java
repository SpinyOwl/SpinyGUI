package com.spinyowl.spinygui.benchmark.frame;

import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** Comparable timed evidence for the E6 non-text frame path. */
public record FrameBaselineArtifact(
    int schemaVersion,
    JsonObject benchmarkRun,
    String frameVocabularyVersion,
    List<Recording> recordings,
    Review review) {
  public static final int SCHEMA_VERSION = 1;

  public FrameBaselineArtifact {
    if (schemaVersion != SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported frame baseline schema: " + schemaVersion);
    }
    BenchmarkRunMetadata run = BenchmarkRunMetadata.fromJson(benchmarkRun);
    if (run.artifact() != BenchmarkRunMetadata.Artifact.FRAME_BASELINE
        || !run.baselineEligible()) {
      throw new IllegalArgumentException("Frame baseline must be paired timed evidence");
    }
    if (frameVocabularyVersion == null || frameVocabularyVersion.isBlank()) {
      throw new IllegalArgumentException("Frame diagnostic vocabulary version is required");
    }
    benchmarkRun = benchmarkRun.deepCopy();
    recordings = List.copyOf(recordings);
    if (recordings.isEmpty()) {
      throw new IllegalArgumentException("Frame baseline requires at least one recording");
    }
    Set<String> identities = new TreeSet<>();
    for (Recording recording : recordings) {
      if (!identities.add(recording.seriesId())) {
        throw new IllegalArgumentException("Duplicate frame baseline series: " + recording.seriesId());
      }
    }
    Objects.requireNonNull(review, "review");
  }

  @Override
  public JsonObject benchmarkRun() {
    return benchmarkRun.deepCopy();
  }

  /** Compares equality-bearing fingerprints and returns reasons when a delta is not valid. */
  public static ComparabilityMetadata.Comparison compareFingerprints(
      Recording first, Recording second) {
    return ComparabilityMetadata.fromJson(first.comparability())
        .compare(ComparabilityMetadata.fromJson(second.comparability()));
  }

  public record Recording(
      String scenarioName,
      String ratePolicy,
      String semanticId,
      String seriesId,
      Map<String, String> declaredInputs,
      JsonObject comparability,
      long measuredFrames,
      long elapsedNanos,
      long cpuNanos,
      long allocatedBytes,
      long gcCollections,
      long gcTimeMillis,
      double allocationBytesPerFrame,
      double allocationBytesPerSecond,
      double cpuNanosPerFrame,
      double cpuNanosPerSecond,
      double measuredFramesPerSecond,
      Map<String, Long> counters,
      Set<String> saturatedCounterIds,
      Map<String, Long> hotMethods,
      Map<String, Long> hotSites,
      boolean profilerAvailable,
      String profilerNote) {
    public Recording {
      requireText(scenarioName, "scenarioName");
      requireText(ratePolicy, "ratePolicy");
      requireText(semanticId, "semanticId");
      requireText(seriesId, "seriesId");
      declaredInputs = Collections.unmodifiableSortedMap(new TreeMap<>(declaredInputs));
      ComparabilityMetadata metadata = ComparabilityMetadata.fromJson(comparability);
      if (!metadata.semanticId().equals(semanticId)) {
        throw new IllegalArgumentException("Frame recording identity disagrees with comparability");
      }
      comparability = comparability.deepCopy();
      if (measuredFrames <= 0 || elapsedNanos <= 0 || cpuNanos < 0 || allocatedBytes < 0
          || gcCollections < 0 || gcTimeMillis < 0) {
        throw new IllegalArgumentException("Frame recording metrics must be non-negative and measured");
      }
      if (!Double.isFinite(allocationBytesPerFrame) || allocationBytesPerFrame < 0
          || !Double.isFinite(allocationBytesPerSecond) || allocationBytesPerSecond < 0
          || !Double.isFinite(cpuNanosPerFrame) || cpuNanosPerFrame < 0
          || !Double.isFinite(cpuNanosPerSecond) || cpuNanosPerSecond < 0
          || !Double.isFinite(measuredFramesPerSecond) || measuredFramesPerSecond < 0) {
        throw new IllegalArgumentException("Derived frame metrics must be finite and non-negative");
      }
      counters = nonNegativeCounters(counters, "counters");
      saturatedCounterIds = Collections.unmodifiableSortedSet(new TreeSet<>(saturatedCounterIds));
      if (!counters.keySet().containsAll(saturatedCounterIds)) {
        throw new IllegalArgumentException("Saturated counter IDs must be recorded counters");
      }
      hotMethods = nonNegativeCounters(hotMethods, "hotMethods");
      hotSites = nonNegativeCounters(hotSites, "hotSites");
      requireText(profilerNote, "profilerNote");
    }

    private static Map<String, Long> nonNegativeCounters(
        Map<String, Long> values, String field) {
      Objects.requireNonNull(values, field);
      if (values.entrySet().stream().anyMatch(entry -> entry.getKey() == null
          || entry.getKey().isBlank() || entry.getValue() == null || entry.getValue() < 0)) {
        throw new IllegalArgumentException(field + " must contain non-negative named values");
      }
      return Collections.unmodifiableSortedMap(new TreeMap<>(values));
    }
  }

  public record Review(
      Map<String, String> ownership,
      List<String> unexpectedFindings,
      boolean separatesStableRenderingFromExpansion,
      boolean separatesTextOwnedWork) {
    public Review {
      ownership = Collections.unmodifiableSortedMap(new TreeMap<>(ownership));
      unexpectedFindings = List.copyOf(unexpectedFindings);
      if (!ownership.keySet().equals(FrameBaselineRecorder.OWNERSHIP_CATEGORIES)) {
        throw new IllegalArgumentException("Frame baseline ownership review is incomplete");
      }
      if (!separatesStableRenderingFromExpansion || !separatesTextOwnedWork) {
        throw new IllegalArgumentException("Frame baseline review did not confirm required boundaries");
      }
    }
  }

  private static void requireText(String value, String field) {
    if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
  }
}
