package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_SHRINK;

public class FlexShrinkProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexShrinkProperty() {
    super(
        FLEX_SHRINK,
        "0",
        !INHERITABLE,
        !ANIMATABLE,
        (value, styles) -> styles.put(FLEX_SHRINK, extractor.extract(value)),
        extractor::isValid);
  }
}
