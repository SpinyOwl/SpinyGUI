package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderRightStyleProperty extends Property<BorderStyle> {

  public BorderRightStyleProperty() {
    super(
        BORDER_RIGHT_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, c) -> s.border().right().style(c),
        s -> s.border().right().style(),
        BorderStyle::find,
        BorderStyle::contains);
  }
}
