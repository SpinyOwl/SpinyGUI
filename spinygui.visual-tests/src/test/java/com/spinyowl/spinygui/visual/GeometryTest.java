package com.spinyowl.spinygui.visual;

import static org.junit.jupiter.api.Assertions.*;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.types.AffineTransform;
import com.spinyowl.spinygui.core.style.types.Display;
import org.junit.jupiter.api.Test;

class GeometryTest {
  @Test
  void boundsIncludeAncestorScrollingAndLocalAndAncestorTransforms() {
    Element parent = element("parent", 20, 30, 100, 100);
    Element child = element("child", 5, 8, 20, 10);
    child.parent(parent);
    child.offsetParent(parent);
    parent.scrollLeft(3);
    parent.scrollTop(4);
    child.presentationState().transform(AffineTransform.scale(2, 2));
    parent.presentationState().transform(AffineTransform.translation(4, 6));
    Geometry actual = Geometry.collect(parent).get(1);
    assertEquals(26, actual.x());
    assertEquals(40, actual.y());
    assertEquals(40, actual.width());
    assertEquals(20, actual.height());
  }

  @Test
  void hiddenAncestorsZeroDescendantGeometry() {
    Element parent = element("parent", 20, 30, 100, 100);
    Element child = element("child", 5, 8, 20, 10);
    child.parent(parent);
    parent.resolvedStyle().display(Display.NONE);
    for (Geometry geometry : Geometry.collect(parent)) {
      assertArrayEquals(new double[10], geometry.values());
    }
  }

  private static Element element(String id, float x, float y, float width, float height) {
    Element element = new Element("div");
    element.setAttribute("id", id);
    element.box().contentPosition(x, y);
    element.box().contentSize(width, height);
    return element;
  }
}
