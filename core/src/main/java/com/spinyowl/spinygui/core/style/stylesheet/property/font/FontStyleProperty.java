package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;

public class FontStyleProperty extends Property {

  public FontStyleProperty() {
    super(
        FONT_STYLE,
        "normal",
        INHERITABLE,
        !ANIMATABLE,
        (fontStyle, styles) -> styles.put(FONT_STYLE, FontStyle.find(fontStyle)),
        FontStyle::contains);
  }
}
