package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderBottomProperty extends Property<BorderItem> {

  public BorderBottomProperty() {
    super(
        BORDER_BOTTOM,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        BorderBottomProperty::setBorderBottom,
        BorderBottomProperty::getBorderBottom,
        BorderProperty::extract,
        BorderProperty::test);
  }

  private static void setBorderBottom(NodeStyle s, BorderItem i) {
    s.borderBottomColor(i.color()).borderBottomStyle(i.style()).borderBottomWidth(i.width());
  }

  private static BorderItem getBorderBottom(NodeStyle s) {
    return new BorderItem(s.borderBottomColor(), s.borderBottomStyle(), s.borderBottomWidth());
  }
}
