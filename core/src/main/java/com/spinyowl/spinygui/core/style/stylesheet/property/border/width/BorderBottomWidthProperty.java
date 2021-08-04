package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;

public class BorderBottomWidthProperty extends Property {

  public BorderBottomWidthProperty() {
    super(
        BORDER_BOTTOM_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_BOTTOM_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
