package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg;

import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.createScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgRenderUtils.resetScissor;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.MIN_ALPHA;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRect;
import static com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util.NvgShapes.drawRectStroke;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthNullSafe;
import static com.spinyowl.spinygui.core.util.NodeUtilities.visible;
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgSave;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.layout.Rect;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.ScrollbarPart;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.util.ScrollbarGeometry;
import org.joml.Vector2f;
import org.joml.Vector4f;

class NvgScrollbarRenderer {

  private static final Color TRANSPARENT = Color.TRANSPARENT;

  private final ScrollbarShapeSink shapeSink;

  NvgScrollbarRenderer() {
    this(new NanoVgScrollbarShapeSink());
  }

  NvgScrollbarRenderer(ScrollbarShapeSink shapeSink) {
    this.shapeSink = shapeSink;
  }

  void render(Element element, long nanovgContext) {
    if (!visible(element) || !ScrollbarGeometry.canShowScrollbars(element)) {
      return;
    }

    ScrollbarGeometry.Metrics metrics = scrollbarMetrics(element);
    if (!metrics.verticalVisible() && !metrics.horizontalVisible()) {
      return;
    }

    shapeSink.begin(nanovgContext, element);
    if (metrics.verticalVisible()) {
      drawPart(nanovgContext, element, ScrollbarPart.TRACK, metrics.verticalTrack());
      drawPart(nanovgContext, element, ScrollbarPart.THUMB, metrics.verticalThumb());
    }
    if (metrics.horizontalVisible()) {
      drawPart(nanovgContext, element, ScrollbarPart.TRACK, metrics.horizontalTrack());
      drawPart(nanovgContext, element, ScrollbarPart.THUMB, metrics.horizontalThumb());
    }
    if (metrics.corner() != null) {
      drawPart(nanovgContext, element, ScrollbarPart.CORNER, metrics.corner());
    }
    shapeSink.end(nanovgContext);
  }

  private ScrollbarGeometry.Metrics scrollbarMetrics(Element element) {
    ScrollbarGeometry.Metrics metrics = element.scrollbarMetrics();
    return metrics == null
        ? ScrollbarGeometry.compute(element, element.scrollWidth(), element.scrollHeight())
        : metrics;
  }

  private void drawPart(
      long context, Element element, ScrollbarPart part, Rect rect) {
    if (rect == null || rect.width() <= 0 || rect.height() <= 0) {
      return;
    }

    ResolvedStyle style = element.scrollbarStyle(part);
    Color background = color(backgroundColor(style, part), style);
    Vector4f radius = borderRadius(style, rect);
    if (paints(background)) {
      shapeSink.fill(context, rect, background, radius);
    }

    float borderWidth = borderWidth(style);
    Color border = color(borderColor(style), style);
    if (drawsBorder(style) && borderWidth > 0 && paints(border)) {
      shapeSink.stroke(context, rect, border, borderWidth, radius);
    }
  }

  private Color backgroundColor(ResolvedStyle style, ScrollbarPart part) {
    if (style != null && style.backgroundColor() != null) {
      return style.backgroundColor();
    }
    return switch (part) {
      case THUMB -> ScrollbarGeometry.DEFAULT_THUMB_COLOR;
      case CORNER -> ScrollbarGeometry.DEFAULT_CORNER_COLOR;
      default -> ScrollbarGeometry.DEFAULT_TRACK_COLOR;
    };
  }

  private Color borderColor(ResolvedStyle style) {
    if (style == null || style.borderTopColor() == null) {
      return TRANSPARENT;
    }
    return style.borderTopColor();
  }

  private Color color(Color color, ResolvedStyle style) {
    if (color == null) {
      return TRANSPARENT;
    }
    if (style == null || style.opacity() == null) {
      return color;
    }
    float opacity = Math.max(0, Math.min(1, style.opacity()));
    float alpha = color.a() * opacity;
    return alpha == color.a() ? color : color.withA(alpha);
  }

  private boolean paints(Color color) {
    return color != null && color.a() > MIN_ALPHA;
  }

  private float borderWidth(ResolvedStyle style) {
    return style == null ? 0 : getFloatLengthNullSafe(style.borderTopWidth(), 0);
  }

  private boolean drawsBorder(ResolvedStyle style) {
    if (style == null) {
      return false;
    }
    BorderStyle borderStyle = style.borderTopStyle();
    return borderStyle != null
        && !BorderStyle.NONE.equals(borderStyle)
        && !BorderStyle.HIDDEN.equals(borderStyle);
  }

  private Vector4f borderRadius(ResolvedStyle style, Rect rect) {
    if (style == null) {
      return new Vector4f(0);
    }
    return new Vector4f(
        getFloatLengthNullSafe(style.borderTopLeftRadius(), rect.width()),
        getFloatLengthNullSafe(style.borderTopRightRadius(), rect.width()),
        getFloatLengthNullSafe(style.borderBottomRightRadius(), rect.width()),
        getFloatLengthNullSafe(style.borderBottomLeftRadius(), rect.width()));
  }

  interface ScrollbarShapeSink {
    void begin(long context, Element element);

    void fill(long context, Rect rect, Color color, Vector4f radius);

    void stroke(long context, Rect rect, Color color, float width, Vector4f radius);

    void end(long context);
  }

  private static final class NanoVgScrollbarShapeSink implements ScrollbarShapeSink {

    @Override
    public void begin(long context, Element element) {
      createScissor(context, element);
      nvgSave(context);
      Vector2f position = element.box().borderBoxPosition();
      Vector2f size = element.box().borderBoxSize();
      nvgIntersectScissor(context, position.x(), position.y(), size.x(), size.y());
    }

    @Override
    public void fill(long context, Rect rect, Color color, Vector4f radius) {
      drawRect(
          context,
          new Vector2f(rect.x(), rect.y()),
          new Vector2f(rect.width(), rect.height()),
          color,
          radius);
    }

    @Override
    public void stroke(long context, Rect rect, Color color, float width, Vector4f radius) {
      float inset = width / 2f;
      drawRectStroke(
          context,
          new Vector2f(rect.x() + inset, rect.y() + inset),
          new Vector2f(Math.max(0, rect.width() - width), Math.max(0, rect.height() - width)),
          color,
          width,
          radius);
    }

    @Override
    public void end(long context) {
      nvgRestore(context);
      resetScissor(context);
    }
  }
}
