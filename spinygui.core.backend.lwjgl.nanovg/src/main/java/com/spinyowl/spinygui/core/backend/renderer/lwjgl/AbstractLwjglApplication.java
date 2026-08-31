package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.node.Frame;
import java.util.Objects;
import java.util.function.DoubleSupplier;
import org.joml.Vector2f;
import org.joml.Vector2i;

/** Reusable canonical LWJGL host loop with injected services and narrow lifecycle hooks. */
public abstract class AbstractLwjglApplication {
  private final Frame frame;
  private final FramePipeline pipeline;
  private final Renderer renderer;
  private final LwjglWindow window;
  private final AutoCloseable services;
  private final DoubleSupplier clock;
  private boolean started;

  protected AbstractLwjglApplication(
      Frame frame,
      FramePipeline pipeline,
      Renderer renderer,
      LwjglWindow window,
      AutoCloseable services,
      DoubleSupplier clock) {
    this.frame = Objects.requireNonNull(frame, "frame");
    this.pipeline = Objects.requireNonNull(pipeline, "pipeline");
    this.renderer = Objects.requireNonNull(renderer, "renderer");
    this.window = Objects.requireNonNull(window, "window");
    this.services = Objects.requireNonNull(services, "services");
    this.clock = Objects.requireNonNull(clock, "clock");
  }

  public final void run() {
    if (started) throw new IllegalStateException("Application instances are single-use");
    started = true;
    boolean windowInitialized = false;
    boolean rendererInitialized = false;
    try {
      window.initialize();
      windowInitialized = true;
      renderer.initialize();
      rendererInitialized = true;
      initializeApplication();
      double previousTime = clock.getAsDouble();
      while (!window.shouldClose()) {
        window.pollEvents();
        pipeline.processInput();

        double currentTime = clock.getAsDouble();
        double deltaSeconds = Math.max(0d, currentTime - previousTime);
        previousTime = currentTime;
        update(deltaSeconds);

        Vector2f windowSize = window.windowSize();
        Vector2i framebufferSize = window.framebufferSize();
        frame.frameSize(windowSize.x, windowSize.y);

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
      }
    } finally {
      try {
        shutdownApplication();
      } finally {
        try {
          if (rendererInitialized) renderer.destroy();
        } finally {
          try {
            closeServices();
          } finally {
            if (windowInitialized) window.close();
          }
        }
      }
    }
  }

  protected final Frame frame() { return frame; }
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
}
