package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TAB_SIZE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import java.util.Map;

public class TabSizeProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public TabSizeProperty() {
    super(
        TAB_SIZE,
        "4",
        INHERITED,
        !ANIMATABLE,
        value -> Map.of(TAB_SIZE, extractor.extract(value)),
        extractor::isValid);
  }
}
