package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;

public class MarginRightProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginRightProperty() {
    super(
        MARGIN_RIGHT,
        "0",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(MARGIN_RIGHT, extractor.extract(value)),
        extractor::isValid);
  }
}
