package com.spinyowl.spinygui.benchmark.interaction;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** One self-contained, machine-readable E6/M1.6 interaction evidence artifact. */
public record DiagnosticsInteractionArtifact(
    int schemaVersion,
    String runId,
    String pairing,
    String benchmarkVersion,
    String workloadVersion,
    String frameCounterVocabularyVersion,
    String rendererCounterVocabularyVersion,
    Map<String, String> environment,
    Settings settings,
    List<Recording> recordings) {
  public static final int SCHEMA_VERSION = 1;

  public DiagnosticsInteractionArtifact {
    if (schemaVersion != SCHEMA_VERSION) throw new IllegalArgumentException("Unsupported schema");
    requireText(runId, "runId");
    requireText(pairing, "pairing");
    requireText(benchmarkVersion, "benchmarkVersion");
    requireText(workloadVersion, "workloadVersion");
    requireText(frameCounterVocabularyVersion, "frameCounterVocabularyVersion");
    requireText(rendererCounterVocabularyVersion, "rendererCounterVocabularyVersion");
    environment = Collections.unmodifiableSortedMap(new TreeMap<>(environment));
    if (environment.isEmpty()) throw new IllegalArgumentException("environment is required");
    if (settings == null) throw new IllegalArgumentException("settings are required");
    recordings = List.copyOf(recordings);
    if (recordings.size() != DiagnosticsInteractionScenario.values().length) {
      throw new IllegalArgumentException("Every required interaction scenario must be recorded once");
    }
  }

  public record Settings(
      int rowCount,
      int textNodesPerRow,
      int warmupOperations,
      int measuredOperations,
      boolean visibleWindow,
      boolean vsync,
      boolean frameCap,
      String timingInstrumentation,
      String structuralInstrumentation) {
    public Settings {
      if (rowCount <= 0 || textNodesPerRow <= 0 || warmupOperations < 0 || measuredOperations <= 0) {
        throw new IllegalArgumentException("Invalid interaction evidence settings");
      }
      requireText(timingInstrumentation, "timingInstrumentation");
      requireText(structuralInstrumentation, "structuralInstrumentation");
    }
  }

  public record Recording(
      String scenario,
      String semanticId,
      long p50ElapsedNanos,
      long p95ElapsedNanos,
      long p50AllocatedBytes,
      long p95AllocatedBytes,
      boolean allocationMeasurementAvailable,
      List<Long> elapsedNanosPerOperation,
      List<Long> allocatedBytesPerOperation,
      Map<String, Long> structuralCounters,
      Set<String> saturatedCounterIds) {
    public Recording {
      requireText(scenario, "scenario");
      requireText(semanticId, "semanticId");
      if (p50ElapsedNanos < 0 || p95ElapsedNanos < p50ElapsedNanos
          || p50AllocatedBytes < 0 || p95AllocatedBytes < p50AllocatedBytes) {
        throw new IllegalArgumentException("Percentiles must be ordered and non-negative");
      }
      elapsedNanosPerOperation = List.copyOf(elapsedNanosPerOperation);
      allocatedBytesPerOperation = List.copyOf(allocatedBytesPerOperation);
      if (elapsedNanosPerOperation.isEmpty()
          || elapsedNanosPerOperation.size() != allocatedBytesPerOperation.size()) {
        throw new IllegalArgumentException("Timing and allocation samples must align");
      }
      structuralCounters = Collections.unmodifiableSortedMap(new TreeMap<>(structuralCounters));
      saturatedCounterIds = Collections.unmodifiableSortedSet(new TreeSet<>(saturatedCounterIds));
    }
  }

  private static void requireText(String value, String field) {
    if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
  }
}
