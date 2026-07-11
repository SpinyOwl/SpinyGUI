package com.spinyowl.spinygui.core.style.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/** Executable examples for the visual-coordinate contract in the M1/P2 phase document. */
class VisualCoordinateContractTest {

  @Test
  void nestedPaintUsesParentTransformThenClipThenScrollThenChildTransform() {
    var parentTransform = AffineTransform.translation(100f, 20f);
    var parentScroll = AffineTransform.translation(-10f, -5f);
    var childLayoutOffset = AffineTransform.translation(30f, 10f);
    var childTransform = AffineTransform.scale(2f, 2f);
    var paintTransform =
        parentTransform.multiply(parentScroll).multiply(childLayoutOffset).multiply(childTransform);

    assertPoint(paintTransform.apply(3f, 4f), 126f, 33f);
    assertPoint(parentTransform.apply(0f, 0f), 100f, 20f);
    assertPoint(parentTransform.apply(50f, 40f), 150f, 60f);
    assertPoint(paintTransform.inverse().orElseThrow().apply(126f, 33f), 3f, 4f);
  }

  @Test
  void debugOverlayRemainsInViewportLayoutSpace() {
    var childLayoutOffset = AffineTransform.translation(30f, 10f);

    assertPoint(childLayoutOffset.apply(3f, 4f), 33f, 14f);
  }

  private void assertPoint(AffineTransform.Point point, float x, float y) {
    assertEquals(x, point.x(), 0.0001f);
    assertEquals(y, point.y(), 0.0001f);
  }
}
