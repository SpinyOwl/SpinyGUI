package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;

public class BorderRightProperty extends Property {

  public BorderRightProperty() {
    super(
        BORDER_RIGHT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_RIGHT_STYLE, BORDER_RIGHT_WIDTH, BORDER_RIGHT_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
