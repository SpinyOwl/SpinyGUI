package com.spinyowl.spinygui.core.style.stylesheet.property.margin;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.MARGIN_LEFT;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class MarginLeftProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public MarginLeftProperty() {
    super(
        MARGIN_LEFT,
        "0",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::marginLeft,
        NodeStyle::marginLeft,
        extractor::extract,
        extractor::isValid);
  }
}
