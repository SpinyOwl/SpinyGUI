package com.spinyowl.spinygui.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.joml.Vector2f;
import org.junit.jupiter.api.Test;

class NodeUtilitiesOverflowHitTest {

  @Test
  void getTargetElement_excludesChildOutsideHiddenOverflowContentBox() {
    Frame frame = frame(500, 500);
    Element container = clippedElement(0, 0, 100, 100);
    container.box().padding().right(50);
    Element child = element(110, 10, 20, 20);
    frame.addChild(container);
    container.addChild(child);
    child.offsetParent(container);

    Vector2f pointInsideChildButOutsideContent = new Vector2f(115, 15);

    assertEquals(container, NodeUtilities.getTargetElement(frame, pointInsideChildButOutsideContent));
    assertFalse(
        NodeUtilities.getTargetElementList(frame, pointInsideChildButOutsideContent).contains(child));
  }

  @Test
  void getTargetElement_returnsChildScrolledIntoVisibleContentBox() {
    Frame frame = frame(500, 500);
    Element container = clippedElement(0, 0, 100, 100);
    container.scrollTop(100);
    Element child = element(10, 150, 30, 30);
    frame.addChild(container);
    container.addChild(child);
    child.offsetParent(container);

    Vector2f visiblePoint = new Vector2f(15, 55);

    assertEquals(child, NodeUtilities.getTargetElement(frame, visiblePoint));
    assertTrue(NodeUtilities.getTargetElementList(frame, visiblePoint).contains(child));
  }

  @Test
  void getTargetElement_excludesChildOutsideInnerNestedScrollContentBox() {
    Frame frame = frame(500, 500);
    Element outer = clippedElement(0, 0, 200, 200);
    Element inner = clippedElement(10, 10, 80, 80);
    inner.box().padding().right(40);
    Element child = element(95, 20, 20, 20);
    frame.addChild(outer);
    outer.addChild(inner);
    inner.addChild(child);
    inner.offsetParent(outer);
    child.offsetParent(inner);

    Vector2f pointInsideChildButOutsideInnerContent = new Vector2f(110, 30);

    assertEquals(inner, NodeUtilities.getTargetElement(frame, pointInsideChildButOutsideInnerContent));
    assertFalse(
        NodeUtilities.getTargetElementList(frame, pointInsideChildButOutsideInnerContent)
            .contains(child));
  }

  private Frame frame(float width, float height) {
    Frame frame = NodeBuilder.frame();
    frame.box().contentSize(width, height);
    return frame;
  }

  private Element clippedElement(float x, float y, float width, float height) {
    Element element = element(x, y, width, height);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);
    return element;
  }

  private Element element(float x, float y, float width, float height) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    return element;
  }
}
