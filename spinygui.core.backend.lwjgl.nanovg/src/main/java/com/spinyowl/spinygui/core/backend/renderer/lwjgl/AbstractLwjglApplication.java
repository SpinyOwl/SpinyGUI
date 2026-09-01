package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import org.joml.Vector2f;
import org.joml.Vector2i;

/**
 * Reusable canonical LWJGL host loop with injected services and narrow lifecycle hooks.
 * Presentation waits for a usable framebuffer and repaints after framebuffer-size changes.
 */
public abstract class AbstractLwjglApplication {
  /** Navigator resolved after every update to select that iteration's frame. */
  private final FrameNavigator navigator;
  private final FramePipeline pipeline;
  private final Renderer renderer;
  private final LwjglWindow window;
  private final AutoCloseable services;
  private final DoubleSupplier clock;
  /** Explicit resource-close policy retained for the single application run. */
  private final ResourceOwnership ownership;
  private boolean started;

  protected AbstractLwjglApplication(
      Frame frame,
      FramePipeline pipeline,
      Renderer renderer,
      LwjglWindow window,
      AutoCloseable services,
      DoubleSupplier clock) {
    this(
        new FrameNavigator(Objects.requireNonNull(frame, "frame"), 1),
        pipeline,
        renderer,
        window,
        services,
        clock,
        ResourceOwnership.legacyOwned());
  }

  /** Creates a navigable application loop with explicit resource ownership. */
  protected AbstractLwjglApplication(
      FrameNavigator navigator,
      FramePipeline pipeline,
      Renderer renderer,
      LwjglWindow window,
      AutoCloseable services,
      DoubleSupplier clock,
      ResourceOwnership ownership) {
    this.navigator = Objects.requireNonNull(navigator, "navigator");
    this.pipeline = Objects.requireNonNull(pipeline, "pipeline");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.window = Objects.requireNonNull(window, "window");
    this.services = Objects.requireNonNull(services, "services");
    this.clock = Objects.requireNonNull(clock, "clock");
    this.ownership = Objects.requireNonNull(ownership, "ownership");
  }

  public final void run() {
    if (started) throw new IllegalStateException("Application instances are single-use");
    started = true;
    boolean windowInitialized = false;
    boolean rendererInitialized = false;
    boolean windowAttempted = false;
    boolean rendererAttempted = false;
    Throwable failure = null;
    try {
      windowAttempted = true;
      window.initialize();
      windowInitialized = true;
      rendererAttempted = true;
      renderer.initialize();
      rendererInitialized = true;
      initializeApplication();
      double previousTime = clock.getAsDouble();
      Vector2i presentedFramebufferSize = null;
      while (!window.shouldClose()) {
        window.pollEvents();
        pipeline.processInput();

        double currentTime = clock.getAsDouble();
        double deltaSeconds = Math.max(0d, currentTime - previousTime);
        previousTime = currentTime;
        update(deltaSeconds);

        Frame frame = navigator.currentFrame();
        Vector2f windowSize = window.windowSize();
        Vector2i framebufferSize = window.framebufferSize();
        frame.frameSize(windowSize.x, windowSize.y);

        if (framebufferSize.x <= 0 || framebufferSize.y <= 0) {
          presentedFramebufferSize = null;
          continue;
        }
        if (presentedFramebufferSize == null
            || !presentedFramebufferSize.equals(framebufferSize)) {
          frame.invalidatePaint();
        }

        FramePreparation preparation = pipeline.prepareFrame(frame);
        if (!preparation.renderable()) throw new FramePreparationException(preparation);
        if (!preparation.renderRequired()) continue;

        beforeRender(preparation);
        window.beginRender(framebufferSize);
        renderer.render(window.handle(), windowSize, framebufferSize, frame);
        if (!pipeline.publishRendered(frame, preparation)) {
          throw new IllegalStateException("Rendered frame was superseded before publication");
        }
        afterRender(preparation);
        window.swapBuffers();
        presentedFramebufferSize = new Vector2i(framebufferSize);
      }
    } catch (RuntimeException | Error runFailure) {
      failure = runFailure;
      throw runFailure;
    } finally {
      RuntimeException cleanupFailure = cleanup(null, this::shutdownApplication);
      if (ownership.closeRenderer()
          && (rendererInitialized || ownership.closeAfterInitializationFailure() && rendererAttempted)) {
        cleanupFailure = cleanup(cleanupFailure, renderer::destroy);
      }
      boolean closeWindow = ownership.closeWindow()
          && (windowInitialized || ownership.closeAfterInitializationFailure() && windowAttempted);
      if (ownership.closeAfterInitializationFailure()) {
        if (closeWindow) cleanupFailure = cleanup(cleanupFailure, window::close);
        if (ownership.closeServices()) cleanupFailure = cleanup(cleanupFailure, this::closeServices);
      } else {
        if (ownership.closeServices()) cleanupFailure = cleanup(cleanupFailure, this::closeServices);
        if (closeWindow) cleanupFailure = cleanup(cleanupFailure, window::close);
      }
      if (cleanupFailure != null) {
        if (failure != null) failure.addSuppressed(cleanupFailure);
        else throw cleanupFailure;
      }
    }
  }

  /** Returns the current frame at call time, preserving the legacy single-frame accessor. */
  protected final Frame frame() { return navigator.currentFrame(); }
  /** Returns the navigator shared with callback capture and iteration frame selection. */
  protected final FrameNavigator navigator() { return navigator; }
  protected final FramePipeline pipeline() { return pipeline; }
  protected void initializeApplication() { }
  protected void update(double deltaSeconds) { }
  protected void beforeRender(FramePreparation preparation) { }
  protected void afterRender(FramePreparation preparation) { }
  protected void shutdownApplication() { }

  private void closeServices() {
    try {
      services.close();
    } catch (RuntimeException failure) {
      throw failure;
    } catch (Exception failure) {
      throw new IllegalStateException("Failed to close application services", failure);
    }
  }

  private static RuntimeException cleanup(RuntimeException failure, Runnable action) {
    try {
      action.run();
      return failure;
    } catch (RuntimeException cleanupFailure) {
      if (failure == null) return cleanupFailure;
      failure.addSuppressed(cleanupFailure);
      return failure;
    }
  }

  /** Explicitly distinguishes host-owned resources from injected caller-owned resources. */
  public record ResourceOwnership(
      boolean closeRenderer,
      boolean closeServices,
      boolean closeWindow,
      boolean closeAfterInitializationFailure) {

    /** Host owns all resources and closes attempted resources after partial initialization. */
    public static ResourceOwnership owned() {
      return new ResourceOwnership(true, true, true, true);
    }

    /** Caller retains all injected resources; the loop never closes them. */
    public static ResourceOwnership injected() {
      return new ResourceOwnership(false, false, false, false);
    }

    private static ResourceOwnership legacyOwned() {
      return new ResourceOwnership(true, true, true, false);
    }
  }
}
