package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;

public class BorderTopStyleProperty extends Property {

  public BorderTopStyleProperty() {
    super(
        BORDER_TOP_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (borderStyle, styles) -> styles.put(BORDER_TOP_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
