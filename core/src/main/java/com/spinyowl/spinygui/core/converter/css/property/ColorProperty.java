package com.spinyowl.spinygui.core.converter.css.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.COLOR;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorProperty extends Property<Color> {

  public static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public ColorProperty() {
    super(COLOR, "black", INHERITED, ANIMATABLE,
        NodeStyle::color, NodeStyle::color,
        extractor::extract, extractor::isValid);
  }
}
