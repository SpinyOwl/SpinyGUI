package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.border.BorderItem;

public class BorderLeftProperty extends Property<BorderItem> {

  public BorderLeftProperty() {
    super(BORDER_LEFT, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, i) -> s.border().right(i), s -> s.border().right(),
        BorderProperty::extract, BorderProperty::test);
  }

}
