package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Map;

public class BorderRightStyleProperty extends Property {

  public BorderRightStyleProperty() {
    super(
        BORDER_RIGHT_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        borderStyle -> Map.of(BORDER_LEFT_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
