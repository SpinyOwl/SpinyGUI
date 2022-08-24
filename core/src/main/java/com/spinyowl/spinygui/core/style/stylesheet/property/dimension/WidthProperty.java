package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WIDTH;

public class WidthProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public WidthProperty() {
    super(
        WIDTH,
        "auto",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(WIDTH, extractor.extract(value)),
        extractor::isValid);
  }
}
