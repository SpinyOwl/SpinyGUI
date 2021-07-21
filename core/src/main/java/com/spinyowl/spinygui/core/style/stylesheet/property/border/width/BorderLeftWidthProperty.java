package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import java.util.Map;

public class BorderLeftWidthProperty extends Property {

  public BorderLeftWidthProperty() {
    super(
        BORDER_LEFT_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BORDER_LEFT_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
