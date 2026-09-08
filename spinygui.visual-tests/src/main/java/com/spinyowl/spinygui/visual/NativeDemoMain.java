package com.spinyowl.spinygui.visual;

import static org.lwjgl.glfw.GLFW.*;

import com.google.gson.Gson;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.AbstractLwjglApplication;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.DefaultLwjglWindow;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglFrameServices;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.parser.impl.DefaultNodeParser;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/** Visible native demo using the standard application pipeline and input callbacks. */
public final class NativeDemoMain {
  private NativeDemoMain() {}

  /** Receives shared case JSON, session directory and the requested client-area position. */
  public static void main(String[] args) throws IOException {
    if (args.length != 4) throw new IllegalArgumentException("Expected case, session, x, y");
    Path session = Path.of(args[1]);
    ViewCase view = new Gson().fromJson(Files.readString(Path.of(args[0])), ViewCase.class);
    Frame frame = new DefaultNodeParser().fromHtml(view.xml()).frame();
    NvgRenderer renderer = new NvgRenderer();
    var services = new LwjglFrameServices(frame, renderer);
    services.addStyleSheet(view.css());
    var configuration = new LwjglApplicationConfiguration(view.width(), view.height(),
        "SpinyGUI — " + view.id(), false, true);
    var window = new DefaultLwjglWindow(configuration, frame, services.systemEvents());
    new AbstractLwjglApplication(frame, services.pipeline(), renderer, window, services,
        services.timeService()::currentTime) {
      private boolean ready;

      @Override protected void initializeApplication() {
        glfwSetWindowPos(window.handle(), Integer.parseInt(args[2]), Integer.parseInt(args[3]));
      }

      @Override protected void update(double seconds) {
        if (Files.exists(session.resolve("stop"))) glfwSetWindowShouldClose(window.handle(), true);
        // The reusable host skips unchanged rendering; avoid spinning while the demo is idle.
        glfwWaitEventsTimeout(0.016);
      }

      @Override protected void afterRender(FramePreparation preparation) {
        if (ready) return;
        if (!view.scrollId().isEmpty()) applyScroll(frame, view);
        int[] x = {0}, y = {0}, width = {0}, height = {0};
        glfwGetWindowPos(window.handle(), x, y);
        glfwGetWindowSize(window.handle(), width, height);
        try {
          Files.writeString(session.resolve("native-ready.json"), new Gson().toJson(Map.of(
              "x", x[0], "y", y[0], "width", width[0], "height", height[0], "demo", view.id())));
        } catch (IOException failure) {
          throw new UncheckedIOException(failure);
        }
        ready = true;
      }
    }.run();
  }

  private static void applyScroll(Element element, ViewCase view) {
    if (view.scrollId().equals(element.getAttribute("id"))) {
      element.scrollLeft((float) view.scrollX());
      element.scrollTop((float) view.scrollY());
    }
    for (Node child : element.childNodes()) {
      if (child instanceof Element nested) applyScroll(nested, view);
    }
  }
}
