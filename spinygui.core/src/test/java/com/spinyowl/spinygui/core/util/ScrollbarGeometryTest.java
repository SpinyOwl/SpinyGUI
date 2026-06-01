package com.spinyowl.spinygui.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.junit.jupiter.api.Test;

class ScrollbarGeometryTest {

  @Test
  void compute_returnsTrackAndThumbGeometryForVerticalScrollbar() {
    Element element = NodeBuilder.div();
    element.box().contentPosition(10, 20);
    element.box().contentSize(100, 100);
    element.resolvedStyle().overflowY(Overflow.AUTO);
    element.scrollTop(100);

    ScrollbarGeometry.Metrics metrics = ScrollbarGeometry.compute(element, 100, 300);

    assertRect(new Rect(98, 20, 12, 100), metrics.verticalTrack());
    assertRect(new Rect(98, 53.333336f, 12, 33.333332f), metrics.verticalThumb());
    assertEquals(88, metrics.clientWidth());
    assertEquals(100, metrics.clientHeight());
  }

  @Test
  void compute_returnsTrackThumbAndCornerGeometryForBothScrollbars() {
    Element element = NodeBuilder.div();
    element.box().contentPosition(10, 20);
    element.box().contentSize(100, 100);
    element.resolvedStyle().overflowX(Overflow.SCROLL);
    element.resolvedStyle().overflowY(Overflow.SCROLL);

    ScrollbarGeometry.Metrics metrics = ScrollbarGeometry.compute(element, 300, 300);

    assertRect(new Rect(98, 20, 12, 88), metrics.verticalTrack());
    assertRect(new Rect(10, 108, 88, 12), metrics.horizontalTrack());
    assertRect(new Rect(98, 108, 12, 12), metrics.corner());
    assertRect(new Rect(98, 20, 12, 25.813334f), metrics.verticalThumb());
    assertRect(new Rect(10, 108, 25.813334f, 12), metrics.horizontalThumb());
  }

  @Test
  void compute_placesScrollbarTracksAgainstPaddingBoxEdge() {
    Element element = NodeBuilder.div();
    element.box().contentPosition(16, 27);
    element.box().contentSize(100, 100);
    element.box().padding().left(6);
    element.box().padding().right(8);
    element.box().padding().top(7);
    element.box().padding().bottom(9);
    element.resolvedStyle().overflowX(Overflow.SCROLL);
    element.resolvedStyle().overflowY(Overflow.SCROLL);

    ScrollbarGeometry.Metrics metrics = ScrollbarGeometry.compute(element, 300, 300);

    assertRect(new Rect(112, 20, 12, 104), metrics.verticalTrack());
    assertRect(new Rect(10, 124, 102, 12), metrics.horizontalTrack());
    assertRect(new Rect(112, 124, 12, 12), metrics.corner());
  }

  private void assertRect(Rect expected, Rect actual) {
    assertEquals(expected.x(), actual.x(), 0.0001f);
    assertEquals(expected.y(), actual.y(), 0.0001f);
    assertEquals(expected.width(), actual.width(), 0.0001f);
    assertEquals(expected.height(), actual.height(), 0.0001f);
  }
}
