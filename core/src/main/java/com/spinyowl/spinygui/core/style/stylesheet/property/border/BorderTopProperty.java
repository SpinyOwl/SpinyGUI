package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderTopProperty extends Property<BorderItem> {

  public BorderTopProperty() {
    super(
        BORDER_TOP,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderTopProperty::setBorderTop,
        BorderTopProperty::getBorderTop,
        BorderProperty::extract,
        BorderProperty::test);
  }

  private static void setBorderTop(NodeStyle s, BorderItem i) {
    s.borderTopColor(i.color());
    s.borderTopStyle(i.style());
    s.borderTopWidth(i.width());
  }

  private static BorderItem getBorderTop(NodeStyle s) {
    return new BorderItem(s.borderTopColor(), s.borderTopStyle(), s.borderTopWidth());
  }
}
