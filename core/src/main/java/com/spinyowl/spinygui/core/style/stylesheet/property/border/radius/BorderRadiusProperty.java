package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RADIUS;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.BorderRadius;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderRadiusProperty extends Property<BorderRadius> {

  private static ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderRadiusProperty() {
    super(
        BORDER_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderRadius,
        NodeStyle::borderRadius,
        BorderRadiusProperty::extract,
        BorderRadiusProperty::test);
  }

  private static BorderRadius extract(String value) {
    String[] v = value.split("\\s+");
    // @formatter:off
    switch (v.length) {
      case 1:
        return new BorderRadius(x(v[0]));
      case 2:
        return new BorderRadius(x(v[0]), x(v[1]));
      case 3:
        return new BorderRadius(x(v[0]), x(v[1]), x(v[2]));
      case 4:
      default:
        return new BorderRadius(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
    }
    // @formatter:on
  }

  private static Length x(String value) {
    return extractor.extract(value);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
