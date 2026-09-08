package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

class NvgBorderRendererTest {
  @Test
  void roundedUniformBorderUsesInsetStrokeRadiusAndPresentedOpacity() {
    Element element = element();
    element.box().border().top(4);
    element.box().border().right(4);
    element.box().border().bottom(4);
    element.box().border().left(4);
    element.box().contentPosition(4, 4);
    element.resolvedStyle().opacity(.5f);
    var sink = new RecordingSink();
    new NvgBorderRenderer(sink).render(element, 1);
    assertEquals(1, sink.strokes.size());
    assertEquals(new Vector4f(16), sink.strokes.getFirst());
    assertEquals(.5f, sink.colors.getFirst().a(), .001f);
    assertEquals(new Rect(2, 2, 104, 64), sink.rect);
  }

  @Test
  void topNoneAndZeroBottomDoNotSuppressOtherSides() {
    Element element = element();
    element.box().border().top(8);
    element.box().border().right(6);
    element.box().border().bottom(0);
    element.box().border().left(10);
    element.resolvedStyle().borderTopStyle(BorderStyle.NONE);
    element.resolvedStyle().borderRightColor(new Color(1, 0, 0, 1));
    element.resolvedStyle().borderLeftColor(new Color(0, 0, 1, 1));
    var sink = new RecordingSink();
    new NvgBorderRenderer(sink).render(element, 1);
    assertEquals(0, sink.strokes.size());
    assertEquals(2, sink.contours.size());
    assertEquals(List.of(new Color(1, 0, 0, 1), new Color(0, 0, 1, 1)), sink.colors);
    element.resolvedStyle().borderRightStyle(BorderStyle.HIDDEN);
    var hiddenSink = new RecordingSink();
    new NvgBorderRenderer(hiddenSink).render(element, 1);
    assertEquals(List.of(new Color(0, 0, 1, 1)), hiddenSink.colors);
  }

  @Test
  void unequalRoundedSidesShareCornerEndpointsAndStayInsideBox() {
    var rect = new Rect(10, 20, 100, 60);
    var widths = new float[] {4, 8, 12, 6};
    var radius = BorderContour.clampRadii(new Vector4f(80, 20, 40, 60), 100, 60);
    assertEquals(60, radius.x + radius.w, .001f);
    for (int side = 0; side < 4; side++) {
      List<Vector2f> points = BorderContour.side(rect, widths, radius, side);
      List<Vector2f> next = BorderContour.side(rect, widths, radius, (side + 1) % 4);
      int half = points.size() / 2;
      assertTrue(points.get(half - 1).distance(next.getFirst()) < .001f);
      assertTrue(points.get(half).distance(next.getLast()) < .001f);
      assertTrue(points.stream().allMatch(p -> p.x >= 9.999 && p.x <= 110.001
          && p.y >= 19.999 && p.y <= 80.001));
    }
  }

  private static Element element() {
    Element e = NodeBuilder.div();
    e.box().contentSize(100, 60);
    var style = e.resolvedStyle();
    style.borderTopStyle(BorderStyle.SOLID);
    style.borderRightStyle(BorderStyle.SOLID);
    style.borderBottomStyle(BorderStyle.SOLID);
    style.borderLeftStyle(BorderStyle.SOLID);
    style.borderTopColor(new Color(0, .5f, 0, 1));
    style.borderRightColor(new Color(0, .5f, 0, 1));
    style.borderBottomColor(new Color(0, .5f, 0, 1));
    style.borderLeftColor(new Color(0, .5f, 0, 1));
    style.borderTopLeftRadius(Length.pixel(18));
    style.borderTopRightRadius(Length.pixel(18));
    style.borderBottomRightRadius(Length.pixel(18));
    style.borderBottomLeftRadius(Length.pixel(18));
    return e;
  }

  private static final class RecordingSink implements NvgBorderRenderer.ShapeSink {
    private final List<Vector4f> strokes = new ArrayList<>();
    private final List<List<Vector2f>> contours = new ArrayList<>();
    private final List<Color> colors = new ArrayList<>();
    private Rect rect;

    public void stroke(long context, Rect rect, Color color, float width, Vector4f radius) {
      strokes.add(radius);
      colors.add(color);
      this.rect = rect;
    }

    public void fill(long context, List<Vector2f> contour, Color color) {
      contours.add(contour);
      colors.add(color);
    }
  }
}
