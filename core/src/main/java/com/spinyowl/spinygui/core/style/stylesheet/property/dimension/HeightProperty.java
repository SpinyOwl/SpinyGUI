package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.HEIGHT;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;

public class HeightProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public HeightProperty() {
    super(
        HEIGHT,
        "auto",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(HEIGHT, extractor.extract(value)),
        extractor::isValid);
  }
}
