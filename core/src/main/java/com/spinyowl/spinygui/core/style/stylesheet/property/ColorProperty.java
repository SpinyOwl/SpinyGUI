package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;

public class ColorProperty extends Property {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public ColorProperty() {
    super(
        COLOR,
        "black",
        INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(COLOR, extractor.extract(value)),
        extractor::isValid);
  }
}
