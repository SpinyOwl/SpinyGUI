package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderBottomRightRadiusProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderBottomRightRadiusProperty() {
    super(
        BORDER_BOTTOM_RIGHT_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        (s, l) -> s.borderRadius().bottomRight(l),
        s -> s.borderRadius().bottomRight(),
        extractor::extract,
        extractor::isValid);
  }
}
