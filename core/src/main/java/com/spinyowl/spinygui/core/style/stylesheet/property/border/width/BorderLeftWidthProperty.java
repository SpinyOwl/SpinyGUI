package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;

public class BorderLeftWidthProperty extends Property {

  public BorderLeftWidthProperty() {
    super(
        BORDER_LEFT_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_LEFT_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
