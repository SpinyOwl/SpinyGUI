package com.spinyowl.spinygui.core.backend.renderer.lwjgl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.TestReporter;

/** Verifies first-frame publication waits for a usable native framebuffer. */
class AbstractLwjglApplicationFirstFrameTraceTest {
  @Test
  void waitsForUsableFramebufferBeforePublishingFirstFrame(TestReporter reporter) {
    Trace trace = new Trace(reporter);
    Frame frame = new Frame();
    ScriptedWindow window =
        new ScriptedWindow(
            trace,
            List.of(new Vector2f(640, 480), new Vector2f(640, 480)),
            List.of(new Vector2i(0, 0), new Vector2i(640, 480)));
    RecordingRenderer renderer = new RecordingRenderer(trace);

    application(frame, window, renderer, trace).run();

    assertEquals(
        List.of(new Vector2i(640, 480)),
        renderer.renderedFramebufferSizes,
        trace.dump());
    assertTrue(
        trace.contains("framebuffer-size iteration=2 value=640x480"),
        trace.dump());
  }

  @Test
  void framebufferResizeRequestsRenderWithoutLogicalWindowResize(TestReporter reporter) {
    Trace trace = new Trace(reporter);
    Frame frame = new Frame();
    ScriptedWindow window =
        new ScriptedWindow(
            trace,
            List.of(new Vector2f(640, 480), new Vector2f(640, 480)),
            List.of(new Vector2i(640, 480), new Vector2i(800, 600)));
    RecordingRenderer renderer = new RecordingRenderer(trace);

    application(frame, window, renderer, trace).run();

    assertEquals(
        List.of(new Vector2i(640, 480), new Vector2i(800, 600)),
        renderer.renderedFramebufferSizes,
        trace.dump());
  }

  private static AbstractLwjglApplication application(
      Frame frame, ScriptedWindow window, RecordingRenderer renderer, Trace trace) {
    FramePipeline pipeline =
        new FramePipeline(
            new NoEvents(),
            new NoGuiEvents(),
            target -> {
              trace.add("style revision=%d", frame.revision());
              return StyleImpact.NO_CHANGE;
            },
            () -> TransitionImpact.NO_CHANGE,
            new TraceLayout(trace));
    return new AbstractLwjglApplication(
        frame, pipeline, renderer, window, () -> trace.add("services-close"), () -> 1d) {
      @Override
      protected void beforeRender(FramePreparation preparation) {
        trace.add(
            "before-render revision=%d renderRequired=%s layout=%s transform=%s",
            preparation.revision(),
            preparation.renderRequired(),
            preparation.layoutExecuted(),
            preparation.transformExecuted());
      }
    };
  }

  private static final class ScriptedWindow implements LwjglWindow {
    private final Trace trace;
    private final List<Vector2f> windowSizes;
    private final List<Vector2i> framebufferSizes;
    private int iteration;

    private ScriptedWindow(
        Trace trace, List<Vector2f> windowSizes, List<Vector2i> framebufferSizes) {
      this.trace = trace;
      this.windowSizes = windowSizes;
      this.framebufferSizes = framebufferSizes;
    }

    @Override
    public void initialize() {
      trace.add("window-initialize");
    }

    @Override
    public boolean shouldClose() {
      return iteration >= framebufferSizes.size();
    }

    @Override
    public void pollEvents() {
      iteration++;
      trace.add("poll-events iteration=%d", iteration);
    }

    @Override
    public long handle() {
      return 1;
    }

    @Override
    public Vector2f windowSize() {
      Vector2f size = new Vector2f(windowSizes.get(iteration - 1));
      trace.add("window-size iteration=%d value=%dx%d", iteration, (int) size.x, (int) size.y);
      return size;
    }

    @Override
    public Vector2i framebufferSize() {
      Vector2i size = new Vector2i(framebufferSizes.get(iteration - 1));
      trace.add("framebuffer-size iteration=%d value=%dx%d", iteration, size.x, size.y);
      return size;
    }

    @Override
    public void beginRender(Vector2i framebufferSize) {
      trace.add("begin-render iteration=%d framebuffer=%dx%d", iteration,
          framebufferSize.x, framebufferSize.y);
    }

    @Override
    public void swapBuffers() {
      trace.add("swap-buffers iteration=%d", iteration);
    }

    @Override
    public void close() {
      trace.add("window-close");
    }
  }

  private static final class RecordingRenderer implements Renderer {
    private final Trace trace;
    private final List<Vector2i> renderedFramebufferSizes = new ArrayList<>();

    private RecordingRenderer(Trace trace) {
      this.trace = trace;
    }

    @Override
    public void initialize() {
      trace.add("renderer-initialize");
    }

    @Override
    public void render(
        long window, Vector2fc windowSize, Vector2ic framebufferSize, Frame frame) {
      renderedFramebufferSizes.add(new Vector2i(framebufferSize));
      trace.add(
          "render revision=%d window=%dx%d framebuffer=%dx%d",
          frame.revision(),
          (int) windowSize.x(),
          (int) windowSize.y(),
          framebufferSize.x(),
          framebufferSize.y());
    }

    @Override
    public void destroy() {
      trace.add("renderer-destroy");
    }
  }

  private static final class TraceLayout implements LayoutService {
    private final Trace trace;

    private TraceLayout(Trace trace) {
      this.trace = trace;
    }

    @Override
    public LayoutResult layout(Frame frame) {
      trace.add("layout revision=%d", frame.revision());
      return LayoutResult.converged(1);
    }

    @Override
    public void resolveTransforms(Frame frame) {
      trace.add("transform revision=%d", frame.revision());
    }

    @Override
    public void layoutNode(Node node, LayoutContext context) {}

    @Override
    public void layoutChildNodes(Element element, LayoutContext context) {}
  }

  private static final class NoEvents implements SystemEventProcessor {
    @Override
    public InputImpact processEvents() {
      return InputImpact.NO_IMPACT;
    }

    @Override
    public void push(SystemEvent event) {}

    @Override
    public boolean hasEvents() {
      return false;
    }
  }

  private static final class NoGuiEvents implements EventProcessor {
    @Override
    public void push(Event event) {}

    @Override
    public InputImpact processEvents() {
      return InputImpact.NO_IMPACT;
    }
  }

  private static final class Trace {
    private final TestReporter reporter;
    private final List<String> lines = new ArrayList<>();

    private Trace(TestReporter reporter) {
      this.reporter = reporter;
    }

    private void add(String pattern, Object... arguments) {
      String line = String.format(pattern, arguments);
      lines.add(line);
      reporter.publishEntry("first-frame-trace", line);
    }

    private boolean contains(String line) {
      return lines.contains(line);
    }

    private String dump() {
      return String.join(System.lineSeparator(), lines);
    }
  }
}
