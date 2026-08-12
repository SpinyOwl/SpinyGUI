package com.spinyowl.spinygui.benchmark.frame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FrameBaselineRecorderTest {
  @Test
  void shortBaselineMatrixProducesAllMatchedRatesAndRequiredMetrics() {
    FrameBaselineArtifact artifact = FrameBaselineRecorder.recordAll("test-frame-baseline", 1, false);

    assertEquals(18, artifact.recordings().size());
    assertEquals(FrameDiagnosticCounter.VOCABULARY_VERSION, artifact.frameVocabularyVersion());
    assertEquals(
        Set.of(
            "traversal-views",
            "geometry",
            "transforms",
            "selectors",
            "properties",
            "layout",
            "lookup",
            "mutation",
            "text-owned-work"),
        artifact.review().ownership().keySet());
    assertTrue(artifact.review().separatesStableRenderingFromExpansion());
    assertTrue(artifact.review().separatesTextOwnedWork());

    Set<String> series = new HashSet<>();
    for (FrameBaselineArtifact.Recording recording : artifact.recordings()) {
      assertTrue(series.add(recording.seriesId()));
      assertTrue(recording.measuredFrames() > 0);
      assertTrue(recording.elapsedNanos() > 0);
      assertTrue(recording.allocationBytesPerFrame() >= 0);
      assertTrue(recording.allocationBytesPerSecond() >= 0);
      assertTrue(recording.cpuNanosPerFrame() >= 0);
      assertTrue(recording.cpuNanosPerSecond() >= 0);
      assertNotNull(recording.comparability());
      assertFalse(recording.counters().isEmpty());
      assertTrue(recording.counters().containsKey(FrameDiagnosticCounter.LAYOUT_PASSES.id()));
      assertTrue(recording.counters().containsKey(FrameDiagnosticCounter.SELECTOR_TESTS.id()));
      assertTrue(recording.profilerNote().contains("focused verification"));
    }
  }

  @Test
  void disabledProfileIsExplicitlyMarkedRatherThanPresentedAsMissingEvidence() {
    var scenario = FrameScenarioSpecifications.SCENARIOS.get(0);
    FrameBaselineArtifact.Recording recording =
        FrameBaselineRecorder.record(scenario, FrameBaselineRecorder.RatePolicy.UNCAPPED, 1, false);

    assertFalse(recording.profilerAvailable());
    assertEquals("disabled for focused verification", recording.profilerNote());
  }

  @Test
  void differentRateFingerprintsAreMarkedIncomparableBeforeAnyDeltaIsPresented() {
    var scenario = FrameScenarioSpecifications.SCENARIOS.get(0);
    var uncapped = FrameBaselineRecorder.record(scenario, FrameBaselineRecorder.RatePolicy.UNCAPPED, 1, false);
    var capped = FrameBaselineRecorder.record(scenario, FrameBaselineRecorder.RatePolicy.FPS_60, 1, false);

    var comparison = FrameBaselineArtifact.compareFingerprints(uncapped, capped);
    assertFalse(comparison.comparable());
    assertTrue(comparison.reasons().stream().anyMatch(reason -> reason.contains("settings")));
  }

  @Test
  void jfrProfileIsCollectedOrExplicitlyReportsRuntimeUnavailability() {
    var scenario = FrameScenarioSpecifications.SCENARIOS.get(0);
    var recording =
        FrameBaselineRecorder.record(scenario, FrameBaselineRecorder.RatePolicy.UNCAPPED, 1, true);

    assertTrue(recording.profilerNote().startsWith("jdk.")
        || recording.profilerNote().startsWith("unavailable:"));
    if (recording.profilerAvailable()) {
      assertFalse(recording.hotMethods().isEmpty() && recording.hotSites().isEmpty());
    }
  }
}
