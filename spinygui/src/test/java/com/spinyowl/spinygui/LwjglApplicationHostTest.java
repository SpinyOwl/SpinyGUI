package com.spinyowl.spinygui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.FrameNavigator;
import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.animation.TransitionImpact;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.AbstractLwjglApplication;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglApplicationConfiguration;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.LwjglWindow;
import com.spinyowl.spinygui.core.event.Event;
import com.spinyowl.spinygui.core.event.processor.EventProcessor;
import com.spinyowl.spinygui.core.event.processor.InputImpact;
import com.spinyowl.spinygui.core.layout.LayoutContext;
import com.spinyowl.spinygui.core.layout.LayoutResult;
import com.spinyowl.spinygui.core.layout.LayoutService;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.manager.StyleImpact;
import com.spinyowl.spinygui.core.system.event.SystemEvent;
import com.spinyowl.spinygui.core.system.event.processor.SystemEventProcessor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.junit.jupiter.api.Test;

class LwjglApplicationHostTest {

  @Test
  void preRunCloseDestroysTransferredRendererBeforeServicesExactlyOnce() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    LwjglApplicationHost host = new LwjglApplicationHost(
        new FrameNavigator(frame, 1),
        pipeline(calls),
        new RecordingRenderer(calls),
        new RecordingWindow(calls),
        () -> calls.add("services-close"),
        () -> 1d,
        AbstractLwjglApplication.ResourceOwnership.owned(),
        new LwjglApplicationHost.Lifecycle() {},
        null,
        null);

    host.close();
    host.close();

