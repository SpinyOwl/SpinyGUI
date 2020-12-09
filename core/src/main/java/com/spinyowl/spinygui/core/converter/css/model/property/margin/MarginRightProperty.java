package com.spinyowl.spinygui.core.converter.css.model.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN_RIGHT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginRightProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginRightProperty() {
    super(MARGIN_RIGHT, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.margin().right(v), s -> s.margin().right(),
        extractor::extract, extractor::isValid);
  }
}
