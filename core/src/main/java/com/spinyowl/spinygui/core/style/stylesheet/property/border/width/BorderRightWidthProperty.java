package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import java.util.Map;

public class BorderRightWidthProperty extends Property {

  public BorderRightWidthProperty() {
    super(
        BORDER_RIGHT_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BORDER_RIGHT_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
