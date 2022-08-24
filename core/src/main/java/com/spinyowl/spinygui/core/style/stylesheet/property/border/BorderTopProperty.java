package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderTopProperty extends Property {

  public BorderTopProperty() {
    super(
        BORDER_TOP,
        BorderProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_TOP_STYLE, BORDER_TOP_WIDTH, BORDER_TOP_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