    assertEquals(List.of("renderer-close", "services-close"), calls);
  }

  @Test
  void invalidHistoryCapacityDoesNotCreateServicesOrMutateRenderer() {
    List<String> calls = new ArrayList<>();
    AtomicInteger serviceCreations = new AtomicInteger();

    assertThrows(
        IllegalArgumentException.class,
        () -> LwjglApplicationHost.owned(
            LwjglApplicationConfiguration.windowed(640, 480, "test"),
            new Frame(),
            0,
            new RecordingRenderer(calls),
            new LwjglApplicationHost.Lifecycle() {},
            (frame, renderer) -> {
              serviceCreations.incrementAndGet();
              throw new AssertionError("services must not be created");
            }));

    assertEquals(0, serviceCreations.get());
    assertEquals(List.of(), calls);
  }

  @Test
  void navigationDuringUpdateSelectsNewFrameForSizePrepareRenderPublishAndSwap() {
    List<String> calls = new ArrayList<>();
    Frame first = new Frame();
    Frame second = new Frame();
    FrameNavigator navigator = new FrameNavigator(first, 3);
    RecordingWindow window = new RecordingWindow(calls);
    RecordingRenderer renderer = new RecordingRenderer(calls);
    FramePipeline pipeline = pipeline(calls);
    AutoCloseable services = () -> calls.add("services-close");
    LwjglApplicationHost host = LwjglApplicationHost.injected(
        navigator, pipeline, renderer, window, services, () -> 1d,
        new LwjglApplicationHost.Lifecycle() {
          @Override public void initialize(LwjglApplicationHost host) { calls.add("initialize"); }
          @Override public void update(LwjglApplicationHost host, double deltaSeconds) {
            calls.add("update");
            host.navigator().navigate(second);
          }
          @Override public void beforeRender(
              LwjglApplicationHost host, FramePreparation preparation) {
            calls.add("before");
          }
          @Override public void afterRender(
              LwjglApplicationHost host, FramePreparation preparation) {
            calls.add("after");
          }
          @Override public void shutdown(LwjglApplicationHost host) { calls.add("shutdown"); }
        });

    host.run();

    assertEquals(
        List.of("window-init", "renderer-init", "initialize", "poll", "system", "gui",
            "update", "window-size", "framebuffer-size", "style", "transition", "layout",
            "transform", "before", "begin", "render", "after", "swap", "shutdown"),
        calls);
    assertSame(second, renderer.renderedFrame);
    assertEquals(new Vector2f(), first.frameSize());
    assertEquals(new Vector2f(640, 480), second.frameSize());
    assertEquals(false, second.invalidation().paintDirty());
  }

  @Test
  void injectedHostNeverClosesCallerResourcesAfterInitializationFailure() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    RecordingWindow window = new RecordingWindow(calls) {
      @Override public void initialize() {
        calls.add("window-init");
        throw new IllegalStateException("window failed");
      }
    };
    Renderer renderer = new RecordingRenderer(calls);
    AutoCloseable services = () -> calls.add("services-close");
    LwjglApplicationHost host = LwjglApplicationHost.injected(
        new FrameNavigator(frame, 1), pipeline(calls), renderer, window, services, () -> 1d,
        new LwjglApplicationHost.Lifecycle() {
          @Override public void shutdown(LwjglApplicationHost host) { calls.add("shutdown"); }
        });

    assertThrows(IllegalStateException.class, host::run);

    assertEquals(List.of("window-init", "shutdown"), calls);
  }

  private static FramePipeline pipeline(List<String> calls) {
    return new FramePipeline(
        new SystemEvents(calls),
        new GuiEvents(calls),
        frame -> { calls.add("style"); return StyleImpact.LAYOUT; },
        () -> { calls.add("transition"); return TransitionImpact.NO_CHANGE; },
        new RecordingLayout(calls));
  }

  private static class RecordingWindow implements LwjglWindow {
    private final List<String> calls;
    private boolean close;
    private RecordingWindow(List<String> calls) { this.calls = calls; }
    @Override public void initialize() { calls.add("window-init"); }
    @Override public boolean shouldClose() { return close; }
    @Override public void pollEvents() { calls.add("poll"); }
    @Override public long handle() { return 1L; }
    @Override public Vector2f windowSize() { calls.add("window-size"); return new Vector2f(640, 480); }
    @Override public Vector2i framebufferSize() {
      calls.add("framebuffer-size");
      return new Vector2i(1280, 960);
    }
    @Override public void beginRender(Vector2i framebufferSize) { calls.add("begin"); }
    @Override public void swapBuffers() { calls.add("swap"); close = true; }
    @Override public void close() { calls.add("window-close"); }
  }

  private static class RecordingRenderer implements Renderer {
    private final List<String> calls;
    private Frame renderedFrame;
    private RecordingRenderer(List<String> calls) { this.calls = calls; }
    @Override public void initialize() { calls.add("renderer-init"); }
    @Override public void render(
        long window, Vector2fc windowSize, Vector2ic framebufferSize, Frame frame) {
      calls.add("render");
      renderedFrame = frame;
    }
    @Override public void destroy() { calls.add("renderer-close"); }
  }

  private static final class SystemEvents implements SystemEventProcessor {
    private final List<String> calls;
    private SystemEvents(List<String> calls) { this.calls = calls; }
    @Override public InputImpact processEvents() { calls.add("system"); return InputImpact.NO_IMPACT; }
    @Override public void push(SystemEvent event) {}
    @Override public boolean hasEvents() { return false; }
  }

  private static final class GuiEvents implements EventProcessor {
    private final List<String> calls;
    private GuiEvents(List<String> calls) { this.calls = calls; }
    @Override public void push(Event event) {}
    @Override public InputImpact processEvents() { calls.add("gui"); return InputImpact.NO_IMPACT; }
  }

  private static final class RecordingLayout implements LayoutService {
    private final List<String> calls;
    private RecordingLayout(List<String> calls) { this.calls = calls; }
    @Override public LayoutResult layout(Frame frame) {
      calls.add("layout");
      return LayoutResult.converged(1);
    }
    @Override public void resolveTransforms(Frame frame) { calls.add("transform"); }
    @Override public void layoutNode(Node node, LayoutContext context) {}
    @Override public void layoutChildNodes(Element element, LayoutContext context) {}
  }
}
