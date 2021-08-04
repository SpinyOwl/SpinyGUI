package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.*;

public class BorderBottomProperty extends Property {

  public BorderBottomProperty() {
    super(
        BORDER_BOTTOM,
        BorderProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_BOTTOM_STYLE, BORDER_BOTTOM_WIDTH, BORDER_BOTTOM_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
