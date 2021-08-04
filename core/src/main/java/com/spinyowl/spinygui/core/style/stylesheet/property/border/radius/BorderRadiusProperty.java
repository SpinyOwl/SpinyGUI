package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import java.util.Arrays;
import java.util.Map;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.setOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;

public class BorderRadiusProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderRadiusProperty() {
    super(
        BORDER_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        BorderRadiusProperty::extract,
        BorderRadiusProperty::test,
        true);
  }

  protected static void extract(String value, Map<String, Object> styles) {
    setOneFour(
        Arrays.stream(value.split("\\s+")).map(extractor::extract).toArray(),
        BORDER_TOP_LEFT_RADIUS,
        BORDER_TOP_RIGHT_RADIUS,
        BORDER_BOTTOM_RIGHT_RADIUS,
        BORDER_BOTTOM_LEFT_RADIUS,
        styles);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
