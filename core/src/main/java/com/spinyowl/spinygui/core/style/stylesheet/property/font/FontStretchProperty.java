package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STRETCH;
import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class FontStretchProperty extends Property {
  public FontStretchProperty() {
    super(
        FONT_STRETCH,
        "normal",
        INHERITABLE,
        ANIMATABLE,
        (fontStretch, styles) -> styles.put(FONT_STRETCH, FontStretch.find(fontStretch)),
        FontStretch::contains);
  }
}
