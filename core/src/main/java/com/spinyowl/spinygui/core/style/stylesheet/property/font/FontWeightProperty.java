package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import java.util.Map;

public class FontWeightProperty extends Property {

  public FontWeightProperty() {
    super(
        FONT_WEIGHT,
        "normal",
        INHERITED,
        ANIMATABLE,
        fontWeight -> Map.of(FONT_WEIGHT, FontWeight.find(fontWeight)),
        FontWeight::contains);
  }
}
