package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderLeftStyleProperty extends Property<BorderStyle> {

  public BorderLeftStyleProperty() {
    super(
        BORDER_LEFT_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderLeftStyle,
        NodeStyle::borderLeftStyle,
        BorderStyle::find,
        BorderStyle::contains);
  }
}
