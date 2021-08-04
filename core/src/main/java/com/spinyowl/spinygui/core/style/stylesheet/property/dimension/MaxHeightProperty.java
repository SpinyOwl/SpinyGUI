package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_HEIGHT;

public class MaxHeightProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MaxHeightProperty() {
    super(
        MAX_HEIGHT,
        "none",
        !INHERITED,
        ANIMATABLE,
        (value, styles) ->
            styles.put(
                MAX_HEIGHT,
                "none".equalsIgnoreCase(value)
                    ? Length.pixel(Integer.MAX_VALUE)
                    : extractor.extract(value)),
        extractor::isValid);
  }
}
