package com.spinyowl.spinygui.core.converter.css.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN_TOP;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginTopProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginTopProperty() {
    super(MARGIN_TOP, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.margin().top(v), s -> s.margin().top(),
        extractor::extract, extractor::isValid);
  }
}
