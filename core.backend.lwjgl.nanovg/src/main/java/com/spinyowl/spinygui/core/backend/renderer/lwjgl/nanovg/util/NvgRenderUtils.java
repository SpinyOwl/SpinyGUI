package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getFloatLengthNullSafe;
import static com.spinyowl.spinygui.core.style.types.HorizontalAlign.CENTER;
import static com.spinyowl.spinygui.core.style.types.HorizontalAlign.LEFT;
import static com.spinyowl.spinygui.core.style.types.VerticalAlign.BOTTOM;
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
import static org.lwjgl.nanovg.NanoVG.nvgIntersectScissor;
import static org.lwjgl.nanovg.NanoVG.nvgPathWinding;
import static org.lwjgl.nanovg.NanoVG.nvgResetScissor;
import static org.lwjgl.nanovg.NanoVG.nvgRestore;
import static org.lwjgl.nanovg.NanoVG.nvgRoundedRectVarying;
import static org.lwjgl.nanovg.NanoVG.nvgScissor;
import static org.lwjgl.nanovg.NanoVG.nvgTextAlign;
import static org.lwjgl.nanovg.NanoVG.nvgTextBounds;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.system.MemoryUtil.memUTF8;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.CalculatedStyle;
import com.spinyowl.spinygui.core.style.types.HorizontalAlign;
import com.spinyowl.spinygui.core.style.types.VerticalAlign;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.nanovg.NVGColor;
import org.lwjgl.nanovg.NVGPaint;

@NoArgsConstructor(access = PRIVATE)
public final class NvgRenderUtils {

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
   * @param element {@link Element}.
   */
  public static void inScissor(long context, Element element, Runnable runnable) {
    createScissor(context, element);
    runnable.run();
    resetScissor(context);
  }

  /**
   * Creates scissor for provided component by it's parent components.
   *
   * @param context nanovg context.
   * @param element {@link Element}.
   */
  public static void createScissor(long context, Element element) {
    createScissorByParent(context, element.parent());
  }

  /**
   * Creates scissor for provided bounds.
   *
   * @param context nanovg context.
   * @param bounds bounds.
   */
  public static void createScissor(long context, Vector4f bounds) {
    nvgScissor(context, bounds.x, bounds.y, bounds.z, bounds.w);
  }

  /**
   * Intersects scissor for provided bounds.
   *
   * @param context nanovg context.
   * @param bounds bounds.
   */
  public static void intersectScissor(long context, Vector4f bounds) {
    nvgIntersectScissor(context, bounds.x, bounds.y, bounds.z, bounds.w);
  }

  /**
   * Creates scissor by provided component and it's parent components.
   *
   * @param context nanovg context.
   * @param parent parent component.
   */
  public static void createScissorByParent(long context, Element parent) {
    List<Element> parents = new ArrayList<>();
    while (parent != null) {
      parents.add(parent);
      parent = parent.parent();
    }
    var pos = new Vector2f();
    int size = parents.size();
    if (size > 0) {
      parent = parents.get(size - 1);
      pos.add(parent.dimensions().paddingBoxPosition());
      Vector2f s = parent.dimensions().paddingBoxSize();
      createScissor(context, new Vector4f(pos, s.x, s.y));
      if (size > 1) {
        for (int i = size - 2; i >= 0; i--) {
          parent = parents.get(i);
          s = parent.dimensions().paddingBoxSize();
          pos = parent.dimensions().paddingBoxPosition();
          nvgIntersectScissor(context, pos.x, pos.y, s.x, s.y);
        }
      }
    }
  }

  /**
   * Used to reset scissor.
   *
   * @param context nanovg context pointer.
   */
  public static void resetScissor(long context) {
    nvgResetScissor(context);
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
  public static Vector4f getBorderRadius(Element element, CalculatedStyle style) {
    Vector2f borderSize = element.dimensions().borderBoxSize();
    return new Vector4f(
        getFloatLengthNullSafe(style.borderTopLeftRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderTopRightRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderBottomRightRadius(), borderSize.x),
        getFloatLengthNullSafe(style.borderBottomLeftRadius(), borderSize.x));
  }

  public static void renderShadow(long context, Element element, CalculatedStyle style) {
    var shadow = style.boxShadow();
    if (shadow != null && shadow.color().a() > 0.01f) {
      float hOffset = shadow.hOffset();
      float vOffset = shadow.vOffset();
      float blur = shadow.blur();
      float spread = shadow.spread();
      Vector2f absolutePosition = element.absolutePosition();
      Vector2f size = element.dimensions().borderBoxSize();

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
        if (verticalAlign == VerticalAlign.MIDDLE) {
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
    if (horizontalAlign == HorizontalAlign.CENTER) {
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
