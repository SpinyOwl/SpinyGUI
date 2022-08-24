package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;

public class BorderLeftColorProperty extends Property {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderLeftColorProperty() {
    super(
        BORDER_LEFT_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_LEFT_COLOR, extractor.extract(value)),
        extractor::isValid);
  }
}
