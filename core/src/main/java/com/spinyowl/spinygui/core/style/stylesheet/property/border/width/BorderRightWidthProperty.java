package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderRightWidthProperty extends Property<Length> {

  public BorderRightWidthProperty() {
    super(
        BORDER_RIGHT_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        (s, v) -> s.border().right().width(v),
        s -> s.border().right().width(),
        BorderWidthProperty::extractOne,
        BorderWidthProperty::testOne);
  }
}
