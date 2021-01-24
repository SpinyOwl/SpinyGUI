package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;

public class MarginBottomProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginBottomProperty() {
    super(MARGIN_BOTTOM, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.margin().bottom(v), s -> s.margin().bottom(),
        extractor::extract, extractor::isValid);
  }
}
