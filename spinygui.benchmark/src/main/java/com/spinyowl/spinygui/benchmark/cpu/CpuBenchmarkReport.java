package com.spinyowl.spinygui.benchmark.cpu;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/** Adds required semantic and comparability metadata to the actual JMH JSON output. */
public final class CpuBenchmarkReport {
  private CpuBenchmarkReport() {
  }

  public static void enrich(Path reportPath, BenchmarkRunMetadata runMetadata) throws IOException {
    JsonArray report = com.google.gson.JsonParser.parseString(Files.readString(reportPath)).getAsJsonArray();
    enrich(
        report,
        BenchmarkRuntimeMetadata.cpuEnvironment(),
        BenchmarkRuntimeMetadata.implementation(),
        runMetadata);
    Path temporary = reportPath.resolveSibling(reportPath.getFileName() + ".comparability.tmp");
    Files.writeString(temporary, new GsonBuilder().setPrettyPrinting().create().toJson(report));
    try {
      Files.move(
          temporary,
          reportPath,
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.ATOMIC_MOVE);
    } catch (AtomicMoveNotSupportedException ignored) {
      Files.move(temporary, reportPath, StandardCopyOption.REPLACE_EXISTING);
    }
  }

  static void enrich(
      JsonArray report,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation,
      BenchmarkRunMetadata runMetadata) {
    if (runMetadata.artifact() != BenchmarkRunMetadata.Artifact.CPU) {
      throw new IllegalArgumentException("CPU report requires CPU run metadata");
    }
    if (runMetadata.evidenceMode() != EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED) {
      throw new IllegalArgumentException("Timed CPU report requires diagnostics-disabled run metadata");
    }
    if (report.isEmpty()) throw new IllegalArgumentException("JMH report contains no results");
    for (JsonElement element : report) {
      if (!element.isJsonObject()) {
        throw new IllegalArgumentException("JMH result must be a JSON object");
      }
      JsonObject result = element.getAsJsonObject();
      if (result.has("comparability")) {
        throw new IllegalArgumentException("JMH result already contains comparability metadata");
      }
      if (result.has("benchmarkRun")) {
        throw new IllegalArgumentException("JMH result already contains benchmark run metadata");
      }
      String benchmark = string(result, "benchmark");
      String jmhVersion = string(result, "jmhVersion");
      CpuWorkloadSpecifications.BenchmarkDispatch dispatch =
          CpuWorkloadSpecifications.dispatchForBenchmark(benchmark);
      ComparabilityMetadata metadata =
          CpuWorkloadSpecifications.comparability(
              dispatch.specification(),
              "jmh-" + jmhVersion,
              EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
              environment,
              implementation);
      result.add("comparability", metadata.toJson());
      result.add("benchmarkRun", runMetadata.toJson());
    }
  }

  private static String string(JsonObject object, String name) {
    JsonElement value = object.get(name);
    if (value == null
        || !value.isJsonPrimitive()
        || !value.getAsJsonPrimitive().isString()) {
      throw new IllegalArgumentException("JMH result field must be a JSON string: " + name);
    }
    return value.getAsString();
  }
}
