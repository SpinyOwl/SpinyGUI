package com.spinyowl.spinygui.visual;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.image.BufferedImage;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewComparisonTest {
  @Test
  void geometryRejectsMissingDuplicateNonfiniteAndScrollMismatch() {
    Geometry baseline = geometry("target", 0, 10);
    assertTrue(ViewComparison.geometry(List.of(baseline), List.of(baseline)).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(baseline), List.of()).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(), List.of()).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(baseline), List.of(baseline, baseline)).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(baseline), List.of(geometry("target", Double.NaN, 10))).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(baseline), List.of(geometry("target", 0, 30))).isEmpty());
    assertFalse(ViewComparison.geometry(List.of(baseline), List.of(geometry("other", 0, 10))).isEmpty());
  }

  @Test
  void geometryAcceptsOnlyDocumentedTolerance() {
    var expected = List.of(geometry("a", 0, 0));
    assertTrue(ViewComparison.geometry(expected, List.of(geometry("a", 1, 0))).isEmpty());
    assertFalse(ViewComparison.geometry(expected, List.of(geometry("a", 1.01, 0))).isEmpty());
  }

  @Test
  void pixelComparisonDetectsShiftColorAndAlphaAndProducesDiff() {
    var expected = image(20, 20, 0xffffffff);
    var actual = image(20, 20, 0xffffffff);
    assertTrue(ViewComparison.pixels(expected, actual).passed());
    for (int y = 0; y < 20; y++) actual.setRGB(10, y, 0xff000000);
    var mismatch = ViewComparison.pixels(expected, actual);
    assertFalse(mismatch.passed());
    assertEquals(20, mismatch.differentPixels());
    assertEquals(0xffff00ff, mismatch.diff().getRGB(10, 0));
    assertFalse(ViewComparison.pixels(expected, image(20, 20, 0x00ffffff)).passed());
  }

  @Test
  void pixelsEnforceChannelAndFractionThresholdsWithoutAcceptingInvalidInput() {
    var expected = image(20, 20, 0xffffffff);
    var actual = image(20, 20, 0xffefefef);
    assertTrue(ViewComparison.pixels(expected, actual).passed());
    actual.setRGB(0, 0, 0xff000000);
    actual.setRGB(1, 0, 0xff000000);
    assertTrue(ViewComparison.pixels(expected, actual).passed());
    actual.setRGB(2, 0, 0xff000000);
    assertFalse(ViewComparison.pixels(expected, actual).passed());
    assertFalse(ViewComparison.pixels(expected, image(1, 1, 0)).passed());
    assertThrows(IllegalArgumentException.class, () -> ViewComparison.pixels(expected, null));
  }

  private static Geometry geometry(String id, double x, double scrollTop) {
    return new Geometry(id, x, 0, 100, 100, 100, 100, 200, 200, 0, scrollTop);
  }

  private static BufferedImage image(int width, int height, int color) {
    var image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) image.setRGB(x, y, color);
    return image;
  }
}
