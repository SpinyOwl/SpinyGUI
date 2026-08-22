package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.FramePipeline;
import com.spinyowl.spinygui.core.FramePreparation;
import com.spinyowl.spinygui.core.animation.TransitionImpact;
import com.spinyowl.spinygui.core.backend.renderer.Renderer;
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
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector2ic;
import org.junit.jupiter.api.Test;

class AbstractLwjglApplicationTest {
  @Test
  void runsCanonicalLoopAndClosesOwnedResources() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    LwjglWindow window = new RecordingWindow(calls);
    Renderer renderer = new RecordingRenderer(calls);
    FramePipeline pipeline = new FramePipeline(
        new SystemEvents(calls),
        new GuiEvents(calls),
        target -> { calls.add("style"); return StyleImpact.LAYOUT; },
        () -> { calls.add("transition"); return TransitionImpact.NO_CHANGE; },
        new RecordingLayout(calls, LayoutResult.converged(1)));
    AbstractLwjglApplication application =
        new AbstractLwjglApplication(frame, pipeline, renderer, window, () -> 1d) {
          @Override protected void update(double deltaSeconds) { calls.add("update"); }
          @Override protected void beforeRender(FramePreparation preparation) { calls.add("before"); }
          @Override protected void afterRender(FramePreparation preparation) { calls.add("after"); }
        };

    application.run();

    assertEquals(
        List.of("window-init", "renderer-init", "poll", "system", "gui", "update",
            "style", "transition", "layout", "transform", "before", "begin", "render",
            "after", "swap", "renderer-close", "window-close"),
        calls);
  }

  @Test
  void preparationFailurePreventsPresentationAndStillClosesResources() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    LwjglWindow window = new RecordingWindow(calls);
    Renderer renderer = new RecordingRenderer(calls);
    FramePipeline pipeline = new FramePipeline(
        new SystemEvents(calls),
        new GuiEvents(calls),
        target -> { calls.add("style"); return StyleImpact.LAYOUT; },
        () -> { calls.add("transition"); return TransitionImpact.NO_CHANGE; },
        new RecordingLayout(calls, LayoutResult.unconverged(4)));
    AbstractLwjglApplication application =
        new AbstractLwjglApplication(frame, pipeline, renderer, window, () -> 1d) { };

    FramePreparationException failure =
        assertThrows(FramePreparationException.class, application::run);

    assertEquals(FramePreparation.Status.UNCONVERGED, failure.preparation().status());
    assertEquals(
        List.of("window-init", "renderer-init", "poll", "system", "gui", "style",
            "transition", "layout", "renderer-close", "window-close"),
        calls);
  }

  private static final class RecordingWindow implements LwjglWindow {
    private final List<String> calls;
    private boolean closed;
    private RecordingWindow(List<String> calls) { this.calls = calls; }
    @Override public void initialize() { calls.add("window-init"); }
    @Override public boolean shouldClose() { return closed; }
    @Override public void pollEvents() { calls.add("poll"); }
    @Override public long handle() { return 1; }
    @Override public Vector2f windowSize() { return new Vector2f(640, 480); }
    @Override public Vector2i framebufferSize() { return new Vector2i(640, 480); }
    @Override public void beginRender(Vector2i size) { calls.add("begin"); }
    @Override public void swapBuffers() { calls.add("swap"); closed = true; }
    @Override public void close() { calls.add("window-close"); }
  }

  private static final class RecordingRenderer implements Renderer {
    private final List<String> calls;
    private RecordingRenderer(List<String> calls) { this.calls = calls; }
    @Override public void initialize() { calls.add("renderer-init"); }
    @Override public void render(long window, Vector2fc windowSize, Vector2ic bufferSize, Frame frame) {
      calls.add("render");
    }
    @Override public void destroy() { calls.add("renderer-close"); }
  }

  private static final class SystemEvents implements SystemEventProcessor {
    private final List<String> calls;
    private SystemEvents(List<String> calls) { this.calls = calls; }
    @Override public InputImpact processEvents() { calls.add("system"); return InputImpact.NO_IMPACT; }
    @Override public void push(SystemEvent event) { }
    @Override public boolean hasEvents() { return false; }
  }

  private static final class GuiEvents implements EventProcessor {
    private final List<String> calls;
    private GuiEvents(List<String> calls) { this.calls = calls; }
    @Override public void push(Event event) { }
    @Override public InputImpact processEvents() { calls.add("gui"); return InputImpact.NO_IMPACT; }
  }

  private static final class RecordingLayout implements LayoutService {
    private final List<String> calls;
    private final LayoutResult result;
    private RecordingLayout(List<String> calls, LayoutResult result) {
      this.calls = calls;
      this.result = result;
    }
    @Override public LayoutResult layout(Frame frame) { calls.add("layout"); return result; }
    @Override public void resolveTransforms(Frame frame) { calls.add("transform"); }
    @Override public void layoutNode(Node node, LayoutContext context) { }
    @Override public void layoutChildNodes(Element element, LayoutContext context) { }
  }
}
