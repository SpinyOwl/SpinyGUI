package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MAX_HEIGHT;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MaxHeightProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MaxHeightProperty() {
    super(
        MAX_HEIGHT,
        "none",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::maxHeight,
        NodeStyle::maxHeight,
        value ->
            "none".equalsIgnoreCase(value)
                ? Length.pixel(Integer.MAX_VALUE)
                : extractor.extract(value),
        extractor::isValid);
  }
}
