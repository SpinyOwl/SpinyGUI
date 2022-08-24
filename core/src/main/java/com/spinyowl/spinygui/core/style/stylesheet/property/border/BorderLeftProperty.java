package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderLeftProperty extends Property {

  public BorderLeftProperty() {
    super(
        BORDER_LEFT,
        BorderProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_LEFT_STYLE, BORDER_LEFT_WIDTH, BORDER_LEFT_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
