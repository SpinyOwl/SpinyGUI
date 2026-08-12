package com.spinyowl.spinygui.benchmark.frame;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.google.gson.JsonObject;
import java.util.List;

/** Matched capped/uncapped evidence for E6/M1.5 input refresh decisions. */
public record InputImpactEvidenceArtifact(
    int schemaVersion,
    JsonObject run,
    List<Recording> recordings) {
  public static final int SCHEMA_VERSION = 1;

  public InputImpactEvidenceArtifact {
    if (schemaVersion != SCHEMA_VERSION) {
      throw new IllegalArgumentException("Unsupported input-impact evidence schema: " + schemaVersion);
    }
    if (run == null || recordings == null || recordings.isEmpty()) {
      throw new IllegalArgumentException("Input-impact evidence requires run metadata and recordings");
    }
    BenchmarkRunMetadata metadata = BenchmarkRunMetadata.fromJson(run);
    if (metadata.artifact() != BenchmarkRunMetadata.Artifact.INPUT_IMPACT
        || !metadata.baselineEligible()) {
      throw new IllegalArgumentException("Input-impact evidence must be paired timed evidence");
    }
    run = run.deepCopy();
    recordings = List.copyOf(recordings);
  }

  public record Recording(
      String scenario,
      String ratePolicy,
      long measuredFrames,
      long inputBatches,
      long unchangedBatches,
      long refreshRequiredBatches,
      long allocatedBytes,
      long cpuNanos,
      long gcCollections,
      long gcTimeMillis,
      double allocationBytesPerFrame,
      double allocationBytesPerSecond,
      double cpuNanosPerFrame,
      double cpuNanosPerSecond,
      double measuredFramesPerSecond) {
    public Recording {
      if (scenario == null || scenario.isBlank() || ratePolicy == null || ratePolicy.isBlank()) {
        throw new IllegalArgumentException("Input-impact recording identity must not be blank");
      }
      if (measuredFrames <= 0 || inputBatches < 0 || unchangedBatches < 0
          || refreshRequiredBatches < 0 || allocatedBytes < 0 || cpuNanos < 0
          || gcCollections < 0 || gcTimeMillis < 0) {
        throw new IllegalArgumentException("Input-impact recording counts must not be negative");
      }
      if (unchangedBatches + refreshRequiredBatches != inputBatches) {
        throw new IllegalArgumentException("Input-impact decision counts must reconcile");
      }
      if (!Double.isFinite(allocationBytesPerFrame) || allocationBytesPerFrame < 0
          || !Double.isFinite(allocationBytesPerSecond) || allocationBytesPerSecond < 0
          || !Double.isFinite(cpuNanosPerFrame) || cpuNanosPerFrame < 0
          || !Double.isFinite(cpuNanosPerSecond) || cpuNanosPerSecond < 0
          || !Double.isFinite(measuredFramesPerSecond) || measuredFramesPerSecond < 0) {
        throw new IllegalArgumentException("Input-impact derived metrics must be finite and non-negative");
      }
    }
  }

  static InputImpactEvidenceArtifact create(String runId, List<Recording> recordings) {
    return new InputImpactEvidenceArtifact(
        SCHEMA_VERSION,
        BenchmarkInvocationMetadata.timed(
                runId,
                BenchmarkRunMetadata.Artifact.INPUT_IMPACT,
                BenchmarkRunMetadata.Pairing.PAIRED_REPORT)
            .toJson(),
        recordings);
  }
}
