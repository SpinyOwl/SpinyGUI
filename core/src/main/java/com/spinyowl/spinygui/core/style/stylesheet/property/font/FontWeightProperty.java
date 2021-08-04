package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;

public class FontWeightProperty extends Property {

  public FontWeightProperty() {
    super(
        FONT_WEIGHT,
        "normal",
        INHERITED,
        ANIMATABLE,
        (fontWeight, styles) -> styles.put(FONT_WEIGHT, FontWeight.find(fontWeight)),
        FontWeight::contains);
  }
}
