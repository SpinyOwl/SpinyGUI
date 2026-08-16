package com.spinyowl.spinygui.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RendererHostLifecycleTest {

  @DisplayName("P4 T3: init failure retains renderer and retries delete before host and core close")
  @Test
  void initializationFailureRetainsRendererForDeleteRetryBeforeHostAndCoreClose() {
    List<String> events = new ArrayList<>();
    Object renderer = new Object();
    AtomicInteger destroys = new AtomicInteger();
    AtomicReference<Object> destroyedRenderer = new AtomicReference<>();
    RendererHostLifecycle<Object> lifecycle =
        new RendererHostLifecycle<>(
            () -> {
              events.add("CREATE_RENDERER");
              return renderer;
            },
            retained -> {
              assertSame(renderer, retained);
              events.add("INITIALIZE_RENDERER");
              throw new IllegalStateException("injected initialize failure");
            },
            retained -> {
              destroyedRenderer.set(retained);
              int attempt = destroys.incrementAndGet();
              events.add("DESTROY_RENDERER_" + attempt);
              if (attempt == 1) {
                throw new IllegalStateException("injected first delete failure");
              }
            },
            () -> events.add("CLOSE_GL_HOST"));

    assertThrows(IllegalStateException.class, lifecycle::initialize);
    lifecycle.close();
    events.add("CLOSE_FONT_SERVICE");
    lifecycle.close();

    assertSame(renderer, destroyedRenderer.get());
    assertEquals(2, destroys.get());
    assertEquals(
        List.of(
            "CREATE_RENDERER",
            "INITIALIZE_RENDERER",
            "DESTROY_RENDERER_1",
            "DESTROY_RENDERER_2",
            "CLOSE_GL_HOST",
            "CLOSE_FONT_SERVICE"),
        events);
  }

  @DisplayName("P4 T3: two delete failures preserve the first failure and leave the host open")
  @Test
  void doubleDeleteFailurePreservesOriginalFailureAndLeavesHostOpen() {
    Object renderer = new Object();
    IllegalStateException firstDeleteFailure =
        new IllegalStateException("injected first delete failure");
    AssertionError retryDeleteFailure = new AssertionError("injected retry delete failure");
    AtomicInteger destroys = new AtomicInteger();
    AtomicInteger hostCloses = new AtomicInteger();
    RendererHostLifecycle<Object> lifecycle =
        new RendererHostLifecycle<>(
            () -> renderer,
            retained -> {
              assertSame(renderer, retained);
              throw new IllegalStateException("injected initialize failure");
            },
            retained -> {
              assertSame(renderer, retained);
              if (destroys.incrementAndGet() == 1) {
                throw firstDeleteFailure;
              }
              throw retryDeleteFailure;
            },
            hostCloses::incrementAndGet);

    assertThrows(IllegalStateException.class, lifecycle::initialize);
    IllegalStateException failure =
        assertThrows(IllegalStateException.class, lifecycle::close);

    assertSame(firstDeleteFailure, failure);
    assertEquals(1, failure.getSuppressed().length);
    assertSame(retryDeleteFailure, failure.getSuppressed()[0]);
    assertEquals(2, destroys.get());
    assertEquals(0, hostCloses.get());
  }
}
