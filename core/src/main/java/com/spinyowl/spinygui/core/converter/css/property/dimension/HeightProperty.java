package com.spinyowl.spinygui.core.converter.css.property.dimension;

import static com.spinyowl.spinygui.core.converter.css.Properties.HEIGHT;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class HeightProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public HeightProperty() {
    super(HEIGHT, "auto", !INHERITED, ANIMATABLE,
        NodeStyle::height, NodeStyle::height,
        extractor::extract, extractor::isValid);
  }
}
