package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderLeftProperty extends Property<BorderItem> {

  public BorderLeftProperty() {
    super(
        BORDER_LEFT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderLeftProperty::setBorderLeft,
        BorderLeftProperty::getBorderLeft,
        BorderProperty::extract,
        BorderProperty::test);
  }

  private static void setBorderLeft(NodeStyle s, BorderItem i) {
    s.borderLeftColor(i.color()).borderLeftStyle(i.style()).borderLeftWidth(i.width());
  }

  private static BorderItem getBorderLeft(NodeStyle s) {
    return new BorderItem(s.borderLeftColor(), s.borderLeftStyle(), s.borderLeftWidth());
  }
}
