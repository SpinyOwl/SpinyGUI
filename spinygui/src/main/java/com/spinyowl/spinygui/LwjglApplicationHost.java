package com.spinyowl.spinygui;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose;

import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.AbstractLwjglApplication;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.DefaultLwjglWindow;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.GlfwSystemEventMapper;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglFrameServices;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglWindow;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.NvgRenderer;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.Objects;
import java.util.function.DoubleSupplier;

/**
 * Production convenience host for browser-like frame navigation on one standalone LWJGL window.
 *
 * <p>The owned factory composes standard services, an owned cbchain bridge, the concrete standalone
 * window, pipeline, renderer, and navigator. The injected factory initializes but never closes its
 * caller-owned resources.
 */
public final class LwjglApplicationHost implements AutoCloseable {
  /** Navigator used by callbacks and resolved after update by every host iteration. */
  private final FrameNavigator navigator;
  /** Pipeline shared by all frames reachable through the navigator. */
  private final FramePipeline pipeline;
  /** Renderer initialized and invoked by the reusable loop. */
  private final Renderer renderer;
  /** Concrete owned window or caller-injected compatible window. */
  private final LwjglWindow window;
  /** Standard owned service bundle or caller-injected service boundary. */
  private final AutoCloseable services;
  /** Host clock used for deterministic frame deltas. */
  private final DoubleSupplier clock;
  /** Explicit all-owned or all-injected cleanup contract. */
  private final AbstractLwjglApplication.ResourceOwnership ownership;
  /** Caller lifecycle hooks retained until the single run completes. */
  private final Lifecycle lifecycle;
  /** Standard services exposed only for the production owned composition. */
  private final LwjglFrameServices defaultServices;
  /** Owned bridge exposed for integration diagnostics; null for injected composition. */
  private final GlfwSystemEventBridge eventBridge;
  /** True after the single permitted run has started. */
  private boolean started;
  /** True after run cleanup or explicit pre-run close completes. */
  private boolean closed;

  LwjglApplicationHost(
      FrameNavigator navigator,
      FramePipeline pipeline,
      Renderer renderer,
      LwjglWindow window,
      AutoCloseable services,
      DoubleSupplier clock,
      AbstractLwjglApplication.ResourceOwnership ownership,
      Lifecycle lifecycle,
      LwjglFrameServices defaultServices,
      GlfwSystemEventBridge eventBridge) {
    this.navigator = Objects.requireNonNull(navigator, "navigator");
    this.pipeline = Objects.requireNonNull(pipeline, "pipeline");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.window = Objects.requireNonNull(window, "window");
    this.services = Objects.requireNonNull(services, "services");
    this.clock = Objects.requireNonNull(clock, "clock");
    this.ownership = Objects.requireNonNull(ownership, "ownership");
    this.lifecycle = Objects.requireNonNull(lifecycle, "lifecycle");
    this.defaultServices = defaultServices;
    this.eventBridge = eventBridge;
  }

  /** Creates an owned NanoVG host with the standard renderer and services. */
  public static LwjglApplicationHost owned(
      LwjglApplicationConfiguration configuration,
      Frame initialFrame,
      int historyCapacity,
      Lifecycle lifecycle) {
    return owned(configuration, initialFrame, historyCapacity, new NvgRenderer(), lifecycle);
  }

  /**
   * Creates an owned host around the supplied renderer and standard service composition.
   * Ownership of {@code renderer} transfers to the returned host after successful construction.
   */
  public static LwjglApplicationHost owned(
      LwjglApplicationConfiguration configuration,
      Frame initialFrame,
      int historyCapacity,
      Renderer renderer,
      Lifecycle lifecycle) {
    return owned(
        configuration,
        initialFrame,
        historyCapacity,
        renderer,
        lifecycle,
        LwjglFrameServices::new);
  }

  static LwjglApplicationHost owned(
      LwjglApplicationConfiguration configuration,
      Frame initialFrame,
      int historyCapacity,
      Renderer renderer,
      Lifecycle lifecycle,
      OwnedServicesFactory servicesFactory) {
    Objects.requireNonNull(configuration, "configuration");
    Objects.requireNonNull(initialFrame, "initialFrame");
    Objects.requireNonNull(renderer, "renderer");
    Objects.requireNonNull(lifecycle, "lifecycle");
    Objects.requireNonNull(servicesFactory, "servicesFactory");
    FrameNavigator navigator = new FrameNavigator(initialFrame, historyCapacity);
    LwjglFrameServices services = servicesFactory.create(initialFrame, renderer);
    try {
      GlfwSystemEventBridge bridge = GlfwSystemEventBridge.owned(
          navigator,
          services.systemEvents(),
          (window, key, action) -> {
            if (key == GLFW_KEY_ESCAPE && action != GLFW_RELEASE) {
              glfwSetWindowShouldClose(window, true);
            }
          });
      DefaultLwjglWindow window = new DefaultLwjglWindow(configuration, bridge);
      return new LwjglApplicationHost(
          navigator,
          services.pipeline(),
          renderer,
          window,
          services,
          services.timeService()::currentTime,
          AbstractLwjglApplication.ResourceOwnership.owned(),
          lifecycle,
          services,
          bridge);
    } catch (RuntimeException failure) {
      try {
        services.close();
      } catch (RuntimeException cleanupFailure) {
        failure.addSuppressed(cleanupFailure);
      }
      throw failure;
    }
  }

