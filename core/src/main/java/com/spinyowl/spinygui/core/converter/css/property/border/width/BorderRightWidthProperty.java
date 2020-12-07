package com.spinyowl.spinygui.core.converter.css.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderRightWidthProperty extends Property<Length> {

  public BorderRightWidthProperty() {
    super(BORDER_RIGHT_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
        (s, v) -> s.border().right().width(v),
        s -> s.border().right().width(),
        BorderWidthProperty::extractOne, BorderWidthProperty::testOne);
  }
}
