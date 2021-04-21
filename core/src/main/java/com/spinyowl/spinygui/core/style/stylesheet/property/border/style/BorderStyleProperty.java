package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.util.StyleUtils.testMultipleValues;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.border.Border;
import com.spinyowl.spinygui.core.node.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderStyleProperty extends Property<Border> {

  public static final String DEFAULT_VALUE = "solid";

  public BorderStyleProperty() {
    super(
        BORDER_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, b) -> s.border().style(b),
        NodeStyle::border,
        BorderStyleProperty::extract,
        BorderStyleProperty::test);
  }

  private static Border extract(String value) {
    String[] v = value.split("\\s+");
    Border border = new Border();
    if (v.length == 1) {
      border.style(x(v[0]));
    } else if (v.length == 2) {
      border.style(x(v[0]), x(v[1]));
    } else if (v.length == 3) {
      border.style(x(v[0]), x(v[1]), x(v[2]));
    } else if (v.length == 4) {
      border.style(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
    }
    return border;
  }

  private static BorderStyle x(String v) {
    return BorderStyle.create(v);
  }

  private static boolean test(String value) {
    return testMultipleValues(value, "\\s+", 1, 4, BorderStyle::contains);
  }
}
