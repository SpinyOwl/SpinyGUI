package com.spinyowl.spinygui.benchmark;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Retains a renderer before initialization and closes it before its native host.
 *
 * <p>A failed first renderer teardown is retried while the host remains valid. If the retry also
 * fails, the first failure is rethrown with the retry failure suppressed and the host is left open;
 * the semantic-owner dependency then prevents a following core font close.
 *
 * @param <R> renderer type
 */
public final class RendererHostLifecycle<R> implements AutoCloseable {
  private final Supplier<? extends R> rendererFactory;
  private final Consumer<? super R> rendererInitializer;
  private final Consumer<? super R> rendererDestroyer;
  private final Runnable hostCloser;

  private R renderer;
  private boolean closed;

  /**
   * Creates one uninitialized renderer/host composition.
   *
   * @param rendererFactory renderer construction
   * @param rendererInitializer renderer initialization
   * @param rendererDestroyer renderer teardown
   * @param hostCloser native host teardown
   */
  public RendererHostLifecycle(
      Supplier<? extends R> rendererFactory,
      Consumer<? super R> rendererInitializer,
      Consumer<? super R> rendererDestroyer,
      Runnable hostCloser) {
    this.rendererFactory = Objects.requireNonNull(rendererFactory, "rendererFactory");
    this.rendererInitializer = Objects.requireNonNull(rendererInitializer, "rendererInitializer");
    this.rendererDestroyer = Objects.requireNonNull(rendererDestroyer, "rendererDestroyer");
    this.hostCloser = Objects.requireNonNull(hostCloser, "hostCloser");
  }

  /**
   * Constructs, retains, and then initializes the renderer.
   *
   * @return initialized renderer
   */
  public R initialize() {
    if (renderer != null || closed) {
      throw new IllegalStateException("Renderer host lifecycle cannot initialize again");
    }
    renderer = Objects.requireNonNull(rendererFactory.get(), "renderer");
    rendererInitializer.accept(renderer);
    return renderer;
  }

  /**
   * Destroys a retained renderer before closing its host; repeated successful close is a no-op.
   */
  @Override
  public void close() {
    if (closed) {
      return;
    }
    if (renderer != null) {
      destroyRendererWithRetry();
    }
    hostCloser.run();
    closed = true;
  }

  private void destroyRendererWithRetry() {
    try {
      rendererDestroyer.accept(renderer);
    } catch (RuntimeException | Error firstFailure) {
      try {
        rendererDestroyer.accept(renderer);
      } catch (RuntimeException | Error retryFailure) {
        firstFailure.addSuppressed(retryFailure);
        throw firstFailure;
      }
    }
  }
}
