package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class FontWeightProperty extends Property<FontWeight> {

  public FontWeightProperty() {
    super(
        FONT_WEIGHT,
        "normal",
        INHERITED,
        ANIMATABLE,
        NodeStyle::fontWeight,
        NodeStyle::fontWeight,
        FontWeight::find,
        FontWeight::contains);
  }
}
