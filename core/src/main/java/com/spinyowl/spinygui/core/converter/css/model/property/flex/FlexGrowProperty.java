package com.spinyowl.spinygui.core.converter.css.model.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_GROW;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;

public class FlexGrowProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexGrowProperty() {
    super(FLEX_GROW, "0", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().flexGrow(v), s -> s.flex().flexGrow(),
        extractor::extract, extractor::isValid);
  }
}
