package com.spinyowl.spinygui.benchmark.cpu;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.openjdk.jmh.Main;

/** Runs JMH and enriches its completed JSON result file before the task publishes it. */
public final class CpuBenchmarkMain {
  private CpuBenchmarkMain() {
  }

  public static void main(String[] args) throws Exception {
    Invocation invocation = invocation(args);
    Main.main(invocation.jmhArguments().toArray(String[]::new));
    CpuBenchmarkReport.enrich(invocation.reportPath(), invocation.runMetadata());
  }

  private static Invocation invocation(String[] args) {
    List<String> jmhArguments = new ArrayList<>();
    String runId = null;
    Pairing pairing = null;
    for (int index = 0; index < args.length; index++) {
      if ("--spiny-run-id".equals(args[index])) {
        runId = argument(args, ++index, "--spiny-run-id");
      } else if ("--spiny-pairing".equals(args[index])) {
        pairing = Pairing.fromJson(argument(args, ++index, "--spiny-pairing"));
      } else {
        jmhArguments.add(args[index]);
      }
    }
    Path reportPath = reportPath(jmhArguments);
    if (runId == null || pairing == null) {
      throw new IllegalArgumentException("Expected --spiny-run-id and --spiny-pairing metadata");
    }
    return new Invocation(
        reportPath,
        List.copyOf(jmhArguments),
        BenchmarkInvocationMetadata.timed(runId, Artifact.CPU, pairing));
  }

  private static Path reportPath(List<String> arguments) {
    for (int index = 0; index < arguments.size() - 1; index++) {
      if ("-rff".equals(arguments.get(index))) return Path.of(arguments.get(index + 1));
    }
    throw new IllegalArgumentException("Expected JMH -rff output path for metadata enrichment");
  }

  private static String argument(String[] arguments, int index, String option) {
    if (index >= arguments.length) throw new IllegalArgumentException("Expected value after " + option);
    return arguments[index];
  }

  private record Invocation(
      Path reportPath, List<String> jmhArguments, BenchmarkRunMetadata runMetadata) {
  }
}
