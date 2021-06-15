package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_WIDTH;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MaxWidthProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MaxWidthProperty() {
    super(
        MAX_WIDTH,
        "none",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::maxWidth,
        NodeStyle::maxWidth,
        value ->
            "none".equalsIgnoreCase(value)
                ? Length.pixel(Integer.MAX_VALUE)
                : extractor.extract(value),
        extractor::isValid);
  }
}
