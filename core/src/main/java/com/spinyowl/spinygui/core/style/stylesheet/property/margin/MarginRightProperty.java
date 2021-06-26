package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_RIGHT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MarginRightProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginRightProperty() {
    super(
        MARGIN_RIGHT,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::marginRight,
        NodeStyle::marginRight,
        extractor::extract,
        extractor::isValid);
  }
}
