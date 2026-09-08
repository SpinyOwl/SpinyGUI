package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.getBorderRadius;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.withPresentedOpacity;

import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgColorUtil;
import com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.List;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NanoVG;

/** Paints solid border contours from individual sides, in the current presented transform. */
public class NvgBorderRenderer {
  /** Backend-owned drawing seam; tests record contours without requiring an OpenGL context. */
  private final ShapeSink sink;

  /** Physical pixels per logical pixel, refreshed by the owning renderer each frame. */
  private float pixelRatio = 1;

  void pixelRatio(float value) {
    pixelRatio = value;
  }

  public NvgBorderRenderer() {
    this(new NativeShapeSink());
  }

  NvgBorderRenderer(ShapeSink sink) {
    this.sink = sink;
  }

  public void render(Node node, long nanovg) {
    Element element = node.asElement();
    var style = element.resolvedStyle();
    var border = element.box().border();
    float[] widths = {
        visibleWidth(style.borderTopStyle(), border.top()),
        visibleWidth(style.borderRightStyle(), border.right()),
        visibleWidth(style.borderBottomStyle(), border.bottom()),
        visibleWidth(style.borderLeftStyle(), border.left())};
    var presented = element.presentedStyle();
    Color[] colors = {presented.borderTopColor(), presented.borderRightColor(),
        presented.borderBottomColor(), presented.borderLeftColor()};
    Vector4f radius = getBorderRadius(element, style);
    if ((Display.INLINE.equals(style.display()) || Display.INLINE_BLOCK.equals(style.display()))
        && !element.inlineFragments().isEmpty()) {
      Vector2f offset = inlineFormattingOffset(element);
      for (var fragment : element.inlineFragments()) {
        paint(element, nanovg, new Rect(offset.x + fragment.x(), offset.y + fragment.y(),
            fragment.width(), fragment.height()), widths, colors, radius);
      }
    } else {
      var position = element.layoutAbsolutePosition();
      var size = element.size();
      paint(element, nanovg, new Rect(position.x, position.y, size.x, size.y),
          widths, colors, radius);
    }
  }

  private void paint(Element element, long context, Rect rect, float[] widths,
      Color[] colors, Vector4f radius) {
    if (rect.width() <= 0 || rect.height() <= 0) return;
    Vector4f clamped = BorderContour.clampRadii(radius, rect.width(), rect.height());
    boolean uniform = true;
    for (int side = 1; side < 4; side++) {
      uniform &= widths[side] == widths[0] && java.util.Objects.equals(colors[side], colors[0]);
    }
    if (uniform) {
      float width = widths[0];
      if (width <= 0) return;
      rect = sink.align(context, rect, width, pixelRatio);
      float half = width / 2;
      Vector4f strokeRadii = new Vector4f(
          Math.max(0, clamped.x - half), Math.max(0, clamped.y - half),
          Math.max(0, clamped.z - half), Math.max(0, clamped.w - half));
      sink.stroke(context, new Rect(rect.x() + half, rect.y() + half,
          Math.max(0, rect.width() - width), Math.max(0, rect.height() - width)),
          withPresentedOpacity(colors[0], element), width, strokeRadii);
      return;
    }
    for (int side = 0; side < 4; side++) {
      if (widths[side] > 0) {
        sink.fill(context, BorderContour.side(rect, widths, clamped, side),
            withPresentedOpacity(colors[side], element));
      }
    }
  }

  private static float visibleWidth(BorderStyle style, float width) {
    return BorderStyle.NONE.equals(style) || BorderStyle.HIDDEN.equals(style)
        ? 0 : Math.max(0, width);
  }

  Vector2f inlineFormattingOffset(Element element) {
    Element parent = element.parent();
    while (parent != null && Display.INLINE.equals(parent.resolvedStyle().display())) {
      parent = parent.parent();
    }
    return parent == null ? new Vector2f() : parent.layoutAbsolutePosition();
  }

  interface ShapeSink {
    default Rect align(long context, Rect rect, float width, float pixelRatio) {
      return rect;
    }

    void stroke(long context, Rect rect, Color color, float width, Vector4f radius);
    void fill(long context, List<Vector2f> contour, Color color);
  }

  private static final class NativeShapeSink implements ShapeSink {
    @Override
    public Rect align(long context, Rect rect, float width, float pixelRatio) {
      try (var stack = org.lwjgl.system.MemoryStack.stackPush()) {
        var transform = stack.mallocFloat(6);
        NanoVG.nvgCurrentTransform(context, transform);
        return alignBorder(rect, width, pixelRatio, transform.get(0), transform.get(1),
            transform.get(2), transform.get(3), transform.get(4), transform.get(5));
      }
    }

    @Override
    public void stroke(long context, Rect rect, Color color, float width, Vector4f radius) {
      NvgShapes.drawRectStroke(context, new Vector4f(rect.x(), rect.y(), rect.width(), rect.height()),
          color, width, radius);
    }

    @Override
    public void fill(long context, List<Vector2f> contour, Color color) {
      try (var nvgColor = NvgColorUtil.create(color)) {
        NanoVG.nvgBeginPath(context);
        Vector2f first = contour.getFirst();
        NanoVG.nvgMoveTo(context, first.x, first.y);
        for (int i = 1; i < contour.size(); i++) {
          Vector2f point = contour.get(i);
          NanoVG.nvgLineTo(context, point.x, point.y);
        }
        NanoVG.nvgClosePath(context);
        NanoVG.nvgFillColor(context, nvgColor);
        NanoVG.nvgFill(context);
      }
    }
  }

  /** Snaps thin, untranslated-scale border edges in device space; transformed artwork stays smooth. */
  static Rect alignBorder(Rect rect, float width, float ratio,
      float a, float b, float c, float d, float tx, float ty) {
    float physicalWidth = width * ratio;
    if (!(ratio > 0) || a != 1 || d != 1 || b != 0 || c != 0
        || physicalWidth < 1 || physicalWidth > 2
        || Math.abs(physicalWidth - Math.round(physicalWidth)) > .0001f) return rect;
    float left = Math.round((rect.x() + tx) * ratio) / ratio - tx;
    float top = Math.round((rect.y() + ty) * ratio) / ratio - ty;
    float right = Math.round((rect.x() + rect.width() + tx) * ratio) / ratio - tx;
    float bottom = Math.round((rect.y() + rect.height() + ty) * ratio) / ratio - ty;
    return new Rect(left, top, Math.max(0, right - left), Math.max(0, bottom - top));
  }
}
