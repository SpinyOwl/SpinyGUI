package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.Arrays;

public class BorderColorProperty extends Property<Color[]> {

  public static final String DEFAULT_VALUE = "black";
  private static ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderColorProperty() {
    super(
        BORDER_COLOR,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderColor,
        NodeStyle::borderColor,
        BorderColorProperty::extract,
        BorderColorProperty::test);
  }

  private static Color[] extract(String value) {
    return Arrays.stream(value.split("\\s+")).map(extractor::extract).toArray(Color[]::new);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
