package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderRightStyleProperty extends Property {

  public BorderRightStyleProperty() {
    super(
        BORDER_RIGHT_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (borderStyle, styles) -> styles.put(BORDER_RIGHT_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
