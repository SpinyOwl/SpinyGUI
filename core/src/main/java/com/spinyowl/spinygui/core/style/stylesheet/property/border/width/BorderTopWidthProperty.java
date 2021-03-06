package com.spinyowl.spinygui.core.style.stylesheet.property.border.width;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_WIDTH;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.width.BorderWidthProperty.MEDIUM;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderTopWidthProperty extends Property<Length> {

  public BorderTopWidthProperty() {
    super(
        BORDER_TOP_WIDTH,
        MEDIUM,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderTopWidth,
        NodeStyle::borderTopWidth,
        BorderWidthProperty::extractOne,
        BorderWidthProperty::testOne);
  }
}
