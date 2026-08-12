package com.spinyowl.spinygui.benchmark.identity;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;

/** Fixed evidence-mode ownership assigned by the benchmark producer entry points. */
public final class BenchmarkInvocationMetadata {
  private BenchmarkInvocationMetadata() {}

  public static BenchmarkRunMetadata timed(String runId, Artifact artifact, Pairing pairing) {
    if (artifact != Artifact.CPU && artifact != Artifact.RENDERING) {
      throw new IllegalArgumentException("Timed benchmark artifacts must be CPU or rendering");
    }
    return new BenchmarkRunMetadata(
        BenchmarkRunMetadata.SCHEMA_VERSION,
        runId,
        artifact,
        pairing,
        EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED);
  }

  public static BenchmarkRunMetadata diagnostics(String runId) {
    return BenchmarkRunMetadata.investigation(
        runId, Artifact.COUNTER_DIAGNOSTICS, EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED);
  }
}
