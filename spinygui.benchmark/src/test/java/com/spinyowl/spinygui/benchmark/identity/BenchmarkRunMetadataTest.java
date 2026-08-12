package com.spinyowl.spinygui.benchmark.identity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import org.junit.jupiter.api.Test;

class BenchmarkRunMetadataTest {
  @Test
  void pairedTimedMetadataRoundTripsAndIsBaselineEligible() {
    BenchmarkRunMetadata metadata =
        BenchmarkRunMetadata.paired(
            "20260726-120000-000000000",
            Artifact.CPU,
            EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED);

    assertEquals(metadata, BenchmarkRunMetadata.fromJson(metadata.toJson()));
    assertTrue(metadata.baselineEligible());
  }

  @Test
  void counterAndStandaloneInvestigationArtifactsAreNotBaselineEligible() {
    assertFalse(
        BenchmarkRunMetadata.paired(
                "20260726-120000-000000000",
                Artifact.CPU,
                EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED)
            .baselineEligible());
    assertFalse(
        BenchmarkRunMetadata.investigation(
                "20260726-120000-000000000",
                Artifact.COUNTER_DIAGNOSTICS,
                EvidenceMode.COUNTER_ONLY_DIAGNOSTICS_ENABLED)
            .baselineEligible());
    assertFalse(
        BenchmarkRunMetadata.investigation(
                "20260726-120000-000000000",
                Artifact.RENDERING,
                EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED)
            .baselineEligible());
  }

  @Test
  void parserFailsClosedForMissingUnknownAndUnsupportedMetadata() {
    JsonObject valid =
        BenchmarkRunMetadata.paired(
                "20260726-120000-000000000",
                Artifact.CPU,
                EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED)
            .toJson();

    JsonObject missing = valid.deepCopy();
    missing.remove("runId");
    assertThrows(IllegalArgumentException.class, () -> BenchmarkRunMetadata.fromJson(missing));

    JsonObject unknown = valid.deepCopy();
    unknown.addProperty("timestamp", "unstable");
    assertThrows(IllegalArgumentException.class, () -> BenchmarkRunMetadata.fromJson(unknown));

    JsonObject future = valid.deepCopy();
    future.addProperty("schemaVersion", 2);
    assertThrows(IllegalArgumentException.class, () -> BenchmarkRunMetadata.fromJson(future));
  }
}
