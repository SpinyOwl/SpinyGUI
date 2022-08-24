package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;

public class PaddingRightProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingRightProperty() {
    super(
        PADDING_RIGHT,
        "0",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(PADDING_RIGHT, extractor.extract(value)),
        extractor::isValid);
  }
}
