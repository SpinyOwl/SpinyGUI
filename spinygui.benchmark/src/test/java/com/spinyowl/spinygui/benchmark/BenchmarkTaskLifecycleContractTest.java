package com.spinyowl.spinygui.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

/** Build-script contract checks that do not execute JMH, GLFW, or report generation. */
class BenchmarkTaskLifecycleContractTest {
  @Test
  void reportLifecycleSharesOneReservationRunsSequentiallyAndIsAlwaysFresh() throws Exception {
    String script = Files.readString(repositoryRoot().resolve("spinygui.benchmark/build.gradle.kts"));

    assertTrue(script.contains("registerIfAbsent(\"benchmarkRunId\", BenchmarkRunIdService::class)"));
    assertTrue(script.contains("benchmarkReportCpu") && script.contains("benchmarkReportRendering"));
    assertTrue(script.contains("dependsOn(benchmarkReportCpu)"));
    assertTrue(script.contains("mustRunAfter(benchmarkReportCpu)"));
    assertTrue(script.contains("dependsOn(benchmarkReportRendering)"));
    assertTrue(script.contains("inputImpactEvidence"));
    assertTrue(count(script, "benchmarkRunId, \"paired-report\"") == 4);
    assertTrue(count(script, "benchmarkRunId, \"unpaired-investigation\"") == 3);
    assertTrue(script.contains("\"text-calculation\", \"-rff\", benchmarkRunId, \"paired-report\""));
    assertTrue(script.contains("\"nanovg-text\", null, benchmarkRunId, \"paired-report\""));
    assertTrue(script.contains("\"text-diagnostics\", null, benchmarkRunId, \"unpaired-investigation\""));
    assertEquals(8, count(script, "    freshBenchmarkRun()"));
    assertTrue(script.contains("doNotTrackState("));
    assertTrue(script.contains("ArchiveReportArgumentAction(benchmarkArchive.asFile, benchmarkRunId)"));
  }

  private static int count(String value, String token) {
    return value.split(java.util.regex.Pattern.quote(token), -1).length - 1;
  }

  private static Path repositoryRoot() {
    Path current = Path.of("").toAbsolutePath();
    while (current != null) {
      if (Files.exists(current.resolve("settings.gradle.kts"))) return current;
      current = current.getParent();
    }
    throw new IllegalStateException("Unable to locate repository root");
  }
}
