package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.Arrays;
import java.util.Map;

public class BorderColorProperty extends Property {

  public static final String DEFAULT_VALUE = "black";
  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderColorProperty() {
    super(
        BORDER_COLOR,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderColorProperty::extract,
        BorderColorProperty::test,
        true);
  }

  private static Map<String, Object> extract(String value) {
    return getOneFour(
        Arrays.stream(value.split("\\s+")).map(extractor::extract).toArray(Color[]::new),
        BORDER_TOP_COLOR,
        BORDER_RIGHT_COLOR,
        BORDER_BOTTOM_COLOR,
        BORDER_LEFT_COLOR);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
