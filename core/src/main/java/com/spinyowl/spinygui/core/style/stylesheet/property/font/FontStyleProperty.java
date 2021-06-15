package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class FontStyleProperty extends Property<FontStyle> {

  public FontStyleProperty() {
    super(
        FONT_STYLE,
        "normal",
        INHERITED,
        !ANIMATABLE,
        NodeStyle::fontStyle,
        NodeStyle::fontStyle,
        FontStyle::find,
        FontStyle::contains);
  }
}
