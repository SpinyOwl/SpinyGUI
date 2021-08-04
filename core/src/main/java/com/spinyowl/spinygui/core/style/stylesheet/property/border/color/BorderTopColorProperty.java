package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_COLOR;

public class BorderTopColorProperty extends Property {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderTopColorProperty() {
    super(
        BORDER_TOP_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_TOP_COLOR, extractor.extract(value)),
        extractor::isValid);
  }
}
