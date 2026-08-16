package com.spinyowl.spinygui.benchmark.rendering;

import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;

import com.spinyowl.spinygui.benchmark.TextStyleSpecification;
import com.spinyowl.spinygui.benchmark.TextWorkloads;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.FontInput;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInputManifests.InputSet;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Category;
import com.spinyowl.spinygui.benchmark.identity.WorkloadIdentity.Dimension;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Canonical current renderer setup and scene pair consumed by execution and identity. */
public final class RenderingWorkloadSpecifications {
  public static final Specification CURRENT =
      new Specification(
          new WindowSpecification(1280, 720, false, false, 0),
          new ContainerSpecification(20, 20, 1240, 680),
          new TextStyleSpecification(
              List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
              List.of(
                  Font.ROBOTO_REGULAR,
                  Font.ROBOTO_LIGHT,
                  Font.ROBOTO_BOLD,
                  Font.NOTO_SANS_CJK_SC_REGULAR),
              FontStyle.NORMAL,
              FontWeight.NORMAL,
              FontStretch.NORMAL,
              16,
              1.2f,
              Color.WHITE,
              Display.BLOCK,
              Position.STATIC,
              WhiteSpace.NORMAL,
              TextAlign.LEFT,
              OverflowWrap.NORMAL,
              WordBreak.NORMAL,
              4),
          List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
          TextWorkloads.MIXED_CJK.replace(" ", ""),
          "mixed-cjk-remove-ascii-spaces-v1",
          List.of(TextWorkloads.LATIN, TextWorkloads.MIXED_CJK),
          "alternating-latin-mixed-cjk-v1",
          "remove-ascii-spaces",
          List.of(new SceneSpecification("small", 100), new SceneSpecification("large", 1_000)),
          0,
          60,
          200,
          true,
          new ClearSpecification(
              true, 0, 0, 0, 1, GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT),
          true,
          new StructuralValidationSpecification(true, "small", "production-command-recording-v1"));

  private RenderingWorkloadSpecifications() {
  }

  public record WindowSpecification(
      int widthPx, int heightPx, boolean visible, boolean resizable, int swapInterval) {

    public WindowSpecification {
      if (widthPx <= 0 || heightPx <= 0 || swapInterval < 0) {
        throw new IllegalArgumentException("Invalid renderer window specification");
      }
    }
  }

  public record ContainerSpecification(
      float positionXPx, float positionYPx, float widthPx, float heightPx) {

    public ContainerSpecification {
      if (widthPx < 0 || heightPx < 0) {
        throw new IllegalArgumentException("Container dimensions cannot be negative");
      }
    }
  }

  public record SceneSpecification(String name, int textNodeCount) {

    public SceneSpecification {
      Objects.requireNonNull(name, "name");
      if (textNodeCount <= 0) {
        throw new IllegalArgumentException("textNodeCount must be positive");
      }
    }
  }

  public record ClearSpecification(
      boolean enabled, float red, float green, float blue, float alpha, int mask) {

    public ClearSpecification {
      if (!Float.isFinite(red)
          || !Float.isFinite(green)
          || !Float.isFinite(blue)
          || !Float.isFinite(alpha)) {
        throw new IllegalArgumentException("Clear color components must be finite");
      }
    }

    String identityPolicy() {
      if (!enabled) {
        return "none";
      }
      if (Float.compare(red, 0) != 0
          || Float.compare(green, 0) != 0
          || Float.compare(blue, 0) != 0
          || Float.compare(alpha, 1) != 0
          || mask != (GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT)) {
        throw new IllegalArgumentException("Unsupported renderer clear specification");
      }
      return "color-stencil-before-sample";
    }
  }

