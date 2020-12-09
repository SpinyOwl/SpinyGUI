package com.spinyowl.spinygui.core.converter.css.model.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_SHRINK;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;

public class FlexShrinkProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexShrinkProperty() {
    super(FLEX_SHRINK, "0", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().flexShrink(v), s -> s.flex().flexShrink(),
        extractor::extract, extractor::isValid);
  }
}
