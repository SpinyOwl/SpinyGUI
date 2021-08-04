package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;

public class BorderTopProperty extends Property {

  public BorderTopProperty() {
    super(
        BORDER_TOP,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_TOP_STYLE, BORDER_TOP_WIDTH, BORDER_TOP_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
