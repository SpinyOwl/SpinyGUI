package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Map;

public class BorderTopStyleProperty extends Property {

  public BorderTopStyleProperty() {
    super(
        BORDER_TOP_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        borderStyle -> Map.of(BORDER_TOP_STYLE, BorderStyle.find(borderStyle)),
        BorderStyle::contains);
  }
}
