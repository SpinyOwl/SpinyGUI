package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_SHRINK;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class FlexShrinkProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexShrinkProperty() {
    super(
        FLEX_SHRINK,
        "0",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::flexShrink,
        NodeStyle::flexShrink,
        extractor::extract,
        extractor::isValid);
  }
}
