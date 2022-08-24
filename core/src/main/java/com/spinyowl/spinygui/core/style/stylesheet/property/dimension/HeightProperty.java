package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.HEIGHT;

public class HeightProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public HeightProperty() {
    super(
        HEIGHT,
        "auto",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(HEIGHT, extractor.extract(value)),
        extractor::isValid);
  }
}
