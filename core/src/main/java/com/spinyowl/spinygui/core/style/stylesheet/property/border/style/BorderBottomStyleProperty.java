package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;

public class BorderBottomStyleProperty extends Property {

  public BorderBottomStyleProperty() {
    super(
        BORDER_BOTTOM_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (borderStyle, styles) -> styles.put(BORDER_BOTTOM_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
