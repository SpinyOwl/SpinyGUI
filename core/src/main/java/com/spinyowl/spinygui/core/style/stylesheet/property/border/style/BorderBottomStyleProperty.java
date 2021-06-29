package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.style.BorderStyleProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderBottomStyleProperty extends Property<BorderStyle> {

  public BorderBottomStyleProperty() {
    super(
        BORDER_BOTTOM_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        NodeStyle::borderBottomStyle,
        NodeStyle::borderBottomStyle,
        BorderStyle::find,
        BorderStyle::contains);
  }
}
