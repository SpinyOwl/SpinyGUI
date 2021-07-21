package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderRightProperty extends Property {

  public BorderRightProperty() {
    super(
        BORDER_RIGHT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        value ->
            BorderProperty.extract(
                value, BORDER_RIGHT_STYLE, BORDER_RIGHT_WIDTH, BORDER_RIGHT_COLOR),
        BorderProperty::test,
        true);
  }
}
