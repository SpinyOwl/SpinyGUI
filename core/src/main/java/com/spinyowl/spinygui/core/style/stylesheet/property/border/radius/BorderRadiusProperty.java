package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.getOneFour;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Arrays;
import java.util.Map;

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

  private static Map<String, Object> extract(String value) {
    return getOneFour(
        Arrays.stream(value.split("\\s+")).map(extractor::extract).toArray(),
        BORDER_TOP_LEFT_RADIUS,
        BORDER_TOP_RIGHT_RADIUS,
        BORDER_BOTTOM_RIGHT_RADIUS,
        BORDER_BOTTOM_LEFT_RADIUS);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
