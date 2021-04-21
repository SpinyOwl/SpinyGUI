package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_HEIGHT;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MinHeightProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MinHeightProperty() {
    super(
        MIN_HEIGHT,
        "0px",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::minHeight,
        NodeStyle::minHeight,
        extractor::extract,
        extractor::isValid);
  }
}
