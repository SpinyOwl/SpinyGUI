package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;

public class BorderLeftProperty extends Property {

  public BorderLeftProperty() {
    super(
        BORDER_LEFT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_LEFT_STYLE, BORDER_LEFT_WIDTH, BORDER_LEFT_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
