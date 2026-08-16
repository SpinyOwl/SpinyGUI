package com.spinyowl.spinygui.benchmark.cpu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.InputSet;
import com.spinyowl.spinygui.benchmark.BenchmarkFontTestOwner;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

class CpuBenchmarkReportTest {
  @BeforeEach
  void installFontOwner() {
    BenchmarkFontTestOwner.install();
  }
  @Test
  void enrichesEveryActualCurrentJmhOperationWithRequiredMetadata() {
    JsonArray report = new JsonArray();
    for (String operation : CpuWorkloadSpecifications.currentOperations().keySet()) {
      JsonObject result = new JsonObject();
      result.addProperty(
          "benchmark", "com.spinyowl.spinygui.benchmark.cpu.TextCalculationBenchmark." + operation);
      result.addProperty("jmhVersion", "1.37");
      report.add(result);
    }
    ComparabilityMetadata.Environment environment =
        new ComparabilityMetadata.Environment(
            ComparabilityMetadata.Scope.CPU, "Vendor", "25", "OS", "1", "x64", "CPU model",
            null, null, null, null);
    ComparabilityMetadata.Implementation implementation =
        new ComparabilityMetadata.Implementation("impl-1", "build-1", "commit-1");

    BenchmarkRunMetadata runMetadata =
        BenchmarkRunMetadata.paired(
            "20260726-120000-000000000",
            BenchmarkRunMetadata.Artifact.CPU,
            ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED);
    CpuBenchmarkReport.enrich(report, environment, implementation, runMetadata);

    assertEquals(CpuWorkloadSpecifications.currentOperations().size(), report.size());
    for (JsonElement element : report) {
      JsonObject result = element.getAsJsonObject();
      String operation = result.get("benchmark").getAsString().replaceFirst("^.*\\.", "");
      WorkloadIdentity identity =
          CpuWorkloadSpecifications.identity(
              CpuWorkloadSpecifications.currentOperations().get(operation));
      InputSet manifests =
          CpuWorkloadSpecifications.inputManifests(
              CpuWorkloadSpecifications.currentOperations().get(operation));
      JsonObject metadataJson = result.getAsJsonObject("comparability");
      ComparabilityMetadata metadata =
          ComparabilityMetadata.fromJson(metadataJson);
      assertEquals(identity.semanticId(), metadata.semanticId());
      assertEquals("jmh-1.37", result.getAsJsonObject("comparability").get("benchmarkVersion").getAsString());
      assertEquals(CpuWorkloadSpecifications.currentExecutionSettings(), metadata.benchmarkSettings());
      assertEquals(implementation, metadata.implementation());
      assertEquals(
          ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
          metadata.evidenceMode());
      assertEquals(runMetadata, BenchmarkRunMetadata.fromJson(result.getAsJsonObject("benchmarkRun")));
      assertFalse(metadata.fingerprints().required().isBlank());
      assertEquals(manifests.content().sha256(), metadataJson.get("workloadContentSha256").getAsString());
      assertEquals(manifests.shape().sha256(), metadataJson.get("workloadShapeSha256").getAsString());
      assertEquals(manifests.fonts().sha256(), metadataJson.get("fontInputsSha256").getAsString());
      metadata.benchmarkSettings().forEach(
          (key, value) -> assertEquals(value, identity.dimensions().get(dimension(key)), key));
    }
  }

  @Test
  void producerManifestsAreGoldenVersionedAndComponentScoped() {
    InputSet manifests =
        CpuWorkloadSpecifications.inputManifests(CpuWorkloadSpecifications.MEASURE_LATIN);

    assertEquals(
        """
        spinygui-benchmark-input:workload-content:v1
        field=4:text=44:The quick brown fox jumps over the lazy dog.
        """,
        manifests.content().canonicalSerialization());
    assertEquals(
        """
        spinygui-benchmark-input:workload-shape:v1
        field=11:line-height=3:1.2
        field=23:measurement-offset-x-px=1:0
        field=10:shape-kind=11:measurement
        field=17:wrap-width-policy=9:unbounded
        field=15:wrapping-policy=9:unwrapped
        """,
        manifests.shape().canonicalSerialization());
    assertEquals(
        """
        spinygui-benchmark-input:font-inputs:v1
        field=26:configuration-font-size-px=2:16
        field=20:font-0000-descriptor=53:Roboto|normal|normal|regular|fonts/Roboto-Regular.ttf
        field=23:font-0000-resource-path=24:fonts/Roboto-Regular.ttf
        field=25:font-0000-resource-sha256=71:sha256:b2efabca5ea4bc56eea829713706b5cd0788b82aca153bd4adde9b1573933b4f
        field=14:font-0000-role=15:cpu-measurement
        """,
        manifests.fonts().canonicalSerialization());
    assertEquals(
        "sha256:d4622aaae4f4e8a29327bda409d25590c9617afc8a28fb62eb7e8c256ecaa834",
        manifests.content().sha256());
    assertEquals(
        "sha256:73fd85c5ea00d6219db21a6e472dba8004d020ae33a2b766002c129c0e9ef1ca",
        manifests.shape().sha256());
    assertEquals(
        "sha256:ad5d9b592114f8fc887393812dc5088dfb2df89497d5381e98a9a250f1230419",
        manifests.fonts().sha256());
    assertFalse(manifests.shape().canonicalSerialization().contains("benchmark-mode"));
    assertFalse(manifests.shape().canonicalSerialization().contains("workload-content"));
    assertFalse(manifests.shape().canonicalSerialization().contains("font-chain"));
  }

  private static Dimension dimension(String key) {
    return java.util.Arrays.stream(Dimension.values())
        .filter(dimension -> dimension.key().equals(key))
        .findFirst()
        .orElseThrow();
  }
}
