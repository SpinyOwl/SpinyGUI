package com.spinyowl.spinygui.benchmark;

import com.spinyowl.spinygui.benchmark.cpu.CpuBenchmarkReport;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.rendering.RenderingBenchmarkMain;
import com.spinyowl.spinygui.benchmark.rendering.RenderingWorkloadSpecifications;
import com.spinyowl.spinygui.benchmark.report.BenchmarkHtmlReportGenerator;
import com.spinyowl.spinygui.core.font.Font;
import java.nio.file.Files;
import java.nio.file.Path;

/** Fresh-JVM probe used by {@link BenchmarkFreshProcessInitializationTest}. */
public final class BenchmarkFreshProcessProbe {
  private BenchmarkFreshProcessProbe() {
  }

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new IllegalArgumentException("Expected one probe mode");
    }
    requireNoOwner("process start");
    switch (args[0]) {
      case "rendering-startup" -> renderingStartup();
      case "report-static" -> reportStaticAccess();
      case "cpu-enrichment" -> cpuEnrichment();
      default -> throw new IllegalArgumentException("Unknown probe mode: " + args[0]);
    }
    requireNoOwner("probe completion");
    System.out.println("BENCHMARK_FRESH_PROCESS_OK " + args[0]);
  }

  private static void renderingStartup() throws Exception {
    var specification = RenderingWorkloadSpecifications.CURRENT;
    var scene = specification.measurementOrder().getFirst();
    specification.identity(scene);
    specification.inputManifests(scene);
    Class.forName(
        RenderingBenchmarkMain.class.getName(),
        true,
        RenderingBenchmarkMain.class.getClassLoader());
    expectArgumentFailure(
        () -> RenderingBenchmarkMain.main(new String[0]), "Expected rendering report path");
  }

  private static void reportStaticAccess() throws Exception {
    Class.forName(
        BenchmarkHtmlReportGenerator.class.getName(),
        true,
        BenchmarkHtmlReportGenerator.class.getClassLoader());
    expectArgumentFailure(
        () -> BenchmarkHtmlReportGenerator.main(new String[0]), "Expected benchmark archive");
  }

  private static void cpuEnrichment() throws Exception {
    Path report = Files.createTempFile("spinygui-cpu-fresh-process-", ".json");
    try {
      Files.writeString(
          report,
          """
          [{
            "benchmark": "com.spinyowl.spinygui.benchmark.cpu.TextCalculationBenchmark.layoutTextDenseInlineContent",
            "jmhVersion": "1.37"
          }]
          """);
      CpuBenchmarkReport.enrich(
          report,
          BenchmarkRunMetadata.paired(
              "20260816-120000-000000000",
              BenchmarkRunMetadata.Artifact.CPU,
              ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED));
      String enriched = Files.readString(report);
      if (!enriched.contains("\"comparability\"")
          || !enriched.contains("\"benchmarkRun\"")) {
        throw new IllegalStateException("CPU report was not enriched");
      }
    } finally {
      Files.deleteIfExists(report);
    }
  }

  private static void expectArgumentFailure(ThrowingRunnable operation, String message)
      throws Exception {
    try {
      operation.run();
      throw new IllegalStateException("Expected argument validation failure");
    } catch (IllegalArgumentException expected) {
      if (!expected.getMessage().contains(message)) {
        throw new IllegalStateException("Unexpected argument validation message", expected);
      }
    }
  }

  private static void requireNoOwner(String stage) {
    if (Font.hasSemanticOwner()) {
      throw new IllegalStateException("Semantic font owner was installed during " + stage);
    }
  }

  @FunctionalInterface
  private interface ThrowingRunnable {
    void run() throws Exception;
  }
}
