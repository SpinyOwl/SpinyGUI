package com.spinyowl.spinygui.visual;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglFrameServices;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.clipboard.Clipboard;
import com.spinyowl.spinygui.core.input.impl.KeyboardLayoutImpl;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import javax.imageio.ImageIO;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

/** Isolated native process: bounded by the parent, and started on the first thread on macOS. */
public final class NativeCaptureMain {
  private NativeCaptureMain() {}

  /** Captures one case so native crashes cannot turn other cases into apparent passes. */
  public static void main(String[] args) throws Exception {
    if (args.length != 2) throw new IllegalArgumentException("Expected case JSON and output directory");
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    ViewCase view = gson.fromJson(Files.readString(Path.of(args[0])), ViewCase.class);
    Path output = Path.of(args[1]);
    Files.createDirectories(output);
    if (!glfwInit()) throw new IllegalStateException("GLFW unavailable: an OpenGL display is required");
    long window = 0;
    try {
      glfwDefaultWindowHints();
      glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
      glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE);
      glfwWindowHint(GLFW_STENCIL_BITS, 8);
      glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
      glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
      glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
      glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
      window = glfwCreateWindow(view.width(), view.height(), "SpinyGUI comparison", 0, 0);
      if (window == 0) throw new IllegalStateException("Unable to create OpenGL 3.2 context");
      glfwMakeContextCurrent(window);
      GL.createCapabilities();
      capture(view, window, output, gson);
    } finally {
      GL.setCapabilities(null);
      if (window != 0) glfwDestroyWindow(window);
      glfwTerminate();
    }
  }

  private static void capture(ViewCase view, long window, Path output, Gson gson) throws Exception {
    Frame frame = new DefaultNodeParser().fromHtml(view.xml()).frame();
    NvgRenderer renderer = new NvgRenderer();
    Clipboard clipboard = new Clipboard() {
      public String getClipboardString() { return ""; }
      public void setClipboardString(String text) { }
    };
    try (var services = new LwjglFrameServices(frame, renderer, () -> 0d,
        clipboard, new KeyboardLayoutImpl(Map.of()))) {
      try {
        renderer.initialize();
        frame.frameSize(view.width(), view.height());
        services.addStyleSheet(view.css());
        prepare(services, frame);
        if (!view.scrollId().isEmpty()) {
          Element target = find(frame, view.scrollId());
          if (target == null) throw new IllegalStateException("Missing scroll target: " + view.scrollId());
          target.scrollLeft((float) view.scrollX());
          target.scrollTop((float) view.scrollY());
          prepare(services, frame);
        }
        // Render a CSS-pixel-sized viewport even on HiDPI displays; no desktop/window chrome.
        int[] framebufferWidth = {0}, framebufferHeight = {0};
        glfwGetFramebufferSize(window, framebufferWidth, framebufferHeight);
        if (framebufferWidth[0] < view.width() || framebufferHeight[0] < view.height()) {
          throw new IllegalStateException("Framebuffer smaller than requested viewport");
        }
        glViewport(0, 0, view.width(), view.height());
        glClearColor(1, 1, 1, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
        renderer.render(window, new Vector2f(view.width(), view.height()),
            new Vector2i(view.width(), view.height()), frame);
        glFinish();
        writePixels(view.width(), view.height(), output.resolve("native.png"));
        Files.writeString(output.resolve("native-geometry.json"), gson.toJson(Geometry.collect(frame)));
        Files.writeString(output.resolve("native-environment.json"), gson.toJson(Map.of(
            "os", System.getProperty("os.name"), "arch", System.getProperty("os.arch"),
            "java", System.getProperty("java.runtime.version"),
            "glVendor", glGetString(GL_VENDOR), "glRenderer", glGetString(GL_RENDERER),
            "glVersion", glGetString(GL_VERSION), "pixelRatio", 1,
            "width", view.width(), "height", view.height())));
      } finally {
        renderer.destroy();
      }
    }
  }

  private static void prepare(LwjglFrameServices services, Frame frame) {
    var prepared = services.pipeline().prepareFrame(frame);
    if (!prepared.renderable()) throw new IllegalStateException("Frame preparation failed: " + prepared);
  }

  private static Element find(Element element, String id) {
    if (id.equals(element.getAttribute("id"))) return element;
    for (Node child : element.childNodes()) {
      if (child instanceof Element nested) {
        Element found = find(nested, id);
        if (found != null) return found;
      }
    }
    return null;
  }

  private static void writePixels(int width, int height, Path output) throws Exception {
    var pixels = MemoryUtil.memAlloc(Math.multiplyExact(Math.multiplyExact(width, height), 4));
    try {
      glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
      int error = glGetError();
      if (error != GL_NO_ERROR) throw new IllegalStateException("OpenGL capture error: " + error);
      var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int offset = ((height - 1 - y) * width + x) * 4;
          image.setRGB(x, y, (pixels.get(offset + 3) & 255) << 24
              | (pixels.get(offset) & 255) << 16 | (pixels.get(offset + 1) & 255) << 8
              | (pixels.get(offset + 2) & 255));
        }
      }
      if (!ImageIO.write(image, "png", output.toFile())) throw new IllegalStateException("PNG writer unavailable");
    } finally {
      MemoryUtil.memFree(pixels);
    }
  }
}
