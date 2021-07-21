package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_RIGHT_RADIUS;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Map;

public class BorderTopRightRadiusProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderTopRightRadiusProperty() {
    super(
        BORDER_TOP_RIGHT_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BORDER_TOP_RIGHT_RADIUS, extractor.extract(value)),
        extractor::isValid);
  }
}
