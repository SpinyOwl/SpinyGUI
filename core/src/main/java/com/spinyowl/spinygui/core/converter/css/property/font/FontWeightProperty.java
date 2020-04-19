package com.spinyowl.spinygui.core.converter.css.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_WEIGHT;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class FontWeightProperty extends Property<FontWeight> {

  public FontWeightProperty() {
    super(FONT_WEIGHT, "normal", INHERITED, ANIMATABLE, NodeStyle::fontWeight,
        NodeStyle::fontWeight, FontWeight::find, FontWeight::contains);
  }

}