  /**
   * Creates a host over caller-owned resources. It initializes and uses them but never closes them,
   * including after partial initialization failure.
   */
  public static LwjglApplicationHost injected(
      FrameNavigator navigator,
      FramePipeline pipeline,
      Renderer renderer,
      LwjglWindow window,
      AutoCloseable services,
      DoubleSupplier clock,
      Lifecycle lifecycle) {
    return new LwjglApplicationHost(
        navigator,
        pipeline,
        renderer,
        window,
        services,
        clock,
        AbstractLwjglApplication.ResourceOwnership.injected(),
        lifecycle,
        null,
        null);
  }

  /** Runs the canonical loop exactly once. */
  public void run() {
    if (started || closed) throw new IllegalStateException("Host instances are single-use");
    started = true;
    try {
      new HostLoop().run();
    } finally {
      closed = true;
    }
  }

  /** Returns the navigator shared by callback capture and the render loop. */
  public FrameNavigator navigator() {
    return navigator;
  }

  /** Returns the current frame at call time. */
  public Frame currentFrame() {
    return navigator.currentFrame();
  }

  /** Returns the standard owned services; unavailable for injected hosts. */
  public LwjglFrameServices services() {
    if (defaultServices == null) {
      throw new IllegalStateException("Injected hosts do not own standard services");
    }
    return defaultServices;
  }

  /** Returns the owned cbchain bridge; unavailable for injected hosts. */
  public GlfwSystemEventBridge eventBridge() {
    if (eventBridge == null) {
      throw new IllegalStateException("Injected hosts do not own an event bridge");
    }
    return eventBridge;
  }

  /** Closes pre-run owned resources; completed or injected hosts require no additional action. */
  @Override
  public void close() {
    if (closed) return;
    if (started) throw new IllegalStateException("A running host is closed by its loop");
    closed = true;
    RuntimeException failure = null;
    if (ownership.closeRenderer()) {
      try {
        renderer.destroy();
      } catch (RuntimeException cleanupFailure) {
        failure = cleanupFailure;
      }
    }
    if (ownership.closeServices()) {
      try {
        services.close();
      } catch (Exception cleanupFailure) {
        RuntimeException serviceFailure = cleanupFailure instanceof RuntimeException runtimeFailure
            ? runtimeFailure
            : new IllegalStateException("Failed to close host services", cleanupFailure);
        if (failure == null) failure = serviceFailure;
        else failure.addSuppressed(serviceFailure);
      }
    }
    if (failure != null) throw failure;
  }

  @FunctionalInterface
  interface OwnedServicesFactory {
    LwjglFrameServices create(Frame frame, Renderer renderer);
  }

  /** Lifecycle callbacks invoked by the host around its canonical loop boundaries. */
  public interface Lifecycle {
    /** Initializes application GUI state after window and renderer initialization. */
    default void initialize(LwjglApplicationHost host) {}
    /** Updates application state after input and before current-frame selection. */
    default void update(LwjglApplicationHost host, double deltaSeconds) {}
    /** Runs immediately before clear/render for the selected current frame. */
    default void beforeRender(LwjglApplicationHost host, FramePreparation preparation) {}
    /** Runs after successful publication and before buffer swap. */
    default void afterRender(LwjglApplicationHost host, FramePreparation preparation) {}
    /** Releases caller lifecycle state before owned host resources close. */
    default void shutdown(LwjglApplicationHost host) {}
  }

  private final class HostLoop extends AbstractLwjglApplication {
    private HostLoop() {
      super(navigator, pipeline, renderer, window, services, clock, ownership);
    }

    @Override protected void initializeApplication() { lifecycle.initialize(LwjglApplicationHost.this); }
    @Override protected void update(double deltaSeconds) {
      lifecycle.update(LwjglApplicationHost.this, deltaSeconds);
    }
    @Override protected void beforeRender(FramePreparation preparation) {
      lifecycle.beforeRender(LwjglApplicationHost.this, preparation);
    }
    @Override protected void afterRender(FramePreparation preparation) {
      lifecycle.afterRender(LwjglApplicationHost.this, preparation);
    }
    @Override protected void shutdownApplication() { lifecycle.shutdown(LwjglApplicationHost.this); }
  }
}
