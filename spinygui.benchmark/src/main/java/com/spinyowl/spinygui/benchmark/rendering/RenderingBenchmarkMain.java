package com.spinyowl.spinygui.benchmark.rendering;

import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_RESIZABLE;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL.setCapabilities;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.spinyowl.spinygui.benchmark.RendererHostLifecycle;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkInvocationMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Artifact;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRunMetadata.Pairing;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata;
import com.spinyowl.spinygui.benchmark.identity.ComparabilityMetadata.EvidenceMode;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector2i;

/** Runs the hidden-context NanoVG text rendering benchmark on the application main thread. */
public final class RenderingBenchmarkMain {
  private static final RenderingWorkloadSpecifications.Specification SPECIFICATION =
      RenderingWorkloadSpecifications.CURRENT;

  private RenderingBenchmarkMain() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 3) {
      throw new IllegalArgumentException(
          "Expected rendering report path, run ID, and pairing eligibility");
    }

    Path reportPath = Path.of(args[0]);
    BenchmarkRunMetadata runMetadata =
        BenchmarkInvocationMetadata.timed(args[1], Artifact.RENDERING, Pairing.fromJson(args[2]));
    RenderingReport report = new RenderingBenchmarkMain().run(runMetadata);
    Files.createDirectories(reportPath.getParent());
    Files.writeString(reportPath, new GsonBuilder().setPrettyPrinting().create().toJson(report));
  }

  private RenderingReport run(BenchmarkRunMetadata runMetadata) {
    DiagnosticSession diagnostics = DiagnosticSession.disabled();
    try (
        FontServiceImpl fontService = SPECIFICATION.createFontService(diagnostics);
        HiddenContext context = new HiddenContext()) {
      context.initialize();
      List<Scene> scenes =
          SPECIFICATION.measurementOrder().stream()
              .map(scene -> createScene(fontService, scene))
              .toList();

      context.initializeRenderer(diagnostics);
      warm(context, scenes);
      StructuralValidationReport structuralEvidence = null;
      if (SPECIFICATION.structuralValidation().enabled()) {
        Scene validationScene =
            scenes.stream()
                .filter(
                    scene ->
                        scene
                            .specification()
                            .name()
                            .equals(SPECIFICATION.structuralValidation().sceneName()))
                .findFirst()
                .orElseThrow();
        structuralEvidence = validateStructure(context, validationScene, fontService);
      }

      ComparabilityMetadata.Environment comparabilityEnvironment = comparabilityEnvironment();
      ComparabilityMetadata.Implementation implementation =
          BenchmarkRuntimeMetadata.implementation();
      return new RenderingReport(
          runMetadata.toJson(),
          environment(comparabilityEnvironment),
          structuralEvidence.toJson(),
          scenes.stream()
              .map(scene -> measure(context, scene, comparabilityEnvironment, implementation))
              .toList());
    }
  }

  private Scene createScene(
      FontServiceImpl fontService,
      RenderingWorkloadSpecifications.SceneSpecification sceneSpecification) {
    fontService.measureText(
        SPECIFICATION.prewarmText(),
        SPECIFICATION.prewarmFonts(),
        SPECIFICATION.style().fontSizePx(),
        SPECIFICATION.style().lineHeight());

    Frame frame = new Frame();
    SPECIFICATION.style().apply(frame);
    frame.frameSize(SPECIFICATION.window().widthPx(), SPECIFICATION.window().heightPx());
    frame.box().contentSize(
        SPECIFICATION.window().widthPx(), SPECIFICATION.window().heightPx());

    Element container = NodeBuilder.div();
    SPECIFICATION.style().apply(container);
    container.box().contentPosition(
        SPECIFICATION.container().positionXPx(), SPECIFICATION.container().positionYPx());
    container.box().contentSize(
        SPECIFICATION.container().widthPx(), SPECIFICATION.container().heightPx());
    container.offsetParent(frame);
    frame.addChild(container);

    List<Node> textNodes = new ArrayList<>(sceneSpecification.textNodeCount());
    for (int index = 0; index < sceneSpecification.textNodeCount(); index++) {
      Text text = NodeBuilder.text(SPECIFICATION.transformedContent(index));
      text.offsetParent(container);
      container.addChild(text);
      textNodes.add(text);
    }
    new InlineFormattingContext(fontService).layout(
        container, textNodes, SPECIFICATION.inlineLayoutStartYPx());

    int actualFragmentCount =
        textNodes.stream()
            .map(Text.class::cast)
            .mapToInt(text -> text.inlineFragments().size())
            .sum();
    if (actualFragmentCount != sceneSpecification.textNodeCount()) {
      throw new IllegalStateException(
          "Expected %d text fragments but created %d"
              .formatted(sceneSpecification.textNodeCount(), actualFragmentCount));
    }
    int codePointCount =
        textNodes.stream()
            .map(Text.class::cast)
            .mapToInt(text -> text.content().codePointCount(0, text.content().length()))
            .sum();
    int resolvedRunCount =
        textNodes.stream()
            .map(Text.class::cast)
            .mapToInt(
                text ->
                    text.inlineFragments().stream()
                        .mapToInt(fragment -> fragment.runs().size())
                        .sum())
            .sum();
    int resolvedGlyphCount =
        textNodes.stream()
            .map(Text.class::cast)
            .mapToInt(
                text ->
                    text.inlineFragments().stream()
                        .flatMap(fragment -> fragment.runs().stream())
                        .mapToInt(run -> run.glyphs().size())
                        .sum())
            .sum();

    frame.layoutChildNodes(List.of(container));
    container.layoutChildNodes(textNodes);
    return new Scene(
        sceneSpecification,
        frame,
        actualFragmentCount,
        textNodes.size(),
        codePointCount,
        resolvedGlyphCount,
        resolvedRunCount);
  }

  private void warm(HiddenContext context, List<Scene> scenes) {
    for (int frame = 0; frame < SPECIFICATION.warmupFrames(); frame++) {
      context.renderAndFinish(scenes.get(frame % scenes.size()).frame());
    }
  }

  private StructuralValidationReport validateStructure(
      HiddenContext context, Scene scene, FontServiceImpl fontService) {
    context.renderAndFinish(scene.frame());
    NvgStructuralValidation.Evidence benchmarkEvidence =
        NvgStructuralValidation.validate(
            scene.frame(),
            fontService,
            RenderingBoundaryScenes.synchronizedSmallRequirements(scene.frame()));
    List<NvgStructuralValidation.Evidence> boundaryEvidence =
        RenderingBoundaryScenes.validateAll(fontService);
    return StructuralValidationReport.create(benchmarkEvidence, boundaryEvidence);
  }

  private SceneReport measure(
      HiddenContext context,
      Scene scene,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation) {
    long[] cpuNanos = new long[SPECIFICATION.measuredFrames()];
    long[] gpuNanos = new long[SPECIFICATION.measuredFrames()];
    for (int index = 0; index < SPECIFICATION.measuredFrames(); index++) {
      FrameTiming timing = context.render(scene.frame());
      cpuNanos[index] = timing.cpuSubmissionNanos();
      gpuNanos[index] = timing.gpuCompleteNanos();
    }
    return new SceneReport(
        scene.fragmentCount(),
        scene.textNodeCount(),
        scene.codePointCount(),
        scene.resolvedGlyphCount(),
        scene.resolvedRunCount(),
        SPECIFICATION.alternatingWarmupFrames(scene.specification()),
        SPECIFICATION.validationExposures(scene.specification()),
        SPECIFICATION.preMeasureExposures(scene.specification()),
        SPECIFICATION.measuredFrames(),
        summarize(cpuNanos),
        summarize(gpuNanos),
        sceneComparability(scene.specification(), environment, implementation).toJson());
  }

  private LatencySummary summarize(long[] nanos) {
    long[] sorted = nanos.clone();
    Arrays.sort(sorted);
    return new LatencySummary(
        micros(percentile(sorted, 0.50)),
        micros(percentile(sorted, 0.95)),
        micros(percentile(sorted, 0.99)),
        budgetPercent(percentile(sorted, 0.50), 60),
        budgetPercent(percentile(sorted, 0.50), 120));
  }

  private static long percentile(long[] sorted, double percentile) {
    int index = Math.min(sorted.length - 1, (int) Math.ceil(percentile * sorted.length) - 1);
    return sorted[index];
  }

  private static double micros(long nanos) {
    return nanos / 1_000.0;
  }

  private static double budgetPercent(long nanos, int hertz) {
    return nanos * hertz / 10_000_000.0;
  }

  static ComparabilityMetadata sceneComparability(
      RenderingWorkloadSpecifications.SceneSpecification scene,
      ComparabilityMetadata.Environment environment,
      ComparabilityMetadata.Implementation implementation) {
    return SPECIFICATION.comparability(
        scene, EvidenceMode.TIMED_ALLOCATION_DIAGNOSTICS_DISABLED, environment, implementation);
  }

  private static ComparabilityMetadata.Environment comparabilityEnvironment() {
    String glVersion = glGetString(GL_VERSION);
    return BenchmarkRuntimeMetadata.renderingEnvironment(
        glGetString(GL_VENDOR), glGetString(GL_RENDERER), glVersion, glVersion);
  }

  private static Environment environment(ComparabilityMetadata.Environment environment) {
    return new Environment(
        environment.jvmVersion(),
        environment.jvmVendor(),
        environment.osName(),
        environment.osVersion(),
        environment.osArchitecture(),
        environment.cpuModel(),
        environment.glVendor(),
        environment.glRenderer(),
        environment.glDriverVersion(),
        environment.glVersion());
  }

  private record Scene(
      RenderingWorkloadSpecifications.SceneSpecification specification,
      Frame frame,
      int fragmentCount,
      int textNodeCount,
      int codePointCount,
      int resolvedGlyphCount,
      int resolvedRunCount) {
  }

  private record FrameTiming(long cpuSubmissionNanos, long gpuCompleteNanos) {
  }

  private record RenderingReport(
      JsonObject benchmarkRun,
      Environment environment,
      JsonObject structuralValidation,
      List<SceneReport> scenes) {
  }


  private record Environment(
      String javaVersion,
      String javaVendor,
      String osName,
      String osVersion,
      String osArchitecture,
      String cpuModel,
      String glVendor,
      String glRenderer,
      String glDriverVersion,
      String glVersion) {
  }

  record SceneReport(
      int textFragmentCount,
      int textNodeCount,
      int textCodePointCount,
      int resolvedGlyphCount,
      int resolvedRunCount,
      int alternatingWarmupFrameCount,
      int validationExposureCount,
      int preMeasureExposureCount,
      int measuredFrameCount,
      LatencySummary cpuSubmissionMicros,
      LatencySummary gpuCompleteMicros,
      JsonObject comparability) {
  }

  record LatencySummary(
      double median, double p95, double p99, double budget60HzPercent, double budget120HzPercent) {
  }

  private static final class HiddenContext implements AutoCloseable {
    private long window;
    private NvgRenderer renderer;
    private RendererHostLifecycle<NvgRenderer> rendererLifecycle;
    private boolean glfwInitialized;

    void initialize() {
      if (!glfwInit()) {
        throw new IllegalStateException("Unable to initialize GLFW");
      }
      glfwInitialized = true;
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, SPECIFICATION.window().visible() ? 1 : GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, SPECIFICATION.window().resizable() ? 1 : GLFW_FALSE);
      window =
          glfwCreateWindow(
              SPECIFICATION.window().widthPx(),
              SPECIFICATION.window().heightPx(),
              "SpinyGUI rendering benchmark",
              NULL,
              NULL);
      if (window == NULL) {
        throw new IllegalStateException("Unable to create hidden GLFW window");
      }
      glfwMakeContextCurrent(window);
      createCapabilities();
      glfwSwapInterval(SPECIFICATION.window().swapInterval());
      glViewport(0, 0, SPECIFICATION.window().widthPx(), SPECIFICATION.window().heightPx());
    }

    void initializeRenderer(DiagnosticSession diagnostics) {
      rendererLifecycle =
          new RendererHostLifecycle<>(
              () -> new NvgRenderer(true, diagnostics),
              NvgRenderer::initialize,
              NvgRenderer::destroy,
              this::closeHost);
      renderer = rendererLifecycle.initialize();
    }

    FrameTiming render(Frame frame) {
      if (SPECIFICATION.clear().enabled()) {
        glClearColor(
            SPECIFICATION.clear().red(),
            SPECIFICATION.clear().green(),
            SPECIFICATION.clear().blue(),
            SPECIFICATION.clear().alpha());
        glClear(SPECIFICATION.clear().mask());
      }
      if (SPECIFICATION.synchronizeWithGlFinish()) {
        glFinish();
      }
      long start = System.nanoTime();
      renderer.render(
          window,
          new Vector2f(SPECIFICATION.window().widthPx(), SPECIFICATION.window().heightPx()),
          new Vector2i(SPECIFICATION.window().widthPx(), SPECIFICATION.window().heightPx()),
          frame);
      long submitted = System.nanoTime();
      if (SPECIFICATION.synchronizeWithGlFinish()) {
        glFinish();
      }
      return new FrameTiming(submitted - start, System.nanoTime() - start);
    }

    void renderAndFinish(Frame frame) {
      render(frame);
    }

    @Override
    public void close() {
      if (rendererLifecycle != null) {
        rendererLifecycle.close();
      } else {
        closeHost();
      }
    }

    private void closeHost() {
      setCapabilities(null);
      if (window != NULL) {
        glfwMakeContextCurrent(NULL);
        glfwDestroyWindow(window);
      }
      if (glfwInitialized) {
        glfwTerminate();
      }
    }
  }
}
