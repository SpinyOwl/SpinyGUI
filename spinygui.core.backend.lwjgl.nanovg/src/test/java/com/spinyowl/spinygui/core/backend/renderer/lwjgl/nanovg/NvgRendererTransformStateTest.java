package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class NvgRendererTransformStateTest {

  @Test
  void renderLayoutTree_balancesNestedAndSiblingTransformStates() {
    NvgRenderer renderer = new NvgRenderer();
    RecordingTransformStates states = new RecordingTransformStates();
    renderer.transformStateFactory(states);
    renderer.subtreeContentStateFactory((context, element) -> () -> {});
    renderer.subtreeContentRenderer((node, context) -> states.record("paint(" + node.nodeName() + ")"));

    Frame frame = new Frame();
    Element parent = element("parent", 10);
    Element child = element("child", 20);
    Element sibling = element("sibling", 30);
    frame.addChildren(parent, sibling);
    parent.addChild(child);
    frame.layoutChildNodes(List.of(parent, sibling));
    parent.layoutChildNodes(List.of(child));

    renderer.renderLayoutTree(frame);

    assertEquals(
        List.of(
            "apply(winframe,0.0)",
            "paint(winframe)",
            "apply(parent,10.0)",
            "paint(parent)",
            "apply(child,20.0)",
            "paint(child)",
            "restore(child)",
            "restore(parent)",
            "apply(sibling,30.0)",
            "paint(sibling)",
            "restore(sibling)",
            "restore(winframe)"),
        states.calls());
  }

  @Test
  void renderLayoutTree_restoresEveryOpenTransformWhenChildPaintThrows() {
    NvgRenderer renderer = new NvgRenderer();
    RecordingTransformStates states = new RecordingTransformStates();
    renderer.transformStateFactory(states);
    renderer.subtreeContentStateFactory((context, element) -> () -> {});
    renderer.subtreeContentRenderer(
        (node, context) -> {
          states.record("paint(" + node.nodeName() + ")");
          if ("child".equals(node.nodeName())) {
            throw new IllegalStateException("child paint failed");
          }
        });

    Frame frame = new Frame();
    Element parent = element("parent", 10);
    Element child = element("child", 20);
    frame.addChild(parent);
    parent.addChild(child);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(child));

    assertThrows(IllegalStateException.class, () -> renderer.renderLayoutTree(frame));

    assertEquals(
        List.of(
            "apply(winframe,0.0)",
            "paint(winframe)",
            "apply(parent,10.0)",
            "paint(parent)",
            "apply(child,20.0)",
            "paint(child)",
            "restore(child)",
            "restore(parent)",
            "restore(winframe)"),
        states.calls());
  }

  @Test
  void renderLayoutTree_scopesScrollToChildContentAndRestoresItBeforeScrollbarPaint() {
    NvgRenderer renderer = new NvgRenderer();
    RecordingTransformStates states = new RecordingTransformStates();
    renderer.transformStateFactory(states);
    renderer.subtreeContentStateFactory(
        (context, element) -> {
          states.record(
              "content(" + element.nodeName() + ",-" + element.scrollLeft() + ",-" + element.scrollTop() + ")");
          return () -> states.record("content-restore(" + element.nodeName() + ")");
        });
    renderer.subtreeContentRenderer((node, context) -> states.record("paint(" + node.nodeName() + ")"));

    Frame frame = new Frame();
    Element parent = element("parent", 10);
    parent.scrollLeft(12);
    parent.scrollTop(8);
    Element child = element("child", 20);
    frame.addChild(parent);
    parent.addChild(child);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(child));

    renderer.renderLayoutTree(frame);

    assertEquals(
        List.of(
            "apply(winframe,0.0)",
            "paint(winframe)",
            "content(winframe,-0.0,-0.0)",
            "apply(parent,10.0)",
            "paint(parent)",
            "content(parent,-12.0,-8.0)",
            "apply(child,20.0)",
            "paint(child)",
            "restore(child)",
            "content-restore(parent)",
            "restore(parent)",
            "content-restore(winframe)",
            "restore(winframe)"),
        states.calls());
  }

  @Test
  void renderLayoutTree_keepsNestedTransformAndScrollScopesWhenPaintValuesComplete() {
    NvgRenderer renderer = new NvgRenderer();
    RecordingTransformStates states = new RecordingTransformStates();
    renderer.transformStateFactory(states);
    renderer.subtreeContentStateFactory(
        (context, element) -> {
          states.record("content(" + element.nodeName() + ")");
          return () -> states.record("content-restore(" + element.nodeName() + ")");
        });
    renderer.subtreeContentRenderer((node, context) -> states.record("paint(" + node.nodeName() + ")"));

    Frame frame = new Frame();
    Element parent = element("parent", 10);
    parent.scrollLeft(12);
    parent.scrollTop(8);
    parent.presentationState().setValue(OPACITY, 0.5f);
    parent.presentationState().setValue(BACKGROUND_COLOR, Color.BLUE);
    Element child = element("child", 20);
    child.presentationState().setValue(OPACITY, 0.25f);
    child.presentationState().setValue(COLOR, Color.RED);
    frame.addChild(parent);
    parent.addChild(child);
    frame.layoutChildNodes(List.of(parent));
    parent.layoutChildNodes(List.of(child));

    renderer.renderLayoutTree(frame);
    List<String> intermediateCalls = states.calls();
    states.clear();
    parent.presentationState().clearValues();
    child.presentationState().clearValues();

    renderer.renderLayoutTree(frame);

    assertEquals(intermediateCalls, states.calls());
  }

  @Test
  void renderDebug_runsOnlyAfterAllTransformedSubtreeStatesAreRestored() {
    NvgRenderer renderer = new NvgRenderer();
    RecordingTransformStates states = new RecordingTransformStates();
    renderer.transformStateFactory(states);
    renderer.subtreeContentStateFactory(
        (context, element) -> {
          states.record("content(" + element.nodeName() + ")");
          return () -> states.record("content-restore(" + element.nodeName() + ")");
        });
    renderer.subtreeContentRenderer((node, context) -> states.record("paint(" + node.nodeName() + ")"));
    renderer.debugRenderer((frame, context, mousePosition) -> states.record("debug"));
    renderer.debugMode(true);

    Frame frame = new Frame();
    Element child = element("child", 10);
    child.scrollTop(8);
    frame.addChild(child);
    frame.layoutChildNodes(List.of(child));

    renderer.renderLayoutTree(frame);
    renderer.renderDebug(frame);

    assertEquals(
        List.of(
            "apply(winframe,0.0)",
            "paint(winframe)",
            "content(winframe)",
            "apply(parent,10.0)",
            "paint(child)",
            "restore(parent)",
            "content-restore(winframe)",
            "restore(winframe)",
            "debug"),
        states.calls());
  }

  private Element element(String name, float translation) {
    Element element = new Element(name);
    element.presentationState().transform(AffineTransform.translation(translation, 0));
    return element;
  }

  private static final class RecordingTransformStates implements NvgTransformState.Factory {
    private final List<String> calls = new ArrayList<>();

    @Override
    public NvgTransformStateScope apply(long context, AffineTransform transform) {
      String name =
          transform.tx() == 0
              ? "winframe"
              : transform.tx() == 10 ? "parent" : transform.tx() == 20 ? "child" : "sibling";
      calls.add("apply(%s,%.1f)".formatted(name, transform.tx()));
      return () -> calls.add("restore(%s)".formatted(name));
    }

    void record(String call) {
      calls.add(call);
    }

    List<String> calls() {
      return List.copyOf(calls);
    }

    void clear() {
      calls.clear();
    }
  }
}
