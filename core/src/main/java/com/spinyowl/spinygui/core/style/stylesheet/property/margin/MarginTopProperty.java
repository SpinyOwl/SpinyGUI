package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_TOP;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginTopProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginTopProperty() {
    super(
        MARGIN_TOP,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::marginTop,
        NodeStyle::marginTop,
        extractor::extract,
        extractor::isValid);
  }
}
