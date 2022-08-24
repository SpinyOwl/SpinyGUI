package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_GROW;

public class FlexGrowProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexGrowProperty() {
    super(
        FLEX_GROW,
        "0",
        !INHERITABLE,
        !ANIMATABLE,
        (value, styles) -> styles.put(FLEX_GROW, extractor.extract(value)),
        extractor::isValid);
  }
}
