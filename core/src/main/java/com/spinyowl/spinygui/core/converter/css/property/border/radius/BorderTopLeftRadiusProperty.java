package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_TOP_LEFT_RADIUS;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderTopLeftRadiusProperty extends Property<Length> {

  public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public BorderTopLeftRadiusProperty() {
    super(BORDER_TOP_LEFT_RADIUS, "0", !INHERITED, ANIMATABLE,
      (s, l) -> s.borderRadius().topLeft(l),
      s -> s.borderRadius().topLeft(),
      extractor::extract, extractor::isValid);
  }
}
