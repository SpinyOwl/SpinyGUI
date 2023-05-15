package com.spinyowl.spinygui.core.backend.renderer.lwjgl.nanovg.util;

import com.spinyowl.spinygui.core.style.types.Color;
import org.joml.Vector4fc;
import org.lwjgl.nanovg.NVGColor;

/**
 * NanoVG utility. Used to convert some NanoVG elements to other. For example {@link NVGColor} to
 * {@link org.joml.Vector4f} and back.
 */
public final class NvgColorUtil {

  private NvgColorUtil() {}

  /**
   * Used to fill {@link NVGColor}.
   *
   * @param color color.
   * @param nvgColor color to fill.
   */
  public static void fillNvgColor(NVGColor nvgColor, Color color) {
    fillNvgColor(nvgColor, color.r(), color.g(), color.b(), color.a());
  }

  /**
   * Used to fill {@link NVGColor}.
   *
   * @param rgba rgba color {@link Vector4fc} of floats.
   *     <ul>
   *       <li>rgba.x - red.
   *       <li>rgba.y - green.
   *       <li>rgba.z - blue.
   *       <li>rgba.w - alpha.
   *     </ul>
   *
   * @param color color to fill.
   */
  public static void fillNvgColorWithRGBA(Vector4fc rgba, NVGColor color) {
    fillNvgColor(color, rgba.x(), rgba.y(), rgba.z(), rgba.w());
  }

  /**
   * Used to allocate and fill instance of {@link NVGColor}. Should be used in try-with-resources to
   * avoid memory leaks.
   *
   * @param rgba rgba color {@link Vector4fc} of floats.
   *     <ul>
   *       <li>rgba.x - red.
   *       <li>rgba.y - green.
   *       <li>rgba.z - blue.
   *       <li>rgba.w - alpha.
   *     </ul>
   *
   * @return allocated and filled color.
   */
  public static NVGColor create(Vector4fc rgba) {
    return create(rgba.x(), rgba.y(), rgba.z(), rgba.w());
  }

  /**
   * Used to allocate and fill instance of {@link NVGColor}. Should be used in try-with-resources to
   * avoid memory leaks.
   *
   * @param color color.
   * @return allocated and filled color.
   */
  public static NVGColor create(Color color) {
    return create(color.r(), color.g(), color.b(), color.a());
  }

  /**
   * Used to allocate and fill instance of {@link NVGColor}. Should be used in try-with-resources to
   * avoid memory leaks.
   *
   * @param r red.
   * @param g green.
   * @param b blue.
   * @param a alpha.
   * @return allocated and filled color.
   */
  public static NVGColor create(float r, float g, float b, float a) {
    var color = NVGColor.calloc();
    fillNvgColor(color, r, g, b, a);
    return color;
  }

  /**
   * Used to fill {@link NVGColor}.
   *
   * @param color color to fill.
   * @param r red.
   * @param g green.
   * @param b blue.
   * @param a alpha.
   */
  public static void fillNvgColor(NVGColor color, float r, float g, float b, float a) {
    color.r(r);
    color.g(g);
    color.b(b);
    color.a(a);
  }
}
