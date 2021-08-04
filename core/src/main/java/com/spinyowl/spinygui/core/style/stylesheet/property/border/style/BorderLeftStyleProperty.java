package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;

public class BorderLeftStyleProperty extends Property {

  public BorderLeftStyleProperty() {
    super(
        BORDER_LEFT_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (borderStyle, styles) -> styles.put(BORDER_LEFT_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
