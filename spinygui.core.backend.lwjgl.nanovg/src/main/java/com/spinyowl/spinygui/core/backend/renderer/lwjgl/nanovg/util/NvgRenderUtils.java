package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthNullSafe;
import static com.spinyowl.spinygui.core.style.types.HorizontalAlign.CENTER;
import static com.spinyowl.spinygui.core.style.types.HorizontalAlign.LEFT;
import static com.spinyowl.spinygui.core.style.types.VerticalAlign.BOTTOM;
import static com.spinyowl.spinygui.core.style.types.VerticalAlign.MIDDLE;
import static com.spinyowl.spinygui.core.style.types.VerticalAlign.TOP;
import static lombok.AccessLevel.PRIVATE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BASELINE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_BOTTOM;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_CENTER;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_LEFT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_MIDDLE;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_RIGHT;
import static org.lwjgl.nanovg.NanoVG.NVG_ALIGN_TOP;
import static org.lwjgl.nanovg.NanoVG.NVG_HOLE;
import static org.lwjgl.nanovg.NanoVG.nvgBeginPath;
import static org.lwjgl.nanovg.NanoVG.nvgBoxGradient;
import static org.lwjgl.nanovg.NanoVG.nvgFill;
import static org.lwjgl.nanovg.NanoVG.nvgFillPaint;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Node;
import com.spinyowl.spinygui.core.style.ResolvedStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.HorizontalAlign;
import com.spinyowl.spinygui.core.style.types.VerticalAlign;
import java.nio.ByteBuffer;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

@NoArgsConstructor(access = PRIVATE)
public final class NvgRenderUtils {

  private static final NvgClipStack CLIP_STACK = new NvgClipStack(new NvgClipStack.NanoVgClipSink());

  /** Applies the cumulative presented opacity for an element and its paint ancestors. */
  public static Color withPresentedOpacity(Color color, Element element) {
    if (color == null) {
      return null;
    }
    float opacity = 1f;
    for (Element current = element; current != null; current = current.parent()) {
      Float currentOpacity = current.presentedStyle().opacity();
      opacity *= currentOpacity == null ? 1f : currentOpacity;
    }
    return color.withA(color.a() * opacity);
  }

  public static float[] calculateTextBoundsRect(
      long context,
      Vector4f rect,
      String text,
      HorizontalAlign horizontalAlign,
      VerticalAlign verticalAlign,
      float fontSize) {
    return calculateTextBoundsRect(
        context, rect.x, rect.y, rect.z, rect.w, text, horizontalAlign, verticalAlign, fontSize);
  }

  public static float[] calculateTextBoundsRect(
      long context,
      float x,
      float y,
      float w,
      float h,
      String text,
      HorizontalAlign horizontalAlign,
      VerticalAlign verticalAlign,
      float fontSize) {
    ByteBuffer byteText = null;
    try {
      byteText = memUTF8(text, false);
      return calculateTextBoundsRect(
          context, x, y, w, h, byteText, horizontalAlign, verticalAlign, fontSize);
    } finally {
      if (byteText != null) {
        memFree(byteText);
      }
    }
  }

  public static float[] calculateTextBoundsRect(
      long context,
      float x,
      float y,
      float w,
      float h,
      ByteBuffer text,
      HorizontalAlign horizontalAlign,
      VerticalAlign verticalAlign,
      float fontSize) {
    float[] bounds = new float[4];
    if (text != null && text.limit() != 0) {
      nvgTextBounds(context, x, y, text, bounds);
      return createBounds(x, y, w, h, horizontalAlign, verticalAlign, bounds);
    }
    return createBounds(x, y, w, h, horizontalAlign, verticalAlign, 0, fontSize);
  }

  public static float[] createBounds(
      float x,
      float y,
      float w,
      float h,
      HorizontalAlign horizontalAlign,
      VerticalAlign verticalAlign,
      float[] bounds) {
    float ww = bounds[2] - bounds[0];
    float hh = bounds[3] - bounds[1];
    return createBounds(x, y, w, h, horizontalAlign, verticalAlign, ww, hh);
  }

  public static float[] createBounds(
      float w,
      float h,
      HorizontalAlign horizontalAlign,
      VerticalAlign verticalAlign,
      float[] bounds,
      float ww,
      float hh) {
    int hp = getHorizontalAlignModifier(horizontalAlign);
    int vp = getVerticalAlignModifier(verticalAlign);

    float x1 = bounds[0] + (w + ww) * 0.5f * hp;

    float baseline = (vp > 2 ? hh / 4.0f : 0);
    float vv = (vp == 3 ? 1 : vp);
    float y1 = bounds[1] + (h + hh) * 0.5f * vv + (vp > 2 ? (+baseline) : 0);
    return new float[] {
      x1, y1, ww, hh, x1 - (ww * 0.5f * hp), y1 - (hh * 0.5f * vv) - baseline, ww, hh
    };
  }

  public static float[] createBounds(
      float x,
      float y,
      float w,
      float h,
      @NonNull HorizontalAlign horizontalAlign,
      @NonNull VerticalAlign verticalAlign,
      float tw,
      float th) {

    int hp = getHorizontalAlignModifier(horizontalAlign);
    float x1 = x + w * 0.5f * hp;

    float baseline = 0;
    float vv = getVerticalAlignModifier(verticalAlign);

    float y1 = y + h * 0.5f * vv;
    return new float[] {
      x1, y1, tw, th, x1 - (tw * 0.5f * hp), y1 - (th * 0.5f * vv) - baseline, tw, th
    };
  }

