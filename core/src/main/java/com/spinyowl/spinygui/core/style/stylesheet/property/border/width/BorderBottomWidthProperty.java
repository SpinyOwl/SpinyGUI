package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderBottomWidthProperty extends Property<Length> {

  public BorderBottomWidthProperty() {
    super(
        BORDER_BOTTOM_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderBottomWidth,
        NodeStyle::borderBottomWidth,
        BorderWidthProperty::extractOne,
        BorderWidthProperty::testOne);
  }
}
