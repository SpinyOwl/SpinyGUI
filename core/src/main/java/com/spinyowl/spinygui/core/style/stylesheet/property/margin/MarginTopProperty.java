package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;

public class MarginTopProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginTopProperty() {
    super(
        MARGIN_TOP,
        "0",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(MARGIN_TOP, extractor.extract(value)),
        extractor::isValid);
  }
}
