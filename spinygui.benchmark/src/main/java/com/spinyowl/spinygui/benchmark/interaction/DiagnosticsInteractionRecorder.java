package com.spinyowl.spinygui.benchmark.interaction;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticCounter;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSnapshot;
import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/** Warmup/sample recorder that keeps timed allocation measurements separate from counters. */
public final class DiagnosticsInteractionRecorder {
  public static final String BENCHMARK_VERSION = "e6-m1.6-diagnostics-interaction-1";
  public static final String WORKLOAD_VERSION = "text-heavy-diagnostics-panel-1";

  private DiagnosticsInteractionRecorder() {}

  public static DiagnosticsInteractionArtifact recordAll(
      String runId, String pairing, int warmupOperations, int measuredOperations) {
    if (warmupOperations < 0 || measuredOperations <= 0) {
      throw new IllegalArgumentException("Warmup must be non-negative and samples must be positive");
    }
    List<DiagnosticsInteractionArtifact.Recording> recordings = new ArrayList<>();
    for (DiagnosticsInteractionScenario scenario : DiagnosticsInteractionScenario.values()) {
      recordings.add(record(scenario, warmupOperations, measuredOperations));
    }
    var environment = BenchmarkRuntimeMetadata.cpuEnvironment();
    Map<String, String> environmentFields = new TreeMap<>();
    environmentFields.put("jvm-vendor", environment.jvmVendor());
    environmentFields.put("jvm-version", environment.jvmVersion());
    environmentFields.put("os-name", environment.osName());
    environmentFields.put("os-version", environment.osVersion());
    environmentFields.put("os-architecture", environment.osArchitecture());
    environmentFields.put("cpu-model", environment.cpuModel());
    environmentFields.put(
        "implementation-revision",
        BenchmarkRuntimeMetadata.implementation().implementationRevision());
    return new DiagnosticsInteractionArtifact(
        DiagnosticsInteractionArtifact.SCHEMA_VERSION,
        runId,
        pairing,
        BENCHMARK_VERSION,
        WORKLOAD_VERSION,
        FrameDiagnosticCounter.VOCABULARY_VERSION,
        NvgDiagnosticCounter.VOCABULARY_VERSION,
        environmentFields,
        new DiagnosticsInteractionArtifact.Settings(
            DiagnosticsPanelFixture.ROW_COUNT,
            DiagnosticsPanelFixture.TEXT_NODES_PER_ROW,
            warmupOperations,
            measuredOperations,
            false,
            false,
            false,
            "per-operation System.nanoTime and ThreadMXBean allocation; diagnostics disabled",
            "one isolated operation after warmup; diagnostics enabled and excluded from timing"),
        recordings);
  }

  static DiagnosticsInteractionArtifact.Recording record(
      DiagnosticsInteractionScenario scenario, int warmupOperations, int measuredOperations) {
    long[] elapsed = new long[measuredOperations];
    long[] allocated = new long[measuredOperations];
    boolean allocationAvailable;
    try (DiagnosticsPanelFixture fixture =
        new DiagnosticsPanelFixture(scenario, DiagnosticSession.disabled())) {
      for (int index = 0; index < warmupOperations; index++) fixture.execute();
      com.sun.management.ThreadMXBean bean = allocationBean();
      allocationAvailable = bean != null;
      long threadId = Thread.currentThread().threadId();
      for (int index = 0; index < measuredOperations; index++) {
        long allocationBefore = bean == null ? 0 : bean.getThreadAllocatedBytes(threadId);
        long start = System.nanoTime();
        fixture.execute();
        elapsed[index] = Math.max(0, System.nanoTime() - start);
        allocated[index] =
            bean == null ? 0 : Math.max(0, bean.getThreadAllocatedBytes(threadId) - allocationBefore);
      }
    }

    DiagnosticSession diagnostics = DiagnosticSession.enabled(vocabulary());
    DiagnosticSnapshot snapshot;
    try (DiagnosticsPanelFixture fixture = new DiagnosticsPanelFixture(scenario, diagnostics)) {
      for (int index = 0; index < warmupOperations; index++) fixture.execute();
      diagnostics.reset();
      fixture.execute();
      snapshot = diagnostics.snapshot();
    }
    return new DiagnosticsInteractionArtifact.Recording(
        scenario.id(),
        WORKLOAD_VERSION + ":" + scenario.id(),
        percentile(elapsed, 0.50),
        percentile(elapsed, 0.95),
        percentile(allocated, 0.50),
        percentile(allocated, 0.95),
        allocationAvailable,
        boxed(elapsed),
        boxed(allocated),
        snapshot.values(),
        snapshot.saturatedCounterIds());
  }

  static long percentile(long[] samples, double percentile) {
    if (samples.length == 0 || percentile <= 0 || percentile > 1) {
      throw new IllegalArgumentException("Percentile requires samples and (0, 1]");
    }
    long[] ordered = samples.clone();
    Arrays.sort(ordered);
    int index = Math.max(0, (int) Math.ceil(percentile * ordered.length) - 1);
    return ordered[index];
  }

  static List<DiagnosticCounter> vocabulary() {
    LinkedHashSet<DiagnosticCounter> counters =
        Stream.concat(
                Arrays.stream(FrameDiagnosticCounter.values()),
                Arrays.stream(NvgDiagnosticCounter.values()))
            .collect(java.util.stream.Collectors.toCollection(LinkedHashSet::new));
    return List.copyOf(counters);
  }

  private static List<Long> boxed(long[] values) {
    return Arrays.stream(values).boxed().toList();
  }

  private static com.sun.management.ThreadMXBean allocationBean() {
    if (!(ManagementFactory.getThreadMXBean() instanceof com.sun.management.ThreadMXBean bean)
        || !bean.isThreadAllocatedMemorySupported()) {
      return null;
    }
    if (!bean.isThreadAllocatedMemoryEnabled()) bean.setThreadAllocatedMemoryEnabled(true);
    return bean;
  }
}
