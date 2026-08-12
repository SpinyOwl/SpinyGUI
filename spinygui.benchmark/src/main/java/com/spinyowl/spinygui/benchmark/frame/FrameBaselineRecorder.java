package com.spinyowl.spinygui.benchmark.frame;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.diagnostic.FrameDiagnosticCounter;
import com.spinyowl.spinygui.core.layout.impl.BlockLayout;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.layout.impl.LayoutServiceImpl;
import com.spinyowl.spinygui.core.layout.impl.NoneLayout;
import com.spinyowl.spinygui.core.layout.impl.TextLayoutImpl;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.system.font.FontChainResolver;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import com.spinyowl.spinygui.core.style.manager.StyleManagerImpl;
import com.spinyowl.spinygui.core.style.stylesheet.impl.DefaultPropertyStoreProvider;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.parser.impl.StyleSheetParserFactory;
import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import jdk.jfr.Recording;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedMethod;
import jdk.jfr.consumer.RecordingFile;

/** Captures matched timed evidence for the E6 non-text frame-path scenarios. */
public final class FrameBaselineRecorder {
  static final Set<String> OWNERSHIP_CATEGORIES =
      Set.of(
          "traversal-views",
          "geometry",
          "transforms",
          "selectors",
          "properties",
          "layout",
          "lookup",
          "mutation",
          "text-owned-work");

  private static final long DEFAULT_PROFILE_MILLIS = 500;
  private static final String BASELINE_BEHAVIOR_VERSION = "e6-frame-baseline-1";
  private static final String NO_FONT_INPUTS =
      BenchmarkInputManifests.shape(Map.of("font-policy", "text-owned-by-e5"))
          .sha256();

  private FrameBaselineRecorder() {}

  public enum RatePolicy {
    UNCAPPED("uncapped", 0),
    FPS_120("120hz", 120),
    FPS_60("60hz", 60);

    private final String id;
    private final int hertz;

    RatePolicy(String id, int hertz) {
      this.id = id;
      this.hertz = hertz;
    }

    public String id() {
      return id;
    }

    private long periodNanos() {
      return hertz == 0 ? 0 : 1_000_000_000L / hertz;
    }
  }

  public static FrameBaselineArtifact recordAll(
      String runId, long measurementDurationMillis, boolean collectProfiles) {
    if (measurementDurationMillis <= 0) {
      throw new IllegalArgumentException("Measurement duration must be positive");
    }
    List<FrameBaselineArtifact.Recording> recordings = new ArrayList<>();
    for (FrameScenarioSpecifications.Scenario scenario : FrameScenarioSpecifications.SCENARIOS) {
      for (RatePolicy rate : RatePolicy.values()) {
        recordings.add(record(scenario, rate, measurementDurationMillis, collectProfiles));
      }
    }
    BenchmarkRunMetadata run =
        BenchmarkInvocationMetadata.timed(
            runId,
            BenchmarkRunMetadata.Artifact.FRAME_BASELINE,
            BenchmarkRunMetadata.Pairing.PAIRED_REPORT);
    return new FrameBaselineArtifact(
        FrameBaselineArtifact.SCHEMA_VERSION,
        run.toJson(),
        FrameDiagnosticCounter.VOCABULARY_VERSION,
        recordings,
        review());
  }

