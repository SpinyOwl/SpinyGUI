package com.spinyowl.spinygui.benchmark.interaction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class DiagnosticsInteractionRecorderTest {
  private static final Set<String> REQUIRED_COUNTERS =
      Set.of(
          FrameDiagnosticCounter.HIT_TEST_NODE_VISITS.id(),
          FrameDiagnosticCounter.ENTER_EXIT_EVENTS.id(),
          FrameDiagnosticCounter.STYLE_RECALCULATIONS.id(),
          FrameDiagnosticCounter.SELECTOR_TESTS.id(),
          FrameDiagnosticCounter.PROPERTY_APPLICATIONS.id(),
          FrameDiagnosticCounter.LAYOUT_PASSES.id(),
          FrameDiagnosticCounter.LAYOUT_NODE_VISITS.id(),
          FrameDiagnosticCounter.TRANSFORM_COMPOSITIONS.id(),
          NvgDiagnosticCounter.RENDER_NODE_VISITS.id());

  @Test
  void scenarioVocabularyIsStableAndComplete() {
    assertEquals(
        List.of(
            "stationary-pointer",
            "pointer-move-within-text-node",
            "pointer-cross-text-boundary",
            "paint-only-hover",
            "dimension-affecting-hover",
            "keyboard-only-input",
            "scroll",
            "click-focus",
            "text-editing",
            "resize",
            "unknown-listener-effect"),
        DiagnosticsInteractionScenario.REQUIRED_NAMES);
  }

  @Test
  void nearestRankPercentilesAreDeterministicAndDoNotMutateSamples() {
    long[] samples = {50, 10, 40, 20, 30};

    assertEquals(30, DiagnosticsInteractionRecorder.percentile(samples, 0.50));
    assertEquals(50, DiagnosticsInteractionRecorder.percentile(samples, 0.95));
    assertEquals(50, samples[0]);
  }

  @Test
  void shortHeadlessCaptureContainsEveryScenarioMetricAndRequiredCounter() {
    DiagnosticsInteractionArtifact artifact =
        DiagnosticsInteractionRecorder.recordAll("fixture-test", "paired-report", 1, 1);

    assertEquals(DiagnosticsInteractionScenario.values().length, artifact.recordings().size());
    assertFalse(artifact.settings().visibleWindow());
    assertFalse(artifact.settings().vsync());
    assertFalse(artifact.settings().frameCap());
    assertEquals(256, artifact.settings().rowCount());
    assertEquals(3, artifact.settings().textNodesPerRow());
    artifact.recordings().forEach(
        recording -> {
          assertEquals(1, recording.elapsedNanosPerOperation().size());
          assertEquals(1, recording.allocatedBytesPerOperation().size());
          assertTrue(recording.p95ElapsedNanos() >= recording.p50ElapsedNanos());
          assertTrue(recording.structuralCounters().keySet().containsAll(REQUIRED_COUNTERS));
          assertTrue(recording.structuralCounters().get(NvgDiagnosticCounter.RENDER_NODE_VISITS.id()) >= 0);
        });
    var stationary = recording(artifact, "stationary-pointer");
    assertEquals(0, stationary.structuralCounters().get(FrameDiagnosticCounter.STYLE_RECALCULATIONS.id()));
    assertEquals(0, stationary.structuralCounters().get(FrameDiagnosticCounter.LAYOUT_PASSES.id()));
    assertEquals(0, stationary.structuralCounters().get(NvgDiagnosticCounter.RENDER_NODE_VISITS.id()));
    var paintHover = recording(artifact, "paint-only-hover");
    assertTrue(paintHover.structuralCounters().get(FrameDiagnosticCounter.STYLE_RECALCULATIONS.id()) > 0);
    assertEquals(0, paintHover.structuralCounters().get(FrameDiagnosticCounter.LAYOUT_PASSES.id()));
    assertTrue(paintHover.structuralCounters().get(NvgDiagnosticCounter.RENDER_NODE_VISITS.id()) > 0);
    var dimensionHover = recording(artifact, "dimension-affecting-hover");
    assertTrue(dimensionHover.structuralCounters().get(FrameDiagnosticCounter.LAYOUT_PASSES.id()) > 0);
    var keyboard = recording(artifact, "keyboard-only-input");
    assertEquals(0, keyboard.structuralCounters().get(FrameDiagnosticCounter.STYLE_RECALCULATIONS.id()));
    assertEquals(0, keyboard.structuralCounters().get(FrameDiagnosticCounter.LAYOUT_PASSES.id()));
    var crossing =
        artifact.recordings().stream()
            .filter(recording -> recording.scenario().equals("pointer-cross-text-boundary"))
            .findFirst()
            .orElseThrow();
    assertTrue(crossing.structuralCounters().get(FrameDiagnosticCounter.HIT_TEST_NODE_VISITS.id()) > 0);
    assertTrue(crossing.structuralCounters().get(FrameDiagnosticCounter.ENTER_EXIT_EVENTS.id()) > 0);
  }

  @Test
  void keyboardScenarioRoutesAProductionListenerUnusedKeyAsNoImpact() {
    try (var fixture =
        new DiagnosticsPanelFixture(
            DiagnosticsInteractionScenario.KEYBOARD_ONLY_INPUT, DiagnosticSession.disabled())) {
      var outcome = fixture.execute();

      assertEquals(InputImpact.NO_IMPACT, outcome);
    }
  }

  private static DiagnosticsInteractionArtifact.Recording recording(
      DiagnosticsInteractionArtifact artifact, String scenario) {
    return artifact.recordings().stream()
        .filter(recording -> recording.scenario().equals(scenario))
        .findFirst()
        .orElseThrow();
  }
}
