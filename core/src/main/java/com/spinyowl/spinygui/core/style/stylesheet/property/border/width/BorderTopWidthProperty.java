package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;

public class BorderTopWidthProperty extends Property {

  public BorderTopWidthProperty() {
    super(
        BORDER_TOP_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_TOP_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
