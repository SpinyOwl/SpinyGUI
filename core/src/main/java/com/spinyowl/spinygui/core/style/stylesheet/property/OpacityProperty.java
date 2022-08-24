package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;

public class OpacityProperty extends Property {

  private static final ValueExtractor<Float> extractor = ValueExtractors.of(Float.class);

  public OpacityProperty() {
    super(
        OPACITY,
        "1",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(OPACITY, extractor.extract(value)),
        extractor::isValid);
  }
}
