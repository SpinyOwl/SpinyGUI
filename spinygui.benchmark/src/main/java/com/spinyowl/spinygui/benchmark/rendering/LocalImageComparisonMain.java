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
import static org.lwjgl.opengl.GL11.GL_RENDERER;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.GL_STENCIL_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.GL_VENDOR;
import static org.lwjgl.opengl.GL11.GL_VERSION;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL30.GL_MAJOR_VERSION;
import static org.lwjgl.opengl.GL30.GL_MINOR_VERSION;
import static org.lwjgl.opengl.GL30.glGetInteger;
import static org.lwjgl.system.MemoryUtil.NULL;

import com.google.gson.GsonBuilder;
import com.spinyowl.spinygui.benchmark.identity.BenchmarkRuntimeMetadata;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.ComparisonOutcome;
import com.spinyowl.spinygui.benchmark.rendering.LocalImageComparisonPolicy.EnvironmentFingerprint;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgStructuralValidation;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.system.font.impl.FontServiceImpl;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

/** Explicit local capture-and-compare runner for the approved source-bound boundary scenes. */
public final class LocalImageComparisonMain {
  private static final int WIDTH = 400;
  private static final int HEIGHT = 200;

  private LocalImageComparisonMain() {}

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new IllegalArgumentException("Expected reference root and artifact root");
    }
    if (!Boolean.getBoolean(LocalImageComparisonPolicy.OPT_IN_PROPERTY)) {
      throw new IllegalStateException(
          "Local image comparison requires -D"
              + LocalImageComparisonPolicy.OPT_IN_PROPERTY
              + "=true");
    }
    Path referenceRoot = Path.of(args[0]);
    Path artifactRoot = Path.of(args[1]);
    Files.createDirectories(artifactRoot);
    List<SceneOutcome> outcomes = new ArrayList<>();
    try (Context context = new Context()) {
      context.initialize();
      FontServiceImpl fontService =
          RenderingWorkloadSpecifications.CURRENT.createFontService(DiagnosticSession.disabled());
      context.initializeRenderer(fontService);
      EnvironmentFingerprint environment = context.environment();
      for (RenderingBoundaryScenes.BoundaryScene scene : RenderingBoundaryScenes.scenes(fontService)) {
        NvgStructuralValidation.validate(scene.frame(), fontService, scene.requirements());
        Path capture =
            artifactRoot
                .resolve("captures")
                .resolve(LocalImageComparisonPolicy.POLICY_VERSION)
                .resolve(scene.id())
                .resolve("actual.png");
        context.capture(scene.frame(), capture);
        ComparisonOutcome outcome =
            LocalImageComparisonPolicy.compareConfigured(
                true, scene.id(), environment, capture, referenceRoot, artifactRoot);
        outcomes.add(
            new SceneOutcome(
                scene.id(), outcome.evaluation().status().name(), outcome.evaluation().detail()));
      }
    }
    Files.writeString(
        artifactRoot.resolve("local-image-comparison-summary.json"),
        new GsonBuilder().setPrettyPrinting().create().toJson(outcomes));
  }

  private record SceneOutcome(String sceneId, String status, String detail) {}

  private static final class Context implements AutoCloseable {
    private long window;
    private NvgRenderer renderer;
    private boolean glfwInitialized;
    private boolean rendererInitialized;

    private void initialize() {
      if (!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW");
      glfwInitialized = true;
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
      window = glfwCreateWindow(WIDTH, HEIGHT, "SpinyGUI local image comparison", NULL, NULL);
      if (window == NULL) throw new IllegalStateException("Unable to create hidden GLFW window");
      glfwMakeContextCurrent(window);
      createCapabilities();
      glfwSwapInterval(0);
      glViewport(0, 0, WIDTH, HEIGHT);
    }

    private void initializeRenderer(FontServiceImpl fontService) {
      renderer = new NvgRenderer(true, DiagnosticSession.disabled());
      renderer.initialize();
      renderer.textMeasurer(fontService);
      rendererInitialized = true;
    }

    private void capture(Frame frame, Path output) throws IOException {
      glClearColor(0, 0, 0, 1);
      glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
      renderer.render(window, new Vector2f(WIDTH, HEIGHT), new Vector2i(WIDTH, HEIGHT), frame);
      glFinish();
      ByteBuffer pixels = MemoryUtil.memAlloc(WIDTH * HEIGHT * 4);
      try {
        glReadPixels(0, 0, WIDTH, HEIGHT, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < HEIGHT; y++) {
          int sourceY = HEIGHT - 1 - y;
          for (int x = 0; x < WIDTH; x++) {
            int offset = (sourceY * WIDTH + x) * 4;
            int red = Byte.toUnsignedInt(pixels.get(offset));
            int green = Byte.toUnsignedInt(pixels.get(offset + 1));
            int blue = Byte.toUnsignedInt(pixels.get(offset + 2));
            int alpha = Byte.toUnsignedInt(pixels.get(offset + 3));
            image.setRGB(x, y, alpha << 24 | red << 16 | green << 8 | blue);
          }
        }
        Files.createDirectories(output.getParent());
        if (!ImageIO.write(image, "png", output.toFile())) {
          throw new IOException("PNG writer unavailable");
        }
      } finally {
        MemoryUtil.memFree(pixels);
      }
    }

    private EnvironmentFingerprint environment() {
      String glVersion = glGetString(GL_VERSION);
      var environment =
          BenchmarkRuntimeMetadata.renderingEnvironment(
              glGetString(GL_VENDOR), glGetString(GL_RENDERER), glVersion, glVersion);
      String backend =
          glGetInteger(GL_MAJOR_VERSION) > 3
                  || glGetInteger(GL_MAJOR_VERSION) == 3 && glGetInteger(GL_MINOR_VERSION) >= 2
              ? "nanovg-gl3"
              : "nanovg-gl2";
      return LocalImageComparisonPolicy.environment(
          environment, backend, true, WIDTH, HEIGHT, 1f);
    }

    @Override
    public void close() {
      try {
        if (rendererInitialized) renderer.destroy();
      } finally {
        setCapabilities(null);
        if (window != NULL) {
          glfwMakeContextCurrent(NULL);
          glfwDestroyWindow(window);
        }
        if (glfwInitialized) glfwTerminate();
      }
    }
  }
}
