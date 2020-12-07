package com.spinyowl.spinygui.core.converter.css.property.position;

import static com.spinyowl.spinygui.core.converter.css.Properties.RIGHT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class RightProperty extends Property<Unit> {

  private static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public RightProperty() {
    super(RIGHT, "auto", !INHERITED, ANIMATABLE,
        NodeStyle::right, NodeStyle::right,
        extractor::extract, extractor::isValid);
  }

}
