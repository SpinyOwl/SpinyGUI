package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM_RIGHT_RADIUS;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderBottomRightRadiusProperty extends Property<Length> {

  public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderBottomRightRadiusProperty() {
    super(BORDER_BOTTOM_RIGHT_RADIUS, "0", !INHERITED, ANIMATABLE,
        (s, l) -> s.borderRadius().bottomRight(l),
        s -> s.borderRadius().bottomRight(),
        extractor::extract, extractor::isValid);
  }

}
