package com.spinyowl.spinygui.core.converter.css.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN_BOTTOM;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginBottomProperty extends Property<Unit> {

  public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginBottomProperty() {
    super(MARGIN_BOTTOM, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.margin().bottom(v), s -> s.margin().bottom(),
        extractor::extract, extractor::isValid);
  }
}
