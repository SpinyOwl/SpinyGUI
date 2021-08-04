package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import java.util.Arrays;
import java.util.Map;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;

public class BorderStyleProperty extends Property {

  public static final String DEFAULT_VALUE = "solid";
  public static final String SPACE_REGEX = "\\s+";

  public BorderStyleProperty() {
    super(
        BORDER_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderStyleProperty::extract,
        BorderStyleProperty::test,
        true);
  }

  private static void extract(String value, Map<String, Object> styles) {
    setOneFour(
        Arrays.stream(value.split(SPACE_REGEX)).map(BorderStyle::find).toArray(),
        BORDER_TOP_STYLE,
        BORDER_RIGHT_STYLE,
        BORDER_BOTTOM_STYLE,
        BORDER_LEFT_STYLE,
        styles);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, SPACE_REGEX, 1, 4, BorderStyle::contains);
  }
}
