package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;

public class BorderRightWidthProperty extends Property {

  public BorderRightWidthProperty() {
    super(
        BORDER_RIGHT_WIDTH,
        MEDIUM,
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_RIGHT_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
