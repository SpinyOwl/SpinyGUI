package com.spinyowl.spinygui.benchmark.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import org.junit.jupiter.api.Test;

class BenchmarkInvocationMetadataTest {
  @Test
  void timedCpuAndRenderingArtifactsAlwaysDisableDiagnostics() {
    for (Artifact artifact : java.util.List.of(Artifact.CPU, Artifact.RENDERING)) {
      for (Pairing pairing : Pairing.values()) {
        BenchmarkRunMetadata metadata =
            BenchmarkInvocationMetadata.timed("20260812-120000-000000000", artifact, pairing);

        assertEquals(EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, metadata.evidenceMode());
        assertEquals(pairing, metadata.pairing());
        assertEquals(
            pairing == Pairing.PAIRED_REPORT,
            metadata.baselineEligible());
      }
    }
  }

  @Test
  void counterDiagnosticsAreAlwaysUntimedUnpairedEvidence() {
    BenchmarkRunMetadata metadata =
        BenchmarkInvocationMetadata.diagnostics("20260812-120000-000000000");

    assertEquals(Artifact.COUNTER_DIAGNOSTICS, metadata.artifact());
    assertEquals(Pairing.UNPAIRED_INVESTIGATION, metadata.pairing());
    assertEquals(EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED, metadata.evidenceMode());
    assertFalse(metadata.baselineEligible());
  }

  @Test
  void timedMetadataRejectsCounterDiagnosticsArtifacts() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            BenchmarkInvocationMetadata.timed(
                "20260812-120000-000000000",
                Artifact.COUNTER_DIAGNOSTICS,
                Pairing.PAIRED_REPORT));
  }
}
