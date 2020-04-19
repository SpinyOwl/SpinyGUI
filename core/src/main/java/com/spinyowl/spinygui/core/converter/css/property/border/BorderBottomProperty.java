package com.spinyowl.spinygui.core.converter.css.property.border;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderBottomProperty extends Property<BorderItem> {

  public BorderBottomProperty() {
    super(BORDER_BOTTOM, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
      (s, i) -> s.border().right(i), s -> s.border().right(),
      BorderProperty::extract, BorderProperty::test);
  }

}
