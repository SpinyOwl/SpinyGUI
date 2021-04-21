package com.spinyowl.spinygui.core.style.stylesheet.util;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import java.util.Objects;
import java.util.function.Predicate;

public final class StyleUtils {

  private StyleUtils() {}

  public static NodeStyle getParentCalculatedStyle(Element element) {
    Objects.requireNonNull(element);
    Element parent = element.parent();
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
}
