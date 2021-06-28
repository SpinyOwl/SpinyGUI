package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_GROW;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class FlexGrowProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public FlexGrowProperty() {
    super(
        FLEX_GROW,
        "0",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::flexGrow,
        NodeStyle::flexGrow,
        extractor::extract,
        extractor::isValid);
  }
}
