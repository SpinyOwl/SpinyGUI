package com.spinyowl.spinygui.benchmark.frame;

import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/** Stable declared inputs for the E6 non-text frame-path evidence matrix. */
public final class FrameScenarioSpecifications {
  public static final String BENCHMARK_VERSION = "e6-frame-path-1";
  public static final String WORKLOAD_VERSION = "1";
  public static final List<Scenario> SCENARIOS =
      List.of(
          scenario("collapsed-idle", Kind.COLLAPSED, 1280, 720, 8, -1, List.of("warmup-idle")),
          scenario("expanded-idle", Kind.EXPANDED, 1280, 720, 96, -1, List.of("warmup-idle")),
          scenario(
              "pointer-active",
              Kind.POINTER_ACTIVE,
              1280,
              720,
              48,
              17,
              List.of("warmup-idle", "pointer-move:17", "pointer-move:17")),
          scenario(
              "scroll-active",
              Kind.SCROLL,
              1280,
              720,
              64,
              -1,
              List.of("warmup-idle", "scroll:64")),
          scenario(
              "resize-active",
              Kind.RESIZE,
              1024,
              640,
              48,
              -1,
              List.of("warmup-idle", "resize:1024x640")),
          scenario(
              "transform-active",
              Kind.TRANSFORM,
              1280,
              720,
              40,
              11,
              List.of("warmup-idle", "transform:11")));

  private FrameScenarioSpecifications() {}

  private static Scenario scenario(
      String name,
      Kind kind,
      int frameWidthPx,
      int frameHeightPx,
      int nodeCount,
      int activeNodeIndex,
      List<String> interactionScript) {
    Map<String, String> content = new LinkedHashMap<>();
    for (int index = 0; index < nodeCount; index++) {
      String suffix = String.format(java.util.Locale.ROOT, "%04d", index);
      content.put(
          "node-" + suffix,
          "div|class=e6-panel-" + kind.id + "|position=" + (index % 16) + "," + (index / 16));
    }
    Map<String, String> shape =
        Map.of(
            "category", "non-text-frame",
            "frame-height-px", Integer.toString(frameHeightPx),
            "frame-width-px", Integer.toString(frameWidthPx),
            "kind", kind.id,
            "node-count", Integer.toString(nodeCount),
            "active-node-index", Integer.toString(activeNodeIndex),
            "warmup-iterations", "3",
            "measurement-duration-millis", "10000",
            "interaction-script", String.join("|", interactionScript));
    Map<String, String> declared = new TreeMap<>();
    declared.put("benchmark-version", BENCHMARK_VERSION);
    declared.put("workload-version", WORKLOAD_VERSION);
    declared.put("scenario", name);
    declared.put("scenario-kind", kind.id);
    declared.put("frame-width-px", Integer.toString(frameWidthPx));
    declared.put("frame-height-px", Integer.toString(frameHeightPx));
    declared.put("node-count", Integer.toString(nodeCount));
    declared.put("active-node-index", Integer.toString(activeNodeIndex));
    declared.put("warmup-iterations", "3");
    declared.put("measurement-duration-millis", "10000");
    declared.put("interaction-script", String.join("|", interactionScript));
    BenchmarkInputManifests.Manifest contentManifest = BenchmarkInputManifests.content(content);
    BenchmarkInputManifests.Manifest shapeManifest = BenchmarkInputManifests.shape(shape);
    declared.put("workload-content-sha256", contentManifest.sha256());
    declared.put("workload-shape-sha256", shapeManifest.sha256());
    return new Scenario(
        name,
        kind,
        frameWidthPx,
        frameHeightPx,
        nodeCount,
        activeNodeIndex,
        3,
        10_000,
        interactionScript,
        declared,
        contentManifest,
        shapeManifest);
  }

  public enum Kind {
    COLLAPSED("collapsed"),
    EXPANDED("expanded"),
    POINTER_ACTIVE("pointer-active"),
    SCROLL("scroll"),
    RESIZE("resize"),
    TRANSFORM("transform");

    private final String id;

    Kind(String id) {
      this.id = id;
    }
  }

  public record Scenario(
      String name,
      Kind kind,
      int frameWidthPx,
      int frameHeightPx,
      int nodeCount,
      int activeNodeIndex,
      int warmupIterations,
      int measurementDurationMillis,
      List<String> interactionScript,
      Map<String, String> declaredInputs,
      BenchmarkInputManifests.Manifest contentManifest,
      BenchmarkInputManifests.Manifest shapeManifest) {
    public Scenario {
      Objects.requireNonNull(name, "name");
      Objects.requireNonNull(kind, "kind");
      if (frameWidthPx <= 0 || frameHeightPx <= 0 || nodeCount <= 0) {
        throw new IllegalArgumentException("Frame scenario dimensions and node count must be positive");
      }
      if (activeNodeIndex < -1 || activeNodeIndex >= nodeCount) {
        throw new IllegalArgumentException("Active node index is outside the scenario node range");
      }
      if (warmupIterations <= 0 || measurementDurationMillis <= 0) {
        throw new IllegalArgumentException("Scenario warmup and duration must be positive");
      }
      interactionScript = List.copyOf(interactionScript);
      declaredInputs = Collections.unmodifiableMap(new TreeMap<>(declaredInputs));
      Objects.requireNonNull(contentManifest, "contentManifest");
      Objects.requireNonNull(shapeManifest, "shapeManifest");
    }

    public String semanticId() {
      return "e6.frame." + name;
    }

    public String seriesId() {
      return "e6-frame-path:" + name;
    }

    public String kindId() {
      return kind.id;
    }

    public boolean hasActiveNode() {
      return activeNodeIndex >= 0;
    }
  }
}
