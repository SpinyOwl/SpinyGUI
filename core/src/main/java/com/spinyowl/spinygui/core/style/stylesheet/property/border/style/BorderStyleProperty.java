package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Arrays;

public class BorderStyleProperty extends Property<BorderStyle[]> {

  public static final String DEFAULT_VALUE = "solid";
  public static final String SPACE_REGEX = "\\s+";

  public BorderStyleProperty() {
    super(
        BORDER_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderStyle,
        NodeStyle::borderStyle,
        BorderStyleProperty::extract,
        BorderStyleProperty::test);
  }

  private static BorderStyle[] extract(String value) {
    return Arrays.stream(value.split(SPACE_REGEX))
        .map(BorderStyle::create)
        .toArray(BorderStyle[]::new);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, SPACE_REGEX, 1, 4, BorderStyle::contains);
  }
}
