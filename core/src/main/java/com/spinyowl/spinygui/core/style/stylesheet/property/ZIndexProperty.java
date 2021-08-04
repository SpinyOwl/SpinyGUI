package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.Z_INDEX;

public class ZIndexProperty extends Property {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);
  private static final String AUTO = "auto";

  public ZIndexProperty() {
    super(
        Z_INDEX,
        AUTO,
        !INHERITED,
        !ANIMATABLE,
        (zIndex, styles) ->
            styles.put(Z_INDEX, AUTO.equals(zIndex) ? 0 : extractor.extract(zIndex)),
        value -> AUTO.equals(value) || extractor.isValid(value));
  }
}
