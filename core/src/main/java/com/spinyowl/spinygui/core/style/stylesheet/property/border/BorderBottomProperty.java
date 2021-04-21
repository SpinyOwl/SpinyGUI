package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM;
import com.spinyowl.spinygui.core.node.style.types.border.BorderItem;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderBottomProperty extends Property<BorderItem> {

  public BorderBottomProperty() {
    super(
        BORDER_BOTTOM,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, i) -> s.border().right(i),
        s -> s.border().right(),
        BorderProperty::extract,
        BorderProperty::test);
  }
}