  static FrameBaselineArtifact.Recording record(
      FrameScenarioSpecifications.Scenario scenario,
      RatePolicy rate,
      long measurementDurationMillis,
      boolean collectProfiles) {
    Scene scene = Scene.create(scenario);
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(FrameDiagnosticCounter.values()));
    scene.frame.diagnostics(diagnostics);
    for (int index = 0; index < scenario.warmupIterations(); index++) {
      execute(scene);
    }
    diagnostics.reset();
    Metrics metrics = measure(scene, rate, measurementDurationMillis);
    Hotspots hotspots =
        collectProfiles
            ? profile(scene, rate, Math.min(DEFAULT_PROFILE_MILLIS, measurementDurationMillis))
            : Hotspots.disabled();
    return new FrameBaselineArtifact.Recording(
        scenario.name(),
        rate.id(),
        scenario.semanticId(),
        scenario.seriesId() + ":" + rate.id(),
        declaredInputs(scenario, rate, measurementDurationMillis),
        comparability(scenario, rate, measurementDurationMillis).toJson(),
        metrics.frames,
        metrics.elapsedNanos,
        metrics.cpuNanos,
        metrics.allocatedBytes,
        metrics.gcCollections,
        metrics.gcTimeMillis,
        metrics.allocationBytesPerFrame(),
        metrics.allocationBytesPerSecond(),
        metrics.cpuNanosPerFrame(),
        metrics.cpuNanosPerSecond(),
        metrics.measuredFramesPerSecond(),
        diagnostics.snapshot().values(),
        diagnostics.snapshot().saturatedCounterIds(),
        hotspots.methods,
        hotspots.sites,
        hotspots.available,
        hotspots.note);
  }

  private static Metrics measure(Scene scene, RatePolicy rate, long durationMillis) {
    long threadId = Thread.currentThread().threadId();
    com.sun.management.ThreadMXBean threadBean =
        (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
    if (threadBean.isThreadAllocatedMemorySupported()
        && !threadBean.isThreadAllocatedMemoryEnabled()) {
      threadBean.setThreadAllocatedMemoryEnabled(true);
    }
    GcTotals beforeGc = GcTotals.capture();
    long beforeCpu = threadBean.isThreadCpuTimeSupported() ? threadBean.getThreadCpuTime(threadId) : 0;
    long beforeAllocation =
        threadBean.isThreadAllocatedMemorySupported()
            ? threadBean.getThreadAllocatedBytes(threadId)
            : 0;
    long start = System.nanoTime();
    long deadline = start + TimeUnit.MILLISECONDS.toNanos(durationMillis);
    long frames = 0;
    do {
      long frameStart = System.nanoTime();
      execute(scene);
      frames++;
      if (rate.periodNanos() > 0) {
        long remaining = rate.periodNanos() - (System.nanoTime() - frameStart);
        if (remaining > 0) LockSupport.parkNanos(remaining);
      }
    } while (System.nanoTime() < deadline || frames == 0);
    long elapsed = Math.max(1, System.nanoTime() - start);
    long afterCpu = threadBean.isThreadCpuTimeSupported() ? threadBean.getThreadCpuTime(threadId) : beforeCpu;
    long afterAllocation =
        threadBean.isThreadAllocatedMemorySupported()
            ? threadBean.getThreadAllocatedBytes(threadId)
            : beforeAllocation;
    GcTotals afterGc = GcTotals.capture();
    return new Metrics(
        frames,
        elapsed,
        Math.max(0, afterCpu - beforeCpu),
        Math.max(0, afterAllocation - beforeAllocation),
        Math.max(0, afterGc.collections - beforeGc.collections),
        Math.max(0, afterGc.timeMillis - beforeGc.timeMillis));
  }

  private static Hotspots profile(Scene scene, RatePolicy rate, long durationMillis) {
    Path recordingPath = null;
    try (Recording recording = new Recording()) {
      recording.enable("jdk.ExecutionSample").withPeriod(Duration.ofMillis(10));
      recording.enable("jdk.ObjectAllocationSample").withPeriod(Duration.ofMillis(10));
      recording.start();
      long start = System.nanoTime();
      long deadline = start + TimeUnit.MILLISECONDS.toNanos(durationMillis);
      do {
        long frameStart = System.nanoTime();
        execute(scene);
        if (rate.periodNanos() > 0) {
          long remaining = rate.periodNanos() - (System.nanoTime() - frameStart);
          if (remaining > 0) LockSupport.parkNanos(remaining);
        }
      } while (System.nanoTime() < deadline);
      recording.stop();
      recordingPath = Files.createTempFile("spinygui-e6-frame-", ".jfr");
      recording.dump(recordingPath);
      Map<String, Long> methods = new TreeMap<>();
      Map<String, Long> sites = new TreeMap<>();
      try (RecordingFile file = new RecordingFile(recordingPath)) {
        while (file.hasMoreEvents()) {
          RecordedEvent event = file.readEvent();
          RecordedFrame frame = firstFrame(event);
          if (frame == null || frame.getMethod() == null) continue;
          RecordedMethod method = frame.getMethod();
          String methodName = method.getType().getName() + "#" + method.getName();
          String site = methodName + ":" + frame.getLineNumber();
          methods.merge(methodName, 1L, Long::sum);
          sites.merge(site, 1L, Long::sum);
        }
      }
      return new Hotspots(methods, sites, true, "jdk.ExecutionSample and jdk.ObjectAllocationSample");
    } catch (Exception exception) {
      return new Hotspots(
          Map.of(),
          Map.of(),
          false,
          "unavailable: " + exception.getClass().getSimpleName());
    } finally {
      if (recordingPath != null) {
        try {
          Files.deleteIfExists(recordingPath);
        } catch (IOException ignored) {
          // The profile is already represented in the artifact; cleanup is best effort.
        }
      }
    }
  }

  private static RecordedFrame firstFrame(RecordedEvent event) {
    if (event.getStackTrace() == null || event.getStackTrace().getFrames().isEmpty()) return null;
    return event.getStackTrace().getFrames().get(0);
  }

  private static void execute(Scene scene) {
    scene.manager.recalculate(scene.frame);
    FrameEvidenceFixtures.applyRuntimeState(scene.scenario, scene.fixture);
    scene.layout.layout(scene.frame);
    for (Node node : scene.frame.childNodes()) {
      if (node instanceof Element element) {
        element.children();
        element.absolutePosition();
        element.layoutAbsolutePosition();
        element.size();
        element.getIdAttribute();
        element.getClassAttribute();
      }
    }
    if (scene.scenario.hasActiveNode()) {
      scene.frame.getElementById(scene.scenario.name() + "-" + scene.scenario.activeNodeIndex());
    }
  }

  private static Map<String, String> declaredInputs(
      FrameScenarioSpecifications.Scenario scenario, RatePolicy rate, long durationMillis) {
    Map<String, String> values = new TreeMap<>(scenario.declaredInputs());
    values.put("rate-policy", rate.id());
    values.put("measurement-duration-millis", Long.toString(durationMillis));
    values.put("evidence-mode", "timed-allocation-diagnostics-disabled");
    return values;
  }

  private static ComparabilityMetadata comparability(
      FrameScenarioSpecifications.Scenario scenario, RatePolicy rate, long durationMillis) {
    Map<String, String> settings =
        Map.ofEntries(
            Map.entry("benchmark-mode", "e6-frame-baseline"),
            Map.entry("forks", "1"),
            Map.entry("measurement-batch-size", "1"),
            Map.entry("measurement-iterations", "1"),
            Map.entry("measurement-time", durationMillis + "ms/" + rate.id()),
            Map.entry("native-access", "not-required"),
            Map.entry("output-time-unit", "nanoseconds"),
            Map.entry("profiler", "thread-mxbean-and-jfr"),
            Map.entry("state-scope", "benchmark"),
            Map.entry("threads", "1"),
            Map.entry("warmup-batch-size", "1"),
            Map.entry("warmup-forks", "1"),
            Map.entry("warmup-iterations", Integer.toString(scenario.warmupIterations())),
            Map.entry("warmup-time", "scenario-defined"));
    return new ComparabilityMetadata(
        FrameScenarioSpecifications.BENCHMARK_VERSION,
        FrameScenarioSpecifications.WORKLOAD_VERSION,
        "e6-frame-baseline-1",
        BASELINE_BEHAVIOR_VERSION,
        ComparabilityMetadata.EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED,
        scenario.semanticId(),
        "E6 frame baseline: " + scenario.name() + " (" + rate.id() + ")",
        scenario.contentManifest().sha256(),
        scenario.shapeManifest().sha256(),
        NO_FONT_INPUTS,
        com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata.cpuEnvironment(),
        settings,
        com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata.implementation());
  }

  private static FrameBaselineArtifact.Review review() {
    Map<String, String> ownership =
        Map.of(
            "traversal-views", "E6/M2",
            "geometry", "E6/M2",
            "transforms", "E6/M2",
            "selectors", "E6/M3",
            "properties", "E6/M4",
            "layout", "E6/M5",
            "lookup", "E6/M6",
            "mutation", "E6/M6",
            "text-owned-work", "E5/excluded-from-E6");
    return new FrameBaselineArtifact.Review(ownership, List.of(), true, true);
  }

  private record Metrics(
      long frames,
      long elapsedNanos,
      long cpuNanos,
      long allocatedBytes,
      long gcCollections,
      long gcTimeMillis) {
    double allocationBytesPerFrame() {
      return (double) allocatedBytes / frames;
    }

    double allocationBytesPerSecond() {
      return allocatedBytes * 1_000_000_000.0 / elapsedNanos;
    }

    double cpuNanosPerFrame() {
      return (double) cpuNanos / frames;
    }

    double cpuNanosPerSecond() {
      return cpuNanos * 1_000_000_000.0 / elapsedNanos;
    }

    double measuredFramesPerSecond() {
      return frames * 1_000_000_000.0 / elapsedNanos;
    }
  }

  private record GcTotals(long collections, long timeMillis) {
    static GcTotals capture() {
      long collections = 0;
      long timeMillis = 0;
      for (GarbageCollectorMXBean bean : ManagementFactory.getGarbageCollectorMXBeans()) {
        if (bean.getCollectionCount() >= 0) collections += bean.getCollectionCount();
        if (bean.getCollectionTime() >= 0) timeMillis += bean.getCollectionTime();
      }
      return new GcTotals(collections, timeMillis);
    }
  }

  private record Hotspots(
      Map<String, Long> methods,
      Map<String, Long> sites,
      boolean available,
      String note) {
    static Hotspots disabled() {
      return new Hotspots(Map.of(), Map.of(), false, "disabled for focused verification");
    }
  }

  private static final class Scene {
    private final FrameScenarioSpecifications.Scenario scenario;
    private final FrameEvidenceFixtures.Fixture fixture;
    private final Frame frame;
    private final StyleManagerImpl manager;
    private final LayoutServiceImpl layout;

    private Scene(
        FrameScenarioSpecifications.Scenario scenario,
        FrameEvidenceFixtures.Fixture fixture,
        StyleManagerImpl manager,
        LayoutServiceImpl layout) {
      this.scenario = scenario;
      this.fixture = fixture;
      this.frame = fixture.frame();
      this.manager = manager;
      this.layout = layout;
    }

    static Scene create(FrameScenarioSpecifications.Scenario scenario) {
      FrameEvidenceFixtures.Fixture fixture = FrameEvidenceFixtures.build(scenario);
      var propertyStore = new DefaultPropertyStoreProvider().createPropertyStore();
      var parser = StyleSheetParserFactory.createParser(propertyStore);
      fixture
          .frame()
          .styleSheets()
          .add(
              parser.parse(
                  ".e6-panel { display: block; position: static; width: 64px; height: 20px; }"));
      StyleManagerImpl manager =
          new StyleManagerImpl(propertyStore, parser, (element, previous, current) -> {});
      DiagnosticSession textDiagnostics = DiagnosticSession.disabled();
      FontServiceImpl fontService =
          new FontServiceImpl(
              new FontStorageImpl(), false, FontChainResolver.DEFAULT, textDiagnostics);
      InlineFormattingContext inline = new InlineFormattingContext(fontService);
      Map<Display, com.spinyowl.spinygui.core.layout.ElementLayout> layouts = new HashMap<>();
      LayoutServiceImpl layout =
          new LayoutServiceImpl(new TextLayoutImpl(fontService, fontService), layouts);
      layouts.put(Display.NONE, new NoneLayout());
      layouts.put(Display.BLOCK, new BlockLayout(layout, inline, fontService));
      return new Scene(scenario, fixture, manager, layout);
    }
  }
}
