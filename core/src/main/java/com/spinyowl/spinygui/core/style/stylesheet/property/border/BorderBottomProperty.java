package com.spinyowl.spinygui.core.style.stylesheet.property.border;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderBottomProperty extends Property {

  public BorderBottomProperty() {
    super(
        BORDER_BOTTOM,
        BorderProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) ->
            BorderProperty.extract(
                value, BORDER_BOTTOM_STYLE, BORDER_BOTTOM_WIDTH, BORDER_BOTTOM_COLOR, styles),
        BorderProperty::test,
        true);
  }
}
