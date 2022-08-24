package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;

public class MarginBottomProperty extends Property {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginBottomProperty() {
    super(
        MARGIN_BOTTOM,
        "0",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(MARGIN_BOTTOM, extractor.extract(value)),
        extractor::isValid);
  }
}
