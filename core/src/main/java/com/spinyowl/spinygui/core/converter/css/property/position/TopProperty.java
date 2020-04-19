package com.spinyowl.spinygui.core.converter.css.property.position;

import static com.spinyowl.spinygui.core.converter.css.Properties.TOP;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class TopProperty extends Property<Unit> {

  public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

  public TopProperty() {
    super(TOP, "auto", !INHERITED, ANIMATABLE,
      NodeStyle::top, NodeStyle::top,
      extractor::extract, extractor::isValid);
  }
}
