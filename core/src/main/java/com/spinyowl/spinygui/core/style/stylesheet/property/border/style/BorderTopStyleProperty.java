package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.node.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class BorderTopStyleProperty extends Property<BorderStyle> {

  public BorderTopStyleProperty() {
    super(
        BORDER_TOP_STYLE,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, c) -> s.border().top().style(c),
        s -> s.border().top().style(),
        BorderStyle::find,
        BorderStyle::contains);
  }
}