  public record StructuralValidationSpecification(
      boolean enabled, String sceneName, String commandContract) {

    public StructuralValidationSpecification {
      Objects.requireNonNull(sceneName, "sceneName");
      Objects.requireNonNull(commandContract, "commandContract");
    }

    String identityPolicy() {
      if (!enabled) return "none";
      if (!"small".equals(sceneName)
          || !"production-command-recording-v1".equals(commandContract)) {
        throw new IllegalArgumentException("Unsupported renderer structural validation specification");
      }
      return "small-scene-production-command-recording-before-measurement";
    }
  }

  public record Specification(
      WindowSpecification window,
      ContainerSpecification container,
      TextStyleSpecification style,
      List<Font> prewarmFonts,
      String prewarmText,
      String prewarmWorkloadContent,
      List<String> sourceContent,
      String workloadContent,
      String contentTransform,
      List<SceneSpecification> measurementOrder,
      float inlineLayoutStartYPx,
      int warmupFrames,
      int measuredFrames,
      boolean roundToPixel,
      ClearSpecification clear,
      boolean synchronizeWithGlFinish,
      StructuralValidationSpecification structuralValidation) {

    public Specification {
      Objects.requireNonNull(window, "window");
      Objects.requireNonNull(container, "container");
      Objects.requireNonNull(style, "style");
      prewarmFonts = List.copyOf(prewarmFonts);
      sourceContent = List.copyOf(sourceContent);
      measurementOrder = List.copyOf(measurementOrder);
      Objects.requireNonNull(prewarmText, "prewarmText");
      Objects.requireNonNull(prewarmWorkloadContent, "prewarmWorkloadContent");
      Objects.requireNonNull(workloadContent, "workloadContent");
      Objects.requireNonNull(contentTransform, "contentTransform");
      Objects.requireNonNull(clear, "clear");
      Objects.requireNonNull(structuralValidation, "structuralValidation");
      clear.identityPolicy();
      structuralValidation.identityPolicy();
      if (sourceContent.isEmpty() || measurementOrder.size() != 2) {
        throw new IllegalArgumentException("Current renderer requires content and exactly two scenes");
      }
      if (!measurementOrder.stream().map(SceneSpecification::name).toList()
              .equals(List.of("small", "large"))
          || measurementOrder.get(0).textNodeCount() >= measurementOrder.get(1).textNodeCount()) {
        throw new IllegalArgumentException(
            "Current renderer requires the smaller scene before the larger scene");
      }
      if (warmupFrames < 0 || measuredFrames <= 0 || !Float.isFinite(inlineLayoutStartYPx)) {
        throw new IllegalArgumentException("Invalid renderer execution counts or layout start");
      }
      if (measurementOrder.stream()
          .noneMatch(scene -> scene.name().equals(structuralValidation.sceneName()))) {
        throw new IllegalArgumentException("Validation scene is not in the measurement order");
      }
      if (structuralValidation.enabled() && !"small".equals(structuralValidation.sceneName())) {
        throw new IllegalArgumentException("Current structural validation must use the small scene");
      }
      if ("alternating-latin-mixed-cjk-v1".equals(workloadContent)
          && (!sourceContent.equals(List.of(TextWorkloads.LATIN, TextWorkloads.MIXED_CJK))
              || !"remove-ascii-spaces".equals(contentTransform))) {
        throw new IllegalArgumentException(
            "Rendering content does not match alternating-latin-mixed-cjk-v1");
      }
      if ("mixed-cjk-remove-ascii-spaces-v1".equals(prewarmWorkloadContent)
          && !TextWorkloads.MIXED_CJK.replace(" ", "").equals(prewarmText)) {
        throw new IllegalArgumentException(
            "Renderer prewarm text does not match mixed-cjk-remove-ascii-spaces-v1");
      }
      requireSameFonts(style.orderedFonts(), prewarmFonts, "prewarm");
      if (style.resolvedFonts().isEmpty()) {
        throw new IllegalArgumentException("Inline layout font chain cannot be empty");
      }
    }

    public String transformedContent(int nodeIndex) {
      String source = sourceContent.get(Math.floorMod(nodeIndex, sourceContent.size()));
      return switch (contentTransform) {
        case "remove-ascii-spaces" -> source.replace(" ", "");
        case "none" -> source;
        default -> throw new IllegalStateException("Unsupported content transform: " + contentTransform);
      };
    }

    public FontServiceImpl createFontService() {
      return createFontService(DiagnosticSession.disabled());
    }

    public FontServiceImpl createFontService(DiagnosticSession diagnostics) {
      FontServiceImpl service =
          new FontServiceImpl(
              new FontStorageImpl(),
              roundToPixel,
              Objects.requireNonNull(diagnostics, "diagnostics"));
      service.installSemanticOwner();
      style.verifyResolution(service.fontChainResolver());
      return service;
    }

    public SceneSpecification scene(String name) {
      return measurementOrder.stream()
          .filter(scene -> scene.name().equals(name))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unknown renderer scene: " + name));
    }

    public List<Font> layoutFonts() {
      return style.resolvedFonts();
    }

    public List<String> fontExecutionIdentities() {
      List<String> identities = new java.util.ArrayList<>();
      prewarmFonts.stream()
          .map(TextStyleSpecification::fontObjectIdentity)
          .map(identity -> "prewarm=" + identity)
          .forEach(identities::add);
      layoutFonts().stream()
          .map(TextStyleSpecification::fontObjectIdentity)
          .map(identity -> "layout=" + identity)
          .forEach(identities::add);
      return List.copyOf(identities);
    }

    public WorkloadIdentity identity(SceneSpecification scene) {
      var requiredDimensions =
          WorkloadIdentity.requiredDimensions(Category.NORMAL_TEXT, "render-text");
      Map<Dimension, Object> dimensions = identityDimensions(scene);
      WorkloadIdentity.Builder builder = WorkloadIdentity.e5("renderer-text");
      for (Dimension dimension : requiredDimensions) {
        Object value = dimensions.get(dimension);
        if (value == null) {
          throw new IllegalStateException(
              "Missing producer identity dimension " + dimension.key() + " for " + scene.name());
        }
        builder.dimension(dimension, value);
      }
      return builder.build("Rendering " + scene.name());
    }

    public Map<String, String> executionSettings(SceneSpecification scene) {
      int orderIndex = measurementOrder.indexOf(scene);
      if (orderIndex < 0) {
        throw new IllegalArgumentException("Scene is not part of the renderer measurement pair");
      }
      return Map.ofEntries(
          Map.entry(
              "alternating-warmup-frames-scene",
              Integer.toString(alternatingWarmupFrames(scene))),
          Map.entry("alternating-warmup-frames-pair", Integer.toString(warmupFrames)),
          Map.entry("clear-policy", clear.identityPolicy()),
          Map.entry("context-visibility", window.visible() ? "visible" : "hidden"),
          Map.entry("measured-frames", Integer.toString(measuredFrames)),
          Map.entry("measurement-order", measurementOrderIdentity()),
          Map.entry("measurement-order-index", Integer.toString(orderIndex + 1)),
          Map.entry("native-access", "all-unnamed"),
          Map.entry(
              "premeasure-sequence",
              structuralValidation.enabled()
                  ? "alternating-small-large-plus-small-structural-validation"
                  : "none"),
          Map.entry("premeasure-exposures-scene", Integer.toString(preMeasureExposures(scene))),
          Map.entry("swap-interval", Integer.toString(window.swapInterval())),
          Map.entry("synchronization", synchronizeWithGlFinish ? "gl-finish" : "none"),
          Map.entry("validation-policy", structuralValidation.identityPolicy()),
          Map.entry("validation-exposures-scene", Integer.toString(validationExposures(scene))),
          Map.entry(
              "validation-synchronization",
              structuralValidation.enabled()
                  ? "render-and-gl-finish-then-production-command-recording"
                  : "none"),
          Map.entry("warmup-order", "alternating-small-large-starting-small"),
          Map.entry("window-resizable", Boolean.toString(window.resizable())));
    }

    public int alternatingWarmupFrames(SceneSpecification scene) {
      int orderIndex = measurementOrder.indexOf(scene);
      if (orderIndex < 0) {
        throw new IllegalArgumentException("Scene is not part of the renderer measurement pair");
      }
      return warmupFrames / measurementOrder.size()
          + (orderIndex < warmupFrames % measurementOrder.size() ? 1 : 0);
    }

    public int validationExposures(SceneSpecification scene) {
      if (!measurementOrder.contains(scene)) {
        throw new IllegalArgumentException("Scene is not part of the renderer measurement pair");
      }
      return structuralValidation.enabled()
              && structuralValidation.sceneName().equals(scene.name())
          ? 1
          : 0;
    }

    public int preMeasureExposures(SceneSpecification scene) {
      return alternatingWarmupFrames(scene) + validationExposures(scene);
    }

    public ComparabilityMetadata comparability(
        SceneSpecification scene,
        EvidenceMode evidenceMode,
        ComparabilityMetadata.Environment environment,
        ComparabilityMetadata.Implementation implementation) {
      WorkloadIdentity identity = identity(scene);
      InputSet manifests = inputManifests(scene, identity);
      return new ComparabilityMetadata(
          "nanovg-rendering-1",
          identity.dimensions().get(Dimension.WORKLOAD_VERSION),
          "rendering-json-comparability-2",
          "text-behavior-1",
          evidenceMode,
          identity.semanticId(),
          identity.displayLabel(),
          manifests.content().sha256(),
          manifests.shape().sha256(),
          manifests.fonts().sha256(),
          environment,
          executionSettings(scene),
          implementation);
    }

    public InputSet inputManifests(SceneSpecification scene) {
      return inputManifests(scene, identity(scene));
    }

    private InputSet inputManifests(
        SceneSpecification scene, WorkloadIdentity identity) {
      Map<String, String> content = new LinkedHashMap<>();
      content.put("prewarm-text", prewarmText);
      for (int index = 0; index < sourceContent.size(); index++) {
        String suffix = String.format(java.util.Locale.ROOT, "%04d", index);
        content.put("source-" + suffix, sourceContent.get(index));
        content.put("transformed-source-" + suffix, transformedContent(index));
      }

      Map<String, String> shape = new LinkedHashMap<>();
      shape.put("shape-kind", "normal-text-rendering-scene");
      for (Dimension dimension :
          EnumSet.of(
              Dimension.CLIP_STATE,
              Dimension.COLOR,
              Dimension.COMPANION_SCENE_SHAPE,
              Dimension.COMPANION_TEXT_NODE_COUNT,
              Dimension.CONTAINER_HEIGHT_PX,
              Dimension.CONTAINER_POSITION_X_PX,
              Dimension.CONTAINER_POSITION_Y_PX,
              Dimension.CONTAINER_WIDTH_PX,
              Dimension.CONTROL_TYPE,
              Dimension.DISPLAY,
              Dimension.FRAME_HEIGHT_PX,
              Dimension.FRAME_WIDTH_PX,
              Dimension.INLINE_LAYOUT_START_Y_PX,
              Dimension.LINE_HEIGHT,
              Dimension.OVERFLOW_WRAP,
              Dimension.POSITION,
              Dimension.SCENE_HEIGHT_PX,
              Dimension.SCENE_PAIR_COUNT,
              Dimension.SCENE_WIDTH_PX,
              Dimension.SUBMISSION_STATE,
              Dimension.TAB_SIZE,
              Dimension.TEXT_ALIGN,
              Dimension.TEXT_NODE_COUNT,
              Dimension.VISIBILITY,
              Dimension.WHITE_SPACE,
              Dimension.WORD_BREAK,
              Dimension.WRAPPING_POLICY)) {
        shape.put(dimension.key(), identity.dimensions().get(dimension));
      }

      List<FontInput> fontInputs = new java.util.ArrayList<>();
      prewarmFonts.stream()
          .map(
              font ->
                  new FontInput(
                      "prewarm",
                      TextStyleSpecification.fontObjectIdentity(font),
                      font.path()))
          .forEach(fontInputs::add);
      layoutFonts().stream()
          .map(
              font ->
                  new FontInput(
                      "layout",
                      TextStyleSpecification.fontObjectIdentity(font),
                      font.path()))
          .forEach(fontInputs::add);
      return new InputSet(
          BenchmarkInputManifests.content(content),
          BenchmarkInputManifests.shape(shape),
          BenchmarkInputManifests.fonts(
              fontInputs,
              Map.of("font-size-px", identity.dimensions().get(Dimension.FONT_SIZE_PX))));
    }

    public Map<Dimension, Object> identityDimensions(SceneSpecification scene) {
      int orderIndex = measurementOrder.indexOf(scene);
      if (orderIndex < 0) {
        throw new IllegalArgumentException("Scene is not part of the renderer measurement pair");
      }
      SceneSpecification companion = measurementOrder.get(1 - orderIndex);
      Map<Dimension, Object> dimensions = new LinkedHashMap<>();
      dimensions.put(Dimension.API, "render-frame");
      dimensions.put(Dimension.CATEGORY, "normal-text");
      dimensions.put(
          Dimension.CLEAR_POLICY,
          clear.identityPolicy());
      dimensions.put(Dimension.CLIP_STATE, "inside");
      dimensions.put(Dimension.COLOR, colorIdentity(style.color()));
      dimensions.put(
          Dimension.COMPANION_SCENE_SHAPE,
          "same-workload-content-style-layout-and-geometry");
      dimensions.put(Dimension.COMPANION_TEXT_NODE_COUNT, companion.textNodeCount());
      dimensions.put(Dimension.CONTAINER_HEIGHT_PX, container.heightPx());
      dimensions.put(Dimension.CONTAINER_POSITION_X_PX, container.positionXPx());
      dimensions.put(Dimension.CONTAINER_POSITION_Y_PX, container.positionYPx());
      dimensions.put(Dimension.CONTAINER_WIDTH_PX, container.widthPx());
      dimensions.put(Dimension.CONTEXT_VISIBILITY, window.visible() ? "visible" : "hidden");
      dimensions.put(Dimension.CONTENT_ALTERNATION, "latin-mixed-cjk");
      dimensions.put(Dimension.CONTENT_TRANSFORM, contentTransform);
      dimensions.put(Dimension.CONTROL_TYPE, "none");
      dimensions.put(Dimension.DISPLAY, style.display().name());
      dimensions.put(
          Dimension.FIXTURE_PREPARATION_POLICY,
          "scenes-created-before-renderer-initialization");
      dimensions.put(Dimension.FONT_CHAIN, fontExecutionIdentities());
      dimensions.put(Dimension.FONT_SIZE_PX, style.fontSizePx());
      dimensions.put(Dimension.FONT_STRETCH, style.effectiveFontStretch().name());
      dimensions.put(Dimension.FONT_STYLE, style.fontStyle().name());
      dimensions.put(Dimension.FONT_WEIGHT, style.fontWeight().name());
      dimensions.put(
          Dimension.FONT_FIXTURE_POLICY,
          "mixed-cjk-once-before-each-scene-before-renderer-initialization");
      dimensions.put(Dimension.FONT_RESOLVER, "default");
      dimensions.put(Dimension.FRAME_HEIGHT_PX, window.heightPx());
      dimensions.put(Dimension.FRAME_WIDTH_PX, window.widthPx());
      dimensions.put(Dimension.HARNESS, "nanovg");
      dimensions.put(Dimension.INLINE_LAYOUT_START_Y_PX, inlineLayoutStartYPx);
      dimensions.put(Dimension.LINE_HEIGHT, style.lineHeight());
      dimensions.put(Dimension.MEASURED_FRAMES, measuredFrames);
      dimensions.put(Dimension.MEASUREMENT_ORDER, measurementOrderIdentity());
      dimensions.put(Dimension.MEASUREMENT_ORDER_INDEX, orderIndex + 1);
      dimensions.put(Dimension.NATIVE_ACCESS, "all-unnamed");
      dimensions.put(Dimension.OPERATION, "render-text");
      dimensions.put(Dimension.OVERFLOW_WRAP, style.overflowWrap().name());
      dimensions.put(Dimension.POSITION, style.position().name());
      dimensions.put(
          Dimension.PREMEASURE_SEQUENCE,
          structuralValidation.enabled()
              ? "alternating-small-large-plus-small-structural-validation"
              : "none");
      dimensions.put(Dimension.PREWARM_WORKLOAD_CONTENT, prewarmWorkloadContent);
      dimensions.put(Dimension.ROUND_TO_PIXEL, roundToPixel);
      dimensions.put(Dimension.RENDERER_PATH, "normal-text");
      dimensions.put(Dimension.SCENE_HEIGHT_PX, window.heightPx());
      dimensions.put(Dimension.SCENE_PAIR_COUNT, measurementOrder.size());
      dimensions.put(Dimension.SCENE_WIDTH_PX, window.widthPx());
      dimensions.put(Dimension.SETUP_LEVEL, "application-run");
      dimensions.put(Dimension.SUBMISSION_STATE, "changed");
      dimensions.put(Dimension.SWAP_INTERVAL, window.swapInterval());
      dimensions.put(
          Dimension.SYNCHRONIZATION, synchronizeWithGlFinish ? "gl-finish" : "none");
      dimensions.put(Dimension.TAB_SIZE, style.tabSize());
      dimensions.put(Dimension.TEXT_ALIGN, style.textAlign().name());
      dimensions.put(Dimension.TEXT_NODE_COUNT, scene.textNodeCount());
      dimensions.put(
          Dimension.VALIDATION_POLICY,
          structuralValidation.identityPolicy());
      dimensions.put(Dimension.VISIBILITY, "visible");
      dimensions.put(Dimension.WARMUP_FRAMES, warmupFrames);
      dimensions.put(Dimension.WARMUP_ORDER, "alternating-small-large-starting-small");
      dimensions.put(Dimension.WINDOW_RESIZABLE, window.resizable());
      dimensions.put(Dimension.WHITE_SPACE, style.whiteSpace().name());
      dimensions.put(Dimension.WORD_BREAK, style.wordBreak().name());
      dimensions.put(Dimension.WORKLOAD_CONTENT, workloadContent);
      dimensions.put(Dimension.WORKLOAD_VERSION, 1);
      dimensions.put(Dimension.WRAPPING_POLICY, "normal");
      return Collections.unmodifiableMap(dimensions);
    }

    private String measurementOrderIdentity() {
      String first = measurementOrder.get(0).name();
      String second = measurementOrder.get(1).name();
      if (first.equals("small") && second.equals("large")) {
        return "small-then-large";
      }
      if (first.equals("large") && second.equals("small")) {
        return "large-then-small";
      }
      throw new IllegalArgumentException("Scene names must define small/large measurement order");
    }

    private static void requireSameFonts(List<Font> expected, List<Font> actual, String path) {
      if (expected.size() != actual.size()) {
        throw new IllegalArgumentException(path + " font chain differs from declared ordered fonts");
      }
      for (int index = 0; index < expected.size(); index++) {
        if (expected.get(index) != actual.get(index)) {
          throw new IllegalArgumentException(
              path + " font at index " + index + " is not the declared exact Font object");
        }
      }
    }

    private static String colorIdentity(Color color) {
      if (Color.WHITE.equals(color)) {
        return "white";
      }
      if (Color.BLACK.equals(color)) {
        return "black";
      }
      throw new IllegalArgumentException("Unsupported identity color: " + color);
    }
  }
}