  public static void alignTextInBox(
      long context, HorizontalAlign horizontalAlign, VerticalAlign verticalAlign) {
    int hAlign = getNvgHorizontalAlign(horizontalAlign);
    int vAlign = getNvgVerticalAlign(verticalAlign);
    nvgTextAlign(context, hAlign | vAlign);
  }

  /**
   * Creates scissor for provided component by it's parent components, executes runnable and resets
   * scissor.
   *
   * @param context nanovg context.
   * @param node node.
   */
  public static void inScissor(long context, Node node, Runnable runnable) {
    createScissor(context, node);
    runnable.run();
    resetScissor(context);
  }

  /**
   * Creates scissor for provided component by it's parent components.
   *
   * @param context nanovg context.
   * @param node node.
   */
  public static void createScissor(long context, Node node) {
    CLIP_STACK.create(context, node);
  }

  /**
   * Creates scissor by provided component and it's parent components.
   *
   * @param context nanovg context.
   * @param parent parent node.
   */
  public static void createScissorByParent(long context, Node parent) {
    CLIP_STACK.createByParent(context, parent);
  }

  /**
   * Used to reset scissor.
   *
   * @param context nanovg context pointer.
   */
  public static void resetScissor(long context) {
    CLIP_STACK.reset(context);
  }

  /**
   * Returns vector of four border radius elements where: x = top left, y = top right, z = bottom
   * right, w = bottom left.
   *
   * <p>NOTE. IF radius specified in percents - radius will be calculated using only width of
   * element - will be represented with segment of circle (not ellipse).
   *
   * @return vector of four border radius.
   */
  public static Vector4f getBorderRadius(Element element, ResolvedStyle style) {
    Vector2f borderSize = element.box().borderBoxSize();
    return new Vector4f(
        getFloatLengthNullSafe(style.borderTopLeftRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderTopRightRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderBottomRightRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderBottomLeftRadius(), borderSize.x));
  }

  public static void renderShadow(long context, Element element, ResolvedStyle style) {
    var shadow = style.boxShadow();
    if (shadow != null && shadow.color().a() > 0.01f) {
      float hOffset = shadow.hOffset().convert();
      float vOffset = shadow.vOffset().convert();
      float blur = shadow.blur().convert();
      float spread = shadow.spread().convert();
      Vector2f absolutePosition = element.box().borderBoxPosition();
      Vector2f size = element.box().borderBoxSize();

      float x = absolutePosition.x;
      float y = absolutePosition.y;
      float w = size.x;
      float h = size.y;
      Vector4f borderRadius = getBorderRadius(element, style);
      float cornerRadius = (borderRadius.x + borderRadius.y + borderRadius.z + borderRadius.w) / 4;

      try (var shadowPaint = NVGPaint.calloc();
          NVGColor firstColor = NvgColorUtil.create(shadow.color());
          NVGColor secondColor = NvgColorUtil.create(0, 0, 0, 0)) {
        // creating gradient and put it to shadowPaint
        nvgBoxGradient(
            context,
            x + hOffset - spread,
            y + vOffset - spread,
            w + 2 * spread,
            h + 2 * spread,
            cornerRadius + spread,
            blur,
            firstColor,
            secondColor,
            shadowPaint);
        nvgBeginPath(context);
        nvgRoundedRectVarying(
            context,
            x + hOffset - spread - blur,
            y + vOffset - spread - blur,
            w + 2 * spread + 2 * blur,
            h + 2 * spread + 2 * blur,
            borderRadius.x + spread,
            borderRadius.y + spread,
            borderRadius.z + spread,
            borderRadius.w + spread);
        nvgRoundedRectVarying(
            context, x, y, w, h, borderRadius.x, borderRadius.y, borderRadius.z, borderRadius.w);
        nvgPathWinding(context, NVG_HOLE);
        nvgFillPaint(context, shadowPaint);
        nvgFill(context);
      }
    }
    nvgRestore(context);
  }

  private static int getVerticalAlignModifier(@NonNull VerticalAlign verticalAlign) {
    int vp;

    if (TOP.equals(verticalAlign)) {
      vp = 0;
    } else if (BOTTOM.equals(verticalAlign)) {
      vp = 2;
    } else {
      vp = 1;
    }
    return vp;
  }

  private static int getHorizontalAlignModifier(@NonNull HorizontalAlign horizontalAlign) {
    int hp;
    if (LEFT.equals(horizontalAlign)) {
      hp = 0;
    } else if (CENTER.equals(horizontalAlign)) {
      hp = 1;
    } else {
      hp = 2;
    }
    return hp;
  }

  private static int getNvgVerticalAlign(VerticalAlign verticalAlign) {
    int vAlign;
    if (verticalAlign == TOP) {
      vAlign = NVG_ALIGN_TOP;
    } else {
      if (verticalAlign == BOTTOM) {
        vAlign = NVG_ALIGN_BOTTOM;
      } else {
        if (verticalAlign == MIDDLE) {
          vAlign = NVG_ALIGN_MIDDLE;
        } else {
          vAlign = NVG_ALIGN_BASELINE;
        }
      }
    }
    return vAlign;
  }

  private static int getNvgHorizontalAlign(HorizontalAlign horizontalAlign) {
    int hAlign;
    if (horizontalAlign == CENTER) {
      hAlign = NVG_ALIGN_CENTER;
    } else {
      if (horizontalAlign == LEFT) {
        hAlign = NVG_ALIGN_LEFT;
      } else {
        hAlign = NVG_ALIGN_RIGHT;
      }
    }
    return hAlign;
  }
}
