package com.spinyowl.spinygui.benchmark.frame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FrameScenarioSpecificationsTest {
  @Test
  void matrixContainsStableCollapsedExpandedAndInteractionScenarios() {
    assertEquals(6, FrameScenarioSpecifications.SCENARIOS.size());
    Set<String> semanticIds = new HashSet<>();
    Set<String> seriesIds = new HashSet<>();
    for (FrameScenarioSpecifications.Scenario scenario : FrameScenarioSpecifications.SCENARIOS) {
      assertTrue(semanticIds.add(scenario.semanticId()));
      assertTrue(seriesIds.add(scenario.seriesId()));
      assertEquals(scenario.nodeCount(), scenario.contentManifest().canonicalSerialization().lines().count() - 1);
      assertTrue(scenario.declaredInputs().get("workload-content-sha256").startsWith("sha256:"));
      assertTrue(scenario.declaredInputs().get("workload-shape-sha256").startsWith("sha256:"));
    }
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.COLLAPSED));
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.EXPANDED));
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.POINTER_ACTIVE));
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.SCROLL));
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.RESIZE));
    assertTrue(
        FrameScenarioSpecifications.SCENARIOS.stream()
            .anyMatch(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.TRANSFORM));
  }

  @Test
  void contentAndShapeManifestsChangeWhenDeclaredInputsChange() {
    var collapsed =
        FrameScenarioSpecifications.SCENARIOS.stream()
            .filter(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.COLLAPSED)
            .findFirst()
            .orElseThrow();
    var expanded =
        FrameScenarioSpecifications.SCENARIOS.stream()
            .filter(scenario -> scenario.kind() == FrameScenarioSpecifications.Kind.EXPANDED)
            .findFirst()
            .orElseThrow();

    assertNotEquals(collapsed.contentManifest().sha256(), expanded.contentManifest().sha256());
    assertNotEquals(collapsed.shapeManifest().sha256(), expanded.shapeManifest().sha256());
    assertFalse(collapsed.declaredInputs().equals(expanded.declaredInputs()));
  }

  @Test
  void referenceFixturesAreDeterministicAndPreserveTreeScrollAndTransformContracts() {
    FrameEvidenceFixtures.validateReferenceContracts();
  }
}
