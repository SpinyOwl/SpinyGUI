package com.spinyowl.spinygui.core.converter.css.model.property.dimension;

import static com.spinyowl.spinygui.core.converter.css.Properties.WIDTH;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class WidthProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public WidthProperty() {
    super(WIDTH, "auto", !INHERITED, ANIMATABLE,
        NodeStyle::width, NodeStyle::width,
        extractor::extract, extractor::isValid);
  }
}
