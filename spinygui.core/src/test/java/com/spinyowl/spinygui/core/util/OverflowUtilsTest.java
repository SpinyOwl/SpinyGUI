package com.spinyowl.spinygui.core.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.style.types.Overflow;
import org.junit.jupiter.api.Test;

class OverflowUtilsTest {

  @Test
  void maxScrollLeft_returnsPositiveOverflowRange() {
    Element element = scrollableElement(300, 100, 100, 100);

    assertEquals(200, OverflowUtils.maxScrollLeft(element));
  }

  @Test
  void maxScrollTop_returnsPositiveOverflowRange() {
    Element element = scrollableElement(100, 350, 100, 100);

    assertEquals(250, OverflowUtils.maxScrollTop(element));
  }

  @Test
  void maxScroll_returnsZeroWhenContentFits() {
    Element element = scrollableElement(80, 90, 100, 100);

    assertEquals(0, OverflowUtils.maxScrollLeft(element));
    assertEquals(0, OverflowUtils.maxScrollTop(element));
  }

  @Test
  void clampScrollOffsets_clampsNegativeOffsetsToZero() {
    Element element = scrollableElement(300, 300, 100, 100);
    element.scrollLeft(-10);
    element.scrollTop(-20);

    OverflowUtils.clampScrollOffsets(element);

    assertEquals(0, element.scrollLeft());
    assertEquals(0, element.scrollTop());
  }

  @Test
  void clampScrollOffsets_clampsOversizedOffsetsToMaximum() {
    Element element = scrollableElement(300, 400, 100, 150);
    element.scrollLeft(500);
    element.scrollTop(600);

    OverflowUtils.clampScrollOffsets(element);

    assertEquals(200, element.scrollLeft());
    assertEquals(250, element.scrollTop());
  }

  @Test
  void clampScrollOffsets_resetsOffsetsWhenContentFits() {
    Element element = scrollableElement(80, 90, 100, 100);
    element.scrollLeft(10);
    element.scrollTop(20);

    OverflowUtils.clampScrollOffsets(element);

    assertEquals(0, element.scrollLeft());
    assertEquals(0, element.scrollTop());
  }

  @Test
  void visibleDoesNotClipOrAcceptWheelScroll() {
    Element element = scrollableElement(300, 300, 100, 100);
    element.resolvedStyle().overflowX(Overflow.VISIBLE);
    element.resolvedStyle().overflowY(Overflow.VISIBLE);

    assertFalse(OverflowUtils.clipsX(element));
    assertFalse(OverflowUtils.clipsY(element));
    assertFalse(OverflowUtils.clipsAny(element));
    assertFalse(OverflowUtils.acceptsWheelX(element));
    assertFalse(OverflowUtils.acceptsWheelY(element));
  }

  @Test
  void hiddenClipsButDoesNotAcceptWheelScroll() {
    Element element = scrollableElement(300, 300, 100, 100);
    element.resolvedStyle().overflowX(Overflow.HIDDEN);
    element.resolvedStyle().overflowY(Overflow.HIDDEN);

    assertTrue(OverflowUtils.clipsX(element));
    assertTrue(OverflowUtils.clipsY(element));
    assertTrue(OverflowUtils.clipsAny(element));
    assertFalse(OverflowUtils.acceptsWheelX(element));
    assertFalse(OverflowUtils.acceptsWheelY(element));
  }

  @Test
  void autoAcceptsWheelOnlyWhenAxisOverflows() {
    Element element = scrollableElement(300, 80, 100, 100);
    element.resolvedStyle().overflowX(Overflow.AUTO);
    element.resolvedStyle().overflowY(Overflow.AUTO);

    assertTrue(OverflowUtils.clipsX(element));
    assertTrue(OverflowUtils.clipsY(element));
    assertTrue(OverflowUtils.acceptsWheelX(element));
    assertFalse(OverflowUtils.acceptsWheelY(element));
  }

  @Test
  void scrollAcceptsWheelOnlyWhenAxisOverflows() {
    Element element = scrollableElement(90, 300, 100, 100);
    element.resolvedStyle().overflowX(Overflow.SCROLL);
    element.resolvedStyle().overflowY(Overflow.SCROLL);

    assertTrue(OverflowUtils.clipsX(element));
    assertTrue(OverflowUtils.clipsY(element));
    assertFalse(OverflowUtils.acceptsWheelX(element));
    assertTrue(OverflowUtils.acceptsWheelY(element));
  }

  private Element scrollableElement(
      float scrollWidth, float scrollHeight, float clientWidth, float clientHeight) {
    Element element = NodeBuilder.div();
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.clientWidth(clientWidth);
    element.clientHeight(clientHeight);
    return element;
  }
}
