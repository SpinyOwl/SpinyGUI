package com.spinyowl.spinygui.core.style.stylesheet.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.CalculatedStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import lombok.NonNull;
import org.joml.Vector2fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public final class StyleUtils {

  private StyleUtils() {}

  public static CalculatedStyle getParentCalculatedStyle(Element element) {
    Objects.requireNonNull(element);
    var parent = element.parent();
    if (parent == null) {
      return null;
    }
    return parent.calculatedStyle();
  }

  public static boolean testMultipleValues(
      String value, String splitRegex, int min, int max, Predicate<String> oneValueTester) {
    String[] values = value.split(splitRegex);
    if (values.length < min || values.length > max) {
      return false;
    }
    for (String v : values) {
      if (!oneValueTester.test(v)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Used to extract float value of length from {@link Length} value.
   *
   * @param length length
   * @param baseWidth base width to calculate length using percentage.
   * @return float(pixels) representation of length.
   */
  public static float getFloatLengthNullSafe(Unit length, float baseWidth) {
    var floatLength = getFloatLength(length, baseWidth);
    return floatLength == null ? 0 : floatLength;
  }

  /**
   * Used to extract float value of length from {@link Length} value.
   *
   * @param length length
   * @param baseWidth base width to calculate length using percentage.
   * @return float(pixels) representation of length.
   */
  public static Float getFloatLength(Unit length, float baseWidth) {
    if (length == null) return null;
    else if (length.isAuto()) return baseWidth;
    else if (length.isLength()) {
      var l = length.asLength();
      return (float) l.convert(baseWidth);
    }
    return null;
  }

  /**
   * Used to calculate inner content rectangle (box-sizing: border-box)
   *
   * @param componentPosition component position.
   * @param componentSize component size.
   * @param componentPadding component padding.
   * @return inner content rectangle represented with {@link Vector4f} where x,y - position, z,w -
   *     width and height.
   */
  public static Vector4f getInnerContentRectangle(
      Vector2fc componentPosition, Vector2fc componentSize, Vector4fc componentPadding) {
    return new Vector4f(
        componentPosition.x() + componentPadding.x(),
        componentPosition.y() + componentPadding.y(),
        componentSize.x() - componentPadding.x() - componentPadding.z(),
        componentSize.y() - componentPadding.y() - componentPadding.w());
  }

  public static Map<String, Object> getOneFour(
      @NonNull Object[] values,
      @NonNull String top,
      @NonNull String right,
      @NonNull String bottom,
      @NonNull String left) {
    switch (values.length) {
      case 0:
        return Map.of();
      case 1:
        {
          return Map.of(top, values[0], right, values[0], bottom, values[0], left, values[0]);
        }
      case 2:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[0], left, values[1]);
        }
      case 3:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[2], left, values[1]);
        }
      case 4:
      default:
        {
          return Map.of(top, values[0], right, values[1], bottom, values[2], left, values[3]);
        }
    }
  }
}
