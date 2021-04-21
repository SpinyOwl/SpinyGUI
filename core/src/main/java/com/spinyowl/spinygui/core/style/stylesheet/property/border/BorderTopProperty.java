package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP;
import com.spinyowl.spinygui.core.node.style.types.border.BorderItem;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderTopProperty extends Property<BorderItem> {

  public BorderTopProperty() {
    super(
        BORDER_TOP,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, i) -> s.border().top(i),
        s -> s.border().top(),
        BorderProperty::extract,
        BorderProperty::test);
  }
}
