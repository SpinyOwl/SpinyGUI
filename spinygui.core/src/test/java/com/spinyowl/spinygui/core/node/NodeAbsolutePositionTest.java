package com.spinyowl.spinygui.core.node;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NodeAbsolutePositionTest {

  @Test
  void absolutePosition_subtractsOffsetParentScroll() {
    Element parent = element(10, 20);
    parent.scrollTop(30);
    Element child = element(40, 80);
    parent.addChild(child);
    child.offsetParent(parent);

    assertEquals(70, child.absolutePosition().y());
  }

  @Test
  void absolutePosition_accumulatesNestedScrollOffsets() {
    Element outer = element(100, 50);
    outer.scrollLeft(10);
    outer.scrollTop(20);
    Element inner = element(30, 40);
    inner.scrollLeft(5);
    inner.scrollTop(8);
    Element child = element(70, 90);
    outer.addChild(inner);
    inner.addChild(child);
    inner.offsetParent(outer);
    child.offsetParent(inner);

    assertEquals(185, child.absolutePosition().x());
    assertEquals(152, child.absolutePosition().y());
  }

  private Element element(float x, float y) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(x, y);
    element.box().contentSize(10, 10);
    return element;
  }
}
