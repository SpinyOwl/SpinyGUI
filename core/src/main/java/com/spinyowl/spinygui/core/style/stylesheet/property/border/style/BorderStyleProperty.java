package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Arrays;
import java.util.Map;

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

  private static Map<String, Object> extract(String value) {
    return getOneFour(
        Arrays.stream(value.split(SPACE_REGEX)).map(BorderStyle::find).toArray(),
        BORDER_TOP_STYLE,
        BORDER_RIGHT_STYLE,
        BORDER_BOTTOM_STYLE,
        BORDER_LEFT_STYLE);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, SPACE_REGEX, 1, 4, BorderStyle::contains);
  }
}
