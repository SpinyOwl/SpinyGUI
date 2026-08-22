package com.spinyowl.spinygui.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.animation.TransitionImpact;
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
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class FramePipelineTest {
  @Test
  void executesOwnerPhasesInCanonicalOrder() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    FramePipeline pipeline = pipeline(
        calls,
        InputImpact.NO_IMPACT,
        InputImpact.NO_IMPACT,
        target -> { calls.add("style"); return StyleImpact.LAYOUT; },
        () -> { calls.add("transition"); return TransitionImpact.TRANSFORM; },
        new RecordingLayout(calls, LayoutResult.converged(1)));

    pipeline.processInput();
    calls.add("update");
    FramePreparation result = pipeline.prepareFrame(frame);

    assertEquals(
        List.of("system", "gui", "update", "style", "transition", "layout", "transform"),
        calls);
    assertTrue(result.renderable());
    assertTrue(result.renderRequired());
    assertTrue(pipeline.publishRendered(frame, result));
    assertFalse(frame.invalidation().paintDirty());
  }

  @Test
  void sourceMutationDuringPreparationSupersedesOutput() {
    Frame frame = new Frame();
    FramePipeline pipeline = pipeline(
        new ArrayList<>(),
        InputImpact.NO_IMPACT,
        InputImpact.NO_IMPACT,
        target -> { target.style("color: red"); return StyleImpact.PAINT_ONLY; },
        () -> TransitionImpact.NO_CHANGE,
        new RecordingLayout(new ArrayList<>(), LayoutResult.converged(1)));

    FramePreparation result = pipeline.prepareFrame(frame);

    assertEquals(FramePreparation.Status.SUPERSEDED, result.status());
    assertFalse(result.renderable());
  }

  @Test
  void unconvergedLayoutIsNotRenderable() {
    FramePipeline pipeline = pipeline(
        new ArrayList<>(), InputImpact.NO_IMPACT, InputImpact.NO_IMPACT,
        frame -> StyleImpact.NO_CHANGE,
        () -> TransitionImpact.NO_CHANGE,
        new RecordingLayout(new ArrayList<>(), LayoutResult.unconverged(4)));

    FramePreparation result = pipeline.prepareFrame(new Frame());

    assertEquals(FramePreparation.Status.UNCONVERGED, result.status());
    assertFalse(result.renderable());
  }

  @Test
  void idlePreparationSkipsStyleLayoutAndTransform() {
    List<String> calls = new ArrayList<>();
    Frame frame = new Frame();
    FramePipeline pipeline = pipeline(
        calls, InputImpact.NO_IMPACT, InputImpact.NO_IMPACT,
        target -> { calls.add("style"); return StyleImpact.NO_CHANGE; },
        () -> { calls.add("transition"); return TransitionImpact.NO_CHANGE; },
        new RecordingLayout(calls, LayoutResult.converged(1)));
    FramePreparation first = pipeline.prepareFrame(frame);
    pipeline.publishRendered(frame, first);
    calls.clear();

    FramePreparation second = pipeline.prepareFrame(frame);

    assertEquals(List.of("transition"), calls);
    assertFalse(second.renderRequired());
  }

  @Test
  void serviceFailureIsAnExplicitNonRenderableOutcome() {
    IllegalStateException failure = new IllegalStateException("style failed");
    FramePipeline pipeline = pipeline(
        new ArrayList<>(), InputImpact.NO_IMPACT, InputImpact.NO_IMPACT,
        frame -> { throw failure; },
        () -> TransitionImpact.NO_CHANGE,
        new RecordingLayout(new ArrayList<>(), LayoutResult.converged(1)));

    FramePreparation result = pipeline.prepareFrame(new Frame());

    assertEquals(FramePreparation.Status.FAILED, result.status());
    assertFalse(result.renderable());
    assertEquals(failure, result.failure());
  }

  @Test
  void reentrantPreparationIsRejectedAsAnExplicitFailure() {
    AtomicReference<FramePipeline> reference = new AtomicReference<>();
    FramePipeline pipeline = pipeline(
        new ArrayList<>(), InputImpact.NO_IMPACT, InputImpact.NO_IMPACT,
        frame -> {
          reference.get().prepareFrame(frame);
          return StyleImpact.NO_CHANGE;
        },
        () -> TransitionImpact.NO_CHANGE,
        new RecordingLayout(new ArrayList<>(), LayoutResult.converged(1)));
    reference.set(pipeline);

    FramePreparation result = pipeline.prepareFrame(new Frame());

    assertEquals(FramePreparation.Status.FAILED, result.status());
    assertFalse(result.renderable());
    assertInstanceOf(IllegalStateException.class, result.failure());
    assertEquals("FramePipeline is non-reentrant", result.failure().getMessage());
  }

  private static FramePipeline pipeline(
      List<String> calls,
      InputImpact systemImpact,
      InputImpact guiImpact,
      com.spinyowl.spinygui.core.style.manager.StyleManager styles,
      com.spinyowl.spinygui.core.animation.TransitionService transitions,
      LayoutService layout) {
    return new FramePipeline(
        new StubSystemEvents(calls, systemImpact),
        new StubGuiEvents(calls, guiImpact),
        styles,
        transitions,
        layout);
  }

  private static final class StubGuiEvents implements EventProcessor {
    private final List<String> calls;
    private final InputImpact impact;
    private StubGuiEvents(List<String> calls, InputImpact impact) {
      this.calls = calls; this.impact = impact;
    }
    @Override public void push(Event event) { }
    @Override public InputImpact processEvents() { calls.add("gui"); return impact; }
  }

  private static final class StubSystemEvents implements SystemEventProcessor {
    private final List<String> calls;
    private final InputImpact impact;
    private StubSystemEvents(List<String> calls, InputImpact impact) {
      this.calls = calls; this.impact = impact;
    }
    @Override public InputImpact processEvents() { calls.add("system"); return impact; }
    @Override public void push(SystemEvent event) { }
    @Override public boolean hasEvents() { return false; }
  }

  private static final class RecordingLayout implements LayoutService {
    private final List<String> calls;
    private final LayoutResult result;
    private RecordingLayout(List<String> calls, LayoutResult result) {
      this.calls = calls; this.result = result;
    }
    @Override public LayoutResult layout(Frame frame) { calls.add("layout"); return result; }
    @Override public void resolveTransforms(Frame frame) { calls.add("transform"); }
    @Override public void layoutNode(Node node, LayoutContext context) { }
    @Override public void layoutChildNodes(Element element, LayoutContext context) { }
  }
}
