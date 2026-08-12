package com.spinyowl.spinygui.benchmark.rendering;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.InputSet;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import org.junit.jupiter.api.Test;

class RenderingBenchmarkMetadataTest {
  @Test
  void pairedRenderingMetadataAndCurrentWarmupExposuresAgreeWithoutAContext() {
    BenchmarkRunMetadata metadata =
        BenchmarkInvocationMetadata.timed(
            "20260812-120000-000000000",
            BenchmarkRunMetadata.Artifact.RENDERING,
            BenchmarkRunMetadata.Pairing.PAIRED_REPORT);
    RenderingWorkloadSpecifications.Specification specification =
        RenderingWorkloadSpecifications.CURRENT;

    assertTrue(metadata.baselineEligible());
    for (RenderingWorkloadSpecifications.SceneSpecification scene : specification.measurementOrder()) {
      int alternating = specification.alternatingWarmupFrames(scene);
      int validation = specification.validationExposures(scene);
      assertEquals(
          alternating + validation,
          specification.preMeasureExposures(scene));
      assertEquals(
          Integer.toString(alternating),
          specification.executionSettings(scene).get("alternating-warmup-frames-scene"));
      assertEquals(
          Integer.toString(validation),
          specification.executionSettings(scene).get("validation-exposures-scene"));
      assertEquals(
          Integer.toString(alternating + validation),
          specification.executionSettings(scene).get("premeasure-exposures-scene"));
    }
  }

  @Test
  void actualSceneReportSerializesRequiredMetadataForEveryCurrentScene() {
    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.RENDERING, "Vendor", "25", "OS", "1", "x64", "CPU model",
            "GL vendor", "Renderer", "driver", "4.6");
    ComparabilityMetadata.Implementation implementation =
        new ComparabilityMetadata.Implementation("impl-1", "build-1", "commit-1");
    RenderingBenchmarkMain.LatencySummary latency =
        new RenderingBenchmarkMain.LatencySummary(1, 2, 3, 4, 5);

    for (RenderingWorkloadSpecifications.SceneSpecification scene :
        RenderingWorkloadSpecifications.CURRENT.measurementOrder()) {
      ComparabilityMetadata metadata =
          RenderingBenchmarkMain.sceneComparability(scene, environment, implementation);
      int validation = RenderingWorkloadSpecifications.CURRENT.validationExposures(scene);
      RenderingBenchmarkMain.SceneReport report =
          new RenderingBenchmarkMain.SceneReport(
              10,
              scene.textNodeCount(),
              20,
              20,
              2,
              30,
              validation,
              30 + validation,
              200,
              latency,
              latency,
              metadata.toJson());
      JsonObject serialized = JsonParser.parseString(new Gson().toJson(report)).getAsJsonObject();
      ComparabilityMetadata parsed =
          ComparabilityMetadata.fromJson(serialized.getAsJsonObject("comparability"));
      WorkloadIdentity identity = RenderingWorkloadSpecifications.CURRENT.identity(scene);
      InputSet manifests = RenderingWorkloadSpecifications.CURRENT.inputManifests(scene);

      assertEquals(identity.semanticId(), parsed.semanticId());
      assertEquals(RenderingWorkloadSpecifications.CURRENT.executionSettings(scene), parsed.benchmarkSettings());
      assertEquals(implementation, parsed.implementation());
      assertEquals(
          ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
          parsed.evidenceMode());
      assertEquals(30, serialized.get("alternatingWarmupFrameCount").getAsInt());
      assertEquals(validation, serialized.get("validationExposureCount").getAsInt());
      assertEquals(30 + validation, serialized.get("preMeasureExposureCount").getAsInt());
      assertTrue(serialized.has("comparability"));
      assertEquals(
          manifests.content().sha256(),
          serialized.getAsJsonObject("comparability").get("workloadContentSha256").getAsString());
      assertEquals(
          manifests.shape().sha256(),
          serialized.getAsJsonObject("comparability").get("workloadShapeSha256").getAsString());
      assertEquals(
          manifests.fonts().sha256(),
          serialized.getAsJsonObject("comparability").get("fontInputsSha256").getAsString());
      parsed.benchmarkSettings().forEach(
          (key, value) -> {
            if (key.equals("alternating-warmup-frames-pair")) {
              assertEquals(value, identity.dimensions().get(Dimension.WARMUP_FRAMES), key);
            } else if (!key.equals("alternating-warmup-frames-scene")
                && !key.equals("premeasure-exposures-scene")
                && !key.equals("validation-exposures-scene")
                && !key.equals("validation-synchronization")) {
              assertEquals(value, identity.dimensions().get(dimension(key)), key);
            }
          });
    }
  }

  @Test
  void producerManifestsAreGoldenVersionedAndComponentScoped() {
    InputSet manifests =
        RenderingWorkloadSpecifications.CURRENT.inputManifests(
            RenderingWorkloadSpecifications.CURRENT.scene("small"));

    assertEquals("workload-content-v1", manifests.content().schema());
    assertEquals("workload-shape-v1", manifests.shape().schema());
    assertEquals("font-inputs-v1", manifests.fonts().schema());
    assertEquals(
        "sha256:de63a4925da0647096203a7fc0b6d0315bdf635fb62c599e78a70858e15e6a6e",
        manifests.content().sha256());
    assertEquals(
        "sha256:b9551a2109141a7d732c065744eb29021d92c380c7494778aee7a9a6bbd0f800",
        manifests.shape().sha256());
    assertEquals(
        "sha256:4bee894d9b8ba24834afb0e9923395a28cc7c0c110cddb3edbfdad88f7c66ad4",
        manifests.fonts().sha256());
    assertFalse(manifests.shape().canonicalSerialization().contains("warmup-frames"));
    assertFalse(manifests.shape().canonicalSerialization().contains(":workload-content="));
    assertFalse(manifests.shape().canonicalSerialization().contains(":font-chain="));
    assertFalse(manifests.shape().canonicalSerialization().contains("spinygui-benchmark:v1"));
  }

  private static Dimension dimension(String key) {
    return java.util.Arrays.stream(Dimension.values())
        .filter(dimension -> dimension.key().equals(key))
        .findFirst()
        .orElseThrow();
  }
}
