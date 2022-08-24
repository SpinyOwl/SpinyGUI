package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;

public class PaddingLeftProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingLeftProperty() {
    super(
        PADDING_LEFT,
        "0",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(PADDING_LEFT, extractor.extract(value)),
        extractor::isValid);
  }
}
