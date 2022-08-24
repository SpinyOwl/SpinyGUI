package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_HEIGHT;

public class MinHeightProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MinHeightProperty() {
    super(
        MIN_HEIGHT,
        "0px",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(MIN_HEIGHT, extractor.extract(value)),
        extractor::isValid);
  }
}
