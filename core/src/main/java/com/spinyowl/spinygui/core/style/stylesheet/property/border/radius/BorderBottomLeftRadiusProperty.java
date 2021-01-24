package com.spinyowl.spinygui.core.style.stylesheet.property.border.radius;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_LEFT_RADIUS;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Length;

public class BorderBottomLeftRadiusProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderBottomLeftRadiusProperty() {
    super(BORDER_BOTTOM_LEFT_RADIUS, "0", !INHERITED, ANIMATABLE,
        (s, l) -> s.borderRadius().bottomLeft(l),
        s -> s.borderRadius().bottomLeft(),
        extractor::extract, extractor::isValid);
  }
}
