package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.Color;
import com.spinyowl.spinygui.core.node.style.types.border.Border;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderColorProperty extends Property<Border> {

  public static final String DEFAULT_VALUE = "black";
  private static ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderColorProperty() {
    super(
        BORDER_COLOR,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, b) -> s.border().color(b),
        NodeStyle::border,
        BorderColorProperty::extract,
        BorderColorProperty::test);
  }

  private static Border extract(String value) {
    String[] values = value.split("\\s+");
    Border border = new Border();
    if (values.length == 1) {
      border.color(extractor.extract(values[0]));
    } else if (values.length == 2) {
      border.color(extractor.extract(values[0]), extractor.extract(values[1]));
    } else if (values.length == 3) {
      border.color(
          extractor.extract(values[0]), extractor.extract(values[1]), extractor.extract(values[2]));
    } else if (values.length == 4) {
      border.color(
          extractor.extract(values[0]),
          extractor.extract(values[1]),
          extractor.extract(values[2]),
          extractor.extract(values[3]));
    }
    return border;
  }

  private static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, extractor::isValid);
  }
}
