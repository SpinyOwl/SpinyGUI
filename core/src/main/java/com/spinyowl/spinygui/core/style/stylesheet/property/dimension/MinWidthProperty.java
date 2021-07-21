package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MIN_WIDTH;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Map;

public class MinWidthProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public MinWidthProperty() {
    super(
        MIN_WIDTH,
        "0px",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(MIN_WIDTH, extractor.extract(value)),
        extractor::isValid);
  }
}
