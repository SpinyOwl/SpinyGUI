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
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAlloc;
import static org.lwjgl.system.MemoryUtil.memFree;

import com.google.gson.GsonBuilder;
import com.spinyowl.spinygui.benchmark.TextWorkloads;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.layout.impl.InlineFormattingContext;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.Text;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.OverflowWrap;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.TextAlign;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.WordBreak;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import com.spinyowl.spinygui.core.system.font.impl.FontStorageImpl;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector2i;

/** Runs the hidden-context NanoVG text rendering benchmark on the application main thread. */
public final class RenderingBenchmarkMain {
  private static final int WIDTH = 1280;
  private static final int HEIGHT = 720;
  private static final int WARMUP_FRAMES = 60;
  private static final int MEASURED_FRAMES = 200;
  private static final String LATIN_FRAGMENT = TextWorkloads.LATIN.replace(" ", "");
  private static final String MIXED_FRAGMENT = TextWorkloads.MIXED_CJK.replace(" ", "");

  private RenderingBenchmarkMain() {
  }

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new IllegalArgumentException("Expected the rendering report path as the only argument");
    }

    Path reportPath = Path.of(args[0]);
    RenderingReport report = new RenderingBenchmarkMain().run();
    Files.createDirectories(reportPath.getParent());
    Files.writeString(reportPath, new GsonBuilder().setPrettyPrinting().create().toJson(report));
  }

  private RenderingReport run() {
    try (HiddenContext context = new HiddenContext()) {
      context.initialize();
      FontServiceImpl fontService = new FontServiceImpl(new FontStorageImpl(), true);
      Scene smallScene = createScene(fontService, 100);
      Scene largeScene = createScene(fontService, 1_000);

      context.initializeRenderer();
      warm(context, smallScene, largeScene);
      validatePixels(context, smallScene);

      return new RenderingReport(
          environment(),
          true,
          List.of(measure(context, smallScene), measure(context, largeScene)));
    }
  }

  private Scene createScene(FontServiceImpl fontService, int fragmentCount) {
    fontService.measureText(
        MIXED_FRAGMENT,
        List.of(Font.ROBOTO_REGULAR, Font.NOTO_SANS_CJK_SC_REGULAR),
        16,
        1.2f);

    Frame frame = new Frame();
    style(frame);
    frame.frameSize(WIDTH, HEIGHT);
    frame.box().contentSize(WIDTH, HEIGHT);

    Element container = NodeBuilder.div();
    style(container);
    container.box().contentPosition(20, 20);
    container.box().contentSize(WIDTH - 40, HEIGHT - 40);
    container.offsetParent(frame);
    frame.addChild(container);

    List<Node> textNodes = new ArrayList<>(fragmentCount);
    for (int index = 0; index < fragmentCount; index++) {
      Text text = NodeBuilder.text(index % 2 == 0 ? LATIN_FRAGMENT : MIXED_FRAGMENT);
      text.offsetParent(container);
      container.addChild(text);
      textNodes.add(text);
    }
    new InlineFormattingContext(fontService).layout(container, textNodes, 0);

    int actualFragmentCount =
        textNodes.stream()
            .map(Text.class::cast)
            .mapToInt(text -> text.inlineFragments().size())
            .sum();
    if (actualFragmentCount != fragmentCount) {
      throw new IllegalStateException(
          "Expected %d text fragments but created %d".formatted(fragmentCount, actualFragmentCount));
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
        frame, fragmentCount, textNodes.size(), codePointCount, resolvedGlyphCount, resolvedRunCount);
  }

  private void warm(HiddenContext context, Scene... scenes) {
    for (int frame = 0; frame < WARMUP_FRAMES; frame++) {
      context.renderAndFinish(scenes[frame % scenes.length].frame());
    }
  }

  private void validatePixels(HiddenContext context, Scene scene) {
    context.renderAndFinish(scene.frame());
    ByteBuffer pixels = memAlloc(WIDTH * HEIGHT * 4);
    try {
      glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
      boolean drewText = false;
      for (int index = 0; index < pixels.capacity(); index += 4) {
        if (Byte.toUnsignedInt(pixels.get(index)) > 0
            || Byte.toUnsignedInt(pixels.get(index + 1)) > 0
            || Byte.toUnsignedInt(pixels.get(index + 2)) > 0) {
          drewText = true;
          break;
        }
      }
      if (!drewText) {
        throw new IllegalStateException("Pixel validation found only the cleared background");
      }
    } finally {
      memFree(pixels);
    }
  }

  private SceneReport measure(HiddenContext context, Scene scene) {
    long[] cpuNanos = new long[MEASURED_FRAMES];
    long[] gpuNanos = new long[MEASURED_FRAMES];
    for (int index = 0; index < MEASURED_FRAMES; index++) {
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
        WARMUP_FRAMES,
        MEASURED_FRAMES,
        summarize(cpuNanos),
        summarize(gpuNanos));
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

  private static Environment environment() {
    return new Environment(
        System.getProperty("java.version"),
        System.getProperty("java.vendor"),
        System.getProperty("os.name"),
        System.getProperty("os.version"),
        System.getProperty("os.arch"),
        glGetString(GL_VENDOR),
        glGetString(GL_RENDERER),
        glGetString(GL_VERSION));
  }

  private static void style(Element element) {
    ResolvedStyle style = element.resolvedStyle();
    style.display(Display.BLOCK);
    style.position(Position.STATIC);
    style.fontFamilies(List.of("Roboto", "Noto Sans CJK SC"));
    style.fontStyle(FontStyle.NORMAL);
    style.fontWeight(FontWeight.NORMAL);
    style.fontSize(Length.pixel(16));
    style.lineHeight(1.2f);
    style.color(Color.WHITE);
    style.whiteSpace(WhiteSpace.NORMAL);
    style.textAlign(TextAlign.LEFT);
    style.overflowWrap(OverflowWrap.NORMAL);
    style.wordBreak(WordBreak.NORMAL);
    style.tabSize(4);
  }

  private record Scene(
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
      Environment environment, boolean pixelValidationPassed, List<SceneReport> scenes) {
  }

  private record Environment(
      String javaVersion,
      String javaVendor,
      String osName,
      String osVersion,
      String osArchitecture,
      String glVendor,
      String glRenderer,
      String glVersion) {
  }

  private record SceneReport(
      int textFragmentCount,
      int textNodeCount,
      int textCodePointCount,
      int resolvedGlyphCount,
      int resolvedRunCount,
      int warmupFrameCount,
      int measuredFrameCount,
      LatencySummary cpuSubmissionMicros,
      LatencySummary gpuCompleteMicros) {
  }

  private record LatencySummary(
      double median, double p95, double p99, double budget60HzPercent, double budget120HzPercent) {
  }

  private static final class HiddenContext implements AutoCloseable {
    private long window;
    private NvgRenderer renderer;
    private boolean glfwInitialized;
    private boolean rendererInitialized;

    void initialize() {
      if (!glfwInit()) {
        throw new IllegalStateException("Unable to initialize GLFW");
      }
      glfwInitialized = true;
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
      window = glfwCreateWindow(WIDTH, HEIGHT, "SpinyGUI rendering benchmark", NULL, NULL);
      if (window == NULL) {
        throw new IllegalStateException("Unable to create hidden GLFW window");
      }
      glfwMakeContextCurrent(window);
      createCapabilities();
      glfwSwapInterval(0);
      glViewport(0, 0, WIDTH, HEIGHT);
    }

    void initializeRenderer() {
      renderer = new NvgRenderer();
      renderer.initialize();
      rendererInitialized = true;
    }

    FrameTiming render(Frame frame) {
      glClearColor(0, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
      glFinish();
      long start = System.nanoTime();
      renderer.render(window, new Vector2f(WIDTH, HEIGHT), new Vector2i(WIDTH, HEIGHT), frame);
      long submitted = System.nanoTime();
      glFinish();
      return new FrameTiming(submitted - start, System.nanoTime() - start);
    }

    void renderAndFinish(Frame frame) {
      render(frame);
    }

    @Override
    public void close() {
      try {
        if (rendererInitialized) {
          renderer.destroy();
        }
      } finally {
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
}
