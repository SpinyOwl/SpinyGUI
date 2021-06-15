package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_TOP_LEFT_RADIUS;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderTopLeftRadiusProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderTopLeftRadiusProperty() {
    super(
        BORDER_TOP_LEFT_RADIUS,
        "0",
        !INHERITED,
        ANIMATABLE,
        (s, l) -> s.borderRadius().topLeft(l),
        s -> s.borderRadius().topLeft(),
        extractor::extract,
        extractor::isValid);
  }
}
