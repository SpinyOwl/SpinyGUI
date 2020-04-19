package com.spinyowl.spinygui.core.converter.css.util;

import com.spinyowl.spinygui.core.node.Container;
import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import java.util.Objects;
import java.util.function.Predicate;

public final class StyleUtils {

  private StyleUtils() {
  }


  public static NodeStyle getParentCalculatedStyle(Element element) {
    Objects.requireNonNull(element);
    Container parent = element.parent();
    if (parent == null) {
      return null;
    }
    return parent.calculatedStyle();
  }

  public static boolean testOneFourValue(String value, Predicate<String> singleValueTester) {
    String[] values = value.split("\\s+");
    if (values.length == 0 || values.length > 4) {
      return false;
    }
    for (String length : values) {
      if (!singleValueTester.test(length)) {
        return false;
      }
    }

    return true;
  }
}
