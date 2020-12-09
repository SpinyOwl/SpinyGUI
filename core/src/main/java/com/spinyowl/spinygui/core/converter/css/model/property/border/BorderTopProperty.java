package com.spinyowl.spinygui.core.converter.css.model.property.border;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_TOP;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderTopProperty extends Property<BorderItem> {

  public BorderTopProperty() {
    super(BORDER_TOP, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, i) -> s.border().top(i), s -> s.border().top(),
        BorderProperty::extract, BorderProperty::test);
  }
}
