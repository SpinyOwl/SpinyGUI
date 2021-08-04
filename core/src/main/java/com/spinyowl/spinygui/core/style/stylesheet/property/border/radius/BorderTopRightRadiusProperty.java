package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;

public class BorderTopRightRadiusProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderTopRightRadiusProperty() {
    super(
        BORDER_TOP_RIGHT_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(BORDER_TOP_RIGHT_RADIUS, extractor.extract(value)),
        extractor::isValid);
  }
}
