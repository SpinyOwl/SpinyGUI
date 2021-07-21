package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LEFT;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Map;

public class LeftProperty extends Property {
  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public LeftProperty() {
    super(
        LEFT,
        "auto",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(LEFT, extractor.extract(value)),
        extractor::isValid);
  }
}
