package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class PaddingProperty extends Property<Padding> {

  private static final ValueExtractor<Length> lengthValueExtractor =
      ValueExtractors.of(Length.class);

  public PaddingProperty() {
    super(
        PADDING,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::padding,
        NodeStyle::padding,
        PaddingProperty::extract,
        PaddingProperty::test);
  }

  public static Padding extract(String value) {
    if (value == null) {
      return null;
    }
    String[] v = value.split("\\s+");
    // @formatter:off
    switch (v.length) {
      case 0:
        return null;
      case 1:
        return new Padding(x(v[0]));
      case 2:
        return new Padding(x(v[0]), x(v[1]));
      case 3:
        return new Padding(x(v[0]), x(v[1]), x(v[2]));
      case 4:
      default:
        return new Padding(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
    }
    // @formatter:on
  }

  private static Length<?> x(String value1) {
    return lengthValueExtractor.extract(value1);
  }

  public static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, lengthValueExtractor::isValid);
  }
}
