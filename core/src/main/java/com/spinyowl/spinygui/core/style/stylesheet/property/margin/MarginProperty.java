package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Arrays;

public class MarginProperty extends Property<Unit[]> {

  private static ValueExtractor<Unit> unitValueExtractor = ValueExtractors.of(Unit.class);

  public MarginProperty() {
    super(
        MARGIN,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::margin,
        NodeStyle::margin,
        MarginProperty::extract,
        MarginProperty::test);
  }

  private static Unit[] extract(String value) {
    if (value == null) {
      return null;
    }

    // @formatter:off
    return Arrays.stream(value.trim().split("\\s+")).map(MarginProperty::x).toArray(Unit[]::new);
    // @formatter:on
  }

  private static Unit x(String v) {
    return unitValueExtractor.extract(v);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, unitValueExtractor::isValid);
  }
}
