package com.spinyowl.spinygui.core.converter.css.model.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.converter.css.model.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderBottomWidthProperty extends Property<Length> {

  public BorderBottomWidthProperty() {
    super(BORDER_BOTTOM_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
        (s, v) -> s.border().bottom().width(v),
        s -> s.border().bottom().width(),
        BorderWidthProperty::extractOne, BorderWidthProperty::testOne);
  }
}
