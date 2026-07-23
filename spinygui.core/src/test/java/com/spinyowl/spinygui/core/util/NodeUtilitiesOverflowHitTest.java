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
  void getTargetElement_returnsChildInsideHiddenOverflowPaddingBox() {
    Frame frame = frame(500, 500);
    Element container = clippedElement(0, 0, 100, 100);
    container.box().padding().right(50);
    Element child = element(110, 10, 20, 20);
    frame.addChild(container);
    container.addChild(child);
    child.offsetParent(container);

    Vector2f pointInsideChildInPadding = new Vector2f(115, 15);

    assertEquals(child, NodeUtilities.getTargetElement(frame, pointInsideChildInPadding));
    assertTrue(NodeUtilities.getTargetElementList(frame, pointInsideChildInPadding).contains(child));
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
  void getTargetElement_returnsNestedRowScrolledIntoViewport() {
    Frame frame = frame(500, 500);
    Element container = clippedElement(0, 0, 200, 200);
    container.scrollTop(600);
    Element lines = element(0, 0, 200, 800);
    Element lastRow = element(0, 780, 200, 20);
    frame.addChild(container);
    container.addChild(lines);
    lines.addChild(lastRow);
    lines.offsetParent(container);
    lastRow.offsetParent(lines);

    assertEquals(lastRow, NodeUtilities.getTargetElement(frame, new Vector2f(10, 190)));
    assertFalse(
        NodeUtilities.getTargetElementList(frame, new Vector2f(10, 10)).contains(lastRow));
  }

  @Test
  void getTargetElement_returnsChildOutsideVisibleOverflowParentBox() {
    Frame frame = frame(500, 500);
    Element container = visibleOverflowElement(0, 0, 100, 100);
    Element child = element(110, 10, 20, 20);
    frame.addChild(container);
    container.addChild(child);
    child.offsetParent(container);

    Vector2f pointInsideOverflowingChild = new Vector2f(115, 15);

    assertEquals(child, NodeUtilities.getTargetElement(frame, pointInsideOverflowingChild));
    assertTrue(
        NodeUtilities.getTargetElementList(frame, pointInsideOverflowingChild).contains(child));
    assertFalse(
        NodeUtilities.getTargetElementList(frame, pointInsideOverflowingChild)
            .contains(container));
  }

  @Test
  void getTargetElement_excludesChildOutsideVisibleOverflowParentWhenClippedByAncestor() {
    Frame frame = frame(500, 500);
    Element outer = clippedElement(0, 0, 100, 100);
    Element inner = visibleOverflowElement(0, 0, 100, 100);
    Element child = element(110, 10, 20, 20);
    frame.addChild(outer);
    outer.addChild(inner);
    inner.addChild(child);
    inner.offsetParent(outer);
    child.offsetParent(inner);

    Vector2f pointInsideChildButOutsideClippingAncestor = new Vector2f(115, 15);

    assertEquals(
        frame, NodeUtilities.getTargetElement(frame, pointInsideChildButOutsideClippingAncestor));
    assertFalse(
        NodeUtilities.getTargetElementList(frame, pointInsideChildButOutsideClippingAncestor)
            .contains(child));
  }

  @Test
  void getTargetElement_returnsChildInsideInnerNestedScrollPaddingBox() {
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

    Vector2f pointInsideChildInInnerPadding = new Vector2f(110, 30);

    assertEquals(child, NodeUtilities.getTargetElement(frame, pointInsideChildInInnerPadding));
    assertTrue(
        NodeUtilities.getTargetElementList(frame, pointInsideChildInInnerPadding).contains(child));
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

  private Element visibleOverflowElement(float x, float y, float width, float height) {
    Element element = element(x, y, width, height);
    element.resolvedStyle().overflowX(Overflow.VISIBLE);
    element.resolvedStyle().overflowY(Overflow.VISIBLE);
    return element;
  }

  private Element element(float x, float y, float width, float height) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    return element;
  }
}
