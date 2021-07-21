package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import java.util.Map;

public class FontStyleProperty extends Property {

  public FontStyleProperty() {
    super(
        FONT_STYLE,
        "normal",
        INHERITED,
        !ANIMATABLE,
        fontStyle -> Map.of(FONT_STYLE, FontStyle.find(fontStyle)),
        FontStyle::contains);
  }
}
