package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import java.util.Map;

public class BorderTopWidthProperty extends Property {

  public BorderTopWidthProperty() {
    super(
        BORDER_TOP_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BORDER_TOP_WIDTH, BorderWidthProperty.extractOne(value)),
        BorderWidthProperty::testOne);
  }
}
