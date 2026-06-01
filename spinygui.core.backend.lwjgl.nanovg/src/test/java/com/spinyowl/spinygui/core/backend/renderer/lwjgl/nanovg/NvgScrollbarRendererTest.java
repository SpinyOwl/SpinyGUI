package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.NodeBuilder;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Overflow;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

class NvgScrollbarRendererTest {

  @Test
  void render_skipsVisibleAndHiddenOverflowElements() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);

    renderer.render(element(Overflow.VISIBLE, Overflow.VISIBLE, 300, 300), 1);
    renderer.render(element(Overflow.HIDDEN, Overflow.HIDDEN, 300, 300), 1);

    assertEquals(List.of(), sink.calls());
  }

  @Test
  void render_scrollOverflowDrawsTrackAndFullThumbWhenContentFits() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 100);

    renderer.render(element, 2);

    assertEquals(
        List.of(
            "begin(2,0.0,0.0,100.0,100.0)",
            "fill(2,88.0,0.0,12.0,100.0,rgba(0.827451, 0.827451, 0.827451, 1.0),0.0)",
            "fill(2,88.0,0.0,12.0,100.0,rgba(0.5019608, 0.5019608, 0.5019608, 1.0),0.0)",
            "end(2)"),
        sink.calls());
  }

  @Test
  void render_thumbSizeAndPositionFollowScrollOffsets() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.SCROLL, Overflow.SCROLL, 300, 300);
    element.scrollTop(100);
    element.scrollLeft(150);

    renderer.render(element, 3);

    assertEquals(
        List.of(
            "begin(3,0.0,0.0,100.0,100.0)",
            "fill(3,88.0,0.0,12.0,88.0,rgba(0.827451, 0.827451, 0.827451, 1.0),0.0)",
            "fill(3,88.0,29.3,12.0,25.8,rgba(0.5019608, 0.5019608, 0.5019608, 1.0),0.0)",
            "fill(3,0.0,88.0,88.0,12.0,rgba(0.827451, 0.827451, 0.827451, 1.0),0.0)",
            "fill(3,44.0,88.0,25.8,12.0,rgba(0.5019608, 0.5019608, 0.5019608, 1.0),0.0)",
            "fill(3,88.0,88.0,12.0,12.0,rgba(0.827451, 0.827451, 0.827451, 1.0),0.0)",
            "end(3)"),
        sink.calls());
  }

  @Test
  void render_usesPseudoStyleColorsRadiusBorderAndOpacity() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 300);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).backgroundColor(Color.RED);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).backgroundColor(Color.BLUE);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).opacity(0.5f);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopColor(Color.GREEN);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopStyle(BorderStyle.SOLID);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopWidth(Length.pixel(2));
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopLeftRadius(Length.pixel(3));
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopRightRadius(Length.pixel(4));
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderBottomRightRadius(Length.pixel(5));
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderBottomLeftRadius(Length.pixel(6));

    renderer.render(element, 4);

    assertEquals(
        List.of(
            "begin(4,0.0,0.0,100.0,100.0)",
            "fill(4,88.0,0.0,12.0,100.0,rgba(1.0, 0.0, 0.0, 1.0),0.0)",
            "fill(4,88.0,0.0,12.0,33.3,rgba(0.0, 0.0, 1.0, 0.5),3.0)",
            "stroke(4,88.0,0.0,12.0,33.3,rgba(0.0, 0.5019608, 0.0, 0.5),2.0,3.0)",
            "end(4)"),
        sink.calls());
    assertEquals(new Vector4f(3, 4, 5, 6), sink.radii().get(1));
  }

  @Test
  void render_doesNotStrokePseudoStyleWithDefaultBorderStyleNone() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 300);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).backgroundColor(Color.RED);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).borderTopStyle(BorderStyle.NONE);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).borderTopWidth(Length.pixel(4));
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).borderTopColor(Color.BLACK);

    renderer.render(element, 5);

    assertEquals(
        List.of(
            "begin(5,0.0,0.0,100.0,100.0)",
            "fill(5,88.0,0.0,12.0,100.0,rgba(1.0, 0.0, 0.0, 1.0),0.0)",
            "fill(5,88.0,0.0,12.0,33.3,rgba(0.5019608, 0.5019608, 0.5019608, 1.0),0.0)",
            "end(5)"),
        sink.calls());
  }

  @Test
  void render_skipsTransparentPseudoStyleFills() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.SCROLL, Overflow.SCROLL, 300, 300);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).backgroundColor(Color.TRANSPARENT);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).backgroundColor(Color.TRANSPARENT);
    element.getOrCreateScrollbarStyle(ScrollbarPart.CORNER).backgroundColor(Color.TRANSPARENT);

    renderer.render(element, 6);

    assertEquals(List.of("begin(6,0.0,0.0,100.0,100.0)", "end(6)"), sink.calls());
  }

  @Test
  void render_skipsTransparentPseudoStyleBorderColor() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 300);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopColor(Color.TRANSPARENT);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopStyle(BorderStyle.SOLID);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).borderTopWidth(Length.pixel(2));

    renderer.render(element, 7);

    assertEquals(
        List.of(
            "begin(7,0.0,0.0,100.0,100.0)",
            "fill(7,88.0,0.0,12.0,100.0,rgba(0.827451, 0.827451, 0.827451, 1.0),0.0)",
            "fill(7,88.0,0.0,12.0,33.3,rgba(0.5019608, 0.5019608, 0.5019608, 1.0),0.0)",
            "end(7)"),
        sink.calls());
  }

  @Test
  void render_reusesDefaultColorsWhenStyleIsNull() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 100);

    renderer.render(element, 8);

    assertSame(ScrollbarGeometry.DEFAULT_TRACK_COLOR, sink.fillColors().get(0));
    assertSame(ScrollbarGeometry.DEFAULT_THUMB_COLOR, sink.fillColors().get(1));
  }

  @Test
  void render_reusesPseudoStyleColorsWhenOpacityDoesNotChangeAlpha() {
    RecordingShapeSink sink = new RecordingShapeSink();
    NvgScrollbarRenderer renderer = new NvgScrollbarRenderer(sink);
    Element element = element(Overflow.VISIBLE, Overflow.SCROLL, 100, 300);
    Color trackColor = new Color(0.2f, 0.3f, 0.4f, 1f);
    Color thumbColor = new Color(0.4f, 0.5f, 0.6f, 1f);
    element.getOrCreateScrollbarStyle(ScrollbarPart.TRACK).backgroundColor(trackColor);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).backgroundColor(thumbColor);
    element.getOrCreateScrollbarStyle(ScrollbarPart.THUMB).opacity(1f);

    renderer.render(element, 9);

    assertSame(trackColor, sink.fillColors().get(0));
    assertSame(thumbColor, sink.fillColors().get(1));
  }

  private Element element(
      Overflow overflowX, Overflow overflowY, float scrollWidth, float scrollHeight) {
    Element element = NodeBuilder.div();
    element.box().contentPosition(0, 0);
    element.box().contentSize(100, 100);
    element.clientWidth(100);
    element.clientHeight(100);
    element.scrollWidth(scrollWidth);
    element.scrollHeight(scrollHeight);
    element.resolvedStyle().overflowX(overflowX);
    element.resolvedStyle().overflowY(overflowY);
    return element;
  }

  private static final class RecordingShapeSink
      implements NvgScrollbarRenderer.ScrollbarShapeSink {

    private final List<String> calls = new ArrayList<>();
    private final List<Vector4f> radii = new ArrayList<>();
    private final List<Color> fillColors = new ArrayList<>();

    @Override
    public void begin(long context, Element element) {
      Vector2f position = element.box().borderBoxPosition();
      Vector2f size = element.box().borderBoxSize();
      calls.add(
          "begin(%d,%.1f,%.1f,%.1f,%.1f)"
              .formatted(context, position.x(), position.y(), size.x(), size.y()));
    }

    @Override
    public void fill(long context, Rect rect, Color color, Vector4f radius) {
      fillColors.add(color);
      radii.add(new Vector4f(radius));
      calls.add(
          "fill(%d,%.1f,%.1f,%.1f,%.1f,%s,%.1f)"
              .formatted(
                  context,
                  rect.x(),
                  rect.y(),
                  rect.width(),
                  rect.height(),
                  color,
                  radius.x()));
    }

    @Override
    public void stroke(long context, Rect rect, Color color, float width, Vector4f radius) {
      calls.add(
          "stroke(%d,%.1f,%.1f,%.1f,%.1f,%s,%.1f,%.1f)"
              .formatted(
                  context,
                  rect.x(),
                  rect.y(),
                  rect.width(),
                  rect.height(),
                  color,
                  width,
                  radius.x()));
    }

    @Override
    public void end(long context) {
      calls.add("end(%d)".formatted(context));
    }

    List<String> calls() {
      return calls;
    }

    List<Vector4f> radii() {
      return radii;
    }

    List<Color> fillColors() {
      return fillColors;
    }
  }
}
