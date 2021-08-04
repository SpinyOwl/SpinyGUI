package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;

public class BorderBottomColorProperty extends Property {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderBottomColorProperty() {
    super(
        BORDER_BOTTOM_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_BOTTOM_COLOR, extractor.extract(value)),
        extractor::isValid);
  }
}
