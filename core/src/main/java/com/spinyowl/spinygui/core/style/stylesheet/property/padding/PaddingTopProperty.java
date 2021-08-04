package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;

public class PaddingTopProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingTopProperty() {
    super(
        PADDING_TOP,
        "0",
        !INHERITED,
        ANIMATABLE,
        (value, styles) -> styles.put(PADDING_TOP, extractor.extract(value)),
        extractor::isValid);
  }
}
