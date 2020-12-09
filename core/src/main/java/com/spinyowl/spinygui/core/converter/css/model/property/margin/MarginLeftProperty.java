package com.spinyowl.spinygui.core.converter.css.model.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN_LEFT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginLeftProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginLeftProperty() {
    super(MARGIN_LEFT, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.margin().left(v), s -> s.margin().left(),
        extractor::extract, extractor::isValid);
  }
}
