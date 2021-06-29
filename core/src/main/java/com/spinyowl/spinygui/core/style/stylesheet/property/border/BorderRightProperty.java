package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderRightProperty extends Property<BorderItem> {

  public BorderRightProperty() {
    super(
        BORDER_RIGHT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderRightProperty::setBorderRight,
        BorderRightProperty::getBorderRight,
        BorderProperty::extract,
        BorderProperty::test);
  }

  private static void setBorderRight(NodeStyle s, BorderItem i) {
    s.borderRightColor(i.color());
    s.borderRightStyle(i.style());
    s.borderRightWidth(i.width());
  }

  private static BorderItem getBorderRight(NodeStyle s) {
    return new BorderItem(s.borderRightColor(), s.borderRightStyle(), s.borderRightWidth());
  }
}
