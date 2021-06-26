package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_BOTTOM;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MarginBottomProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginBottomProperty() {
    super(
        MARGIN_BOTTOM,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::marginBottom,
        NodeStyle::marginBottom,
        extractor::extract,
        extractor::isValid);
  }
}
