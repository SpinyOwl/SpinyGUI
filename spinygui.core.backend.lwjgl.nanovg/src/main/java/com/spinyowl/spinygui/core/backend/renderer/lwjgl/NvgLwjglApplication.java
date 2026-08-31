package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.parser.NodeParser;
import com.spinyowl.spinygui.core.parser.StyleSheetParser;
import java.util.Objects;

/**
 * Ready-to-use NanoVG application base that owns the default core services and GLFW adapter.
 *
 * <p>Applications normally implement only {@link #initializeGui()}; low-level integrations can use
 * {@link AbstractLwjglApplication} directly and inject every service independently.
 */
public abstract class NvgLwjglApplication extends AbstractLwjglApplication {
  private final LwjglFrameServices services;
  private final NvgRenderer renderer;

  protected NvgLwjglApplication(LwjglApplicationConfiguration configuration) {
    this(configuration, new NvgRenderer());
  }

  protected NvgLwjglApplication(
      LwjglApplicationConfiguration configuration, NvgRenderer renderer) {
    this(components(configuration, renderer));
  }

  private NvgLwjglApplication(Components components) {
    super(
        components.services().frame(),
        components.services().pipeline(),
        components.renderer(),
        components.window(),
        components.services(),
        components.services().timeService()::currentTime);
    services = components.services();
    renderer = components.renderer();
  }

  @Override
  protected final void initializeApplication() {
    initializeGui();
  }

  @Override
  protected final void beforeRender(FramePreparation preparation) {
    renderer.debugMousePosition(services.mouseService().getCursorPositions(frame()).current());
    beforeNvgRender(preparation);
  }

  @Override
  protected final void shutdownApplication() {
    shutdownGui();
  }

  /** Creates nodes, listeners, and styles after the GLFW context and renderer are initialized. */
  protected abstract void initializeGui();

  /** Optional renderer-specific hook immediately before the clear/render boundary. */
  protected void beforeNvgRender(FramePreparation preparation) {}

  /** Optional application cleanup that runs before renderer and service teardown. */
  protected void shutdownGui() {}

  protected final void addStyleSheet(String css) {
    services.addStyleSheet(css);
  }

  protected final StyleSheetParser styleSheetParser() {
    return services.styleSheetParser();
  }

  protected final NodeParser nodeParser() {
    return services.nodeParser();
  }

  protected final LwjglFrameServices services() {
    return services;
  }

  private static Components components(
      LwjglApplicationConfiguration configuration, NvgRenderer renderer) {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(renderer, "renderer");
    LwjglFrameServices services = new LwjglFrameServices(renderer);
    DefaultLwjglWindow window =
        new DefaultLwjglWindow(configuration, services.frame(), services.systemEvents());
    return new Components(renderer, services, window);
  }

  private record Components(
      NvgRenderer renderer, LwjglFrameServices services, DefaultLwjglWindow window) {}
}
