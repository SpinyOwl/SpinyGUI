package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.diagnostic.NvgDiagnosticCounter;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgClipStack.ClipSink;
import com.spinyowl.spinygui.core.diagnostic.DiagnosticSession;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgClipStackTest {

  @Test
  void create_doesNotClipVisibleAncestors() {
    RecordingClipSink sink = new RecordingClipSink();
    NvgClipStack clipStack = new NvgClipStack(sink);
    Element parent = element(10, 20, 100, 80, Overflow.VISIBLE);
    Element child = element(15, 25, 30, 20, Overflow.VISIBLE);
    parent.addChild(child);
    child.offsetParent(parent);

    clipStack.create(1, child);

    assertTrue(sink.calls().isEmpty());
  }

  @Test
  void create_clipsHiddenAutoAndScrollAncestorsToPaddingBox() {
    RecordingClipSink sink = new RecordingClipSink();
    NvgClipStack clipStack = new NvgClipStack(sink);
    Element hidden = element(10, 20, 100, 80, Overflow.HIDDEN);
    hidden.box().padding().left(4);
    hidden.box().padding().top(5);
    hidden.box().border().left(1);
    hidden.box().border().top(2);
    Element auto = element(30, 40, 70, 60, Overflow.AUTO);
    auto.box().padding().left(3);
    auto.box().padding().top(4);
    Element scroll = element(50, 60, 40, 30, Overflow.SCROLL);
    Element child = element(0, 0, 10, 10, Overflow.VISIBLE);
    hidden.presentationState().setValue(OPACITY, 0.5f);
    auto.presentationState().setValue(BACKGROUND_COLOR, Color.BLUE);
    child.presentationState().setValue(COLOR, Color.RED);
    hidden.addChild(auto);
    auto.addChild(scroll);
    scroll.addChild(child);
    auto.offsetParent(hidden);
    scroll.offsetParent(auto);
    child.offsetParent(scroll);

    clipStack.create(1, child);

    assertEquals(
        List.of(
            "scissor(1,6.0,15.0,104.0,85.0)",
            "intersect(1,32.0,49.0,73.0,64.0)",
            "intersect(1,82.0,109.0,40.0,30.0)"),
        sink.calls());

    sink.clear();
    hidden.presentationState().clearValues();
    auto.presentationState().clearValues();
    child.presentationState().clearValues();
    clipStack.create(1, child);

    assertEquals(
        List.of(
            "scissor(1,6.0,15.0,104.0,85.0)",
            "intersect(1,32.0,49.0,73.0,64.0)",
            "intersect(1,82.0,109.0,40.0,30.0)"),
        sink.calls());
  }

  @Test
  void reset_delegatesToSink() {
    RecordingClipSink sink = new RecordingClipSink();
    NvgClipStack clipStack = new NvgClipStack(sink);

    clipStack.reset(7);

    assertEquals(List.of("reset(7)"), sink.calls());
  }

  @Test
  void diagnosticsCountClipCommandsWithoutChangingSinkOrdering() {
    Element outer = element(10, 20, 100, 80, Overflow.HIDDEN);
    Element inner = element(30, 40, 70, 60, Overflow.AUTO);
    Element child = element(0, 0, 10, 10, Overflow.VISIBLE);
    outer.addChild(inner);
    inner.addChild(child);
    inner.offsetParent(outer);
    child.offsetParent(inner);
    RecordingClipSink disabledSink = new RecordingClipSink();
    NvgClipStack disabled = new NvgClipStack(disabledSink);
    RecordingClipSink enabledSink = new RecordingClipSink();
    DiagnosticSession diagnostics =
        DiagnosticSession.enabled(List.of(NvgDiagnosticCounter.values()));
    NvgClipStack enabled = new NvgClipStack(enabledSink, diagnostics);

    disabled.create(9, child);
    disabled.reset(9);
    enabled.create(9, child);
    enabled.reset(9);

    assertEquals(disabledSink.calls(), enabledSink.calls());
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.SCISSOR_CALLS));
    assertEquals(
        1, diagnostics.snapshot().value(NvgDiagnosticCounter.INTERSECT_SCISSOR_CALLS));
    assertEquals(1, diagnostics.snapshot().value(NvgDiagnosticCounter.RESET_SCISSOR_CALLS));
  }

  private Element element(float x, float y, float width, float height, Overflow overflow) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    element.resolvedStyle().overflowX(overflow);
    element.resolvedStyle().overflowY(overflow);
    return element;
  }

  private static final class RecordingClipSink implements ClipSink {

    private final List<String> calls = new ArrayList<>();

    @Override
    public void scissor(long context, float x, float y, float width, float height) {
      calls.add("scissor(%d,%.1f,%.1f,%.1f,%.1f)".formatted(context, x, y, width, height));
    }

    @Override
    public void intersectScissor(long context, float x, float y, float width, float height) {
      calls.add("intersect(%d,%.1f,%.1f,%.1f,%.1f)".formatted(context, x, y, width, height));
    }

    @Override
    public void reset(long context) {
      calls.add("reset(%d)".formatted(context));
    }

    List<String> calls() {
      return calls;
    }

    void clear() {
      calls.clear();
    }
  }
}
