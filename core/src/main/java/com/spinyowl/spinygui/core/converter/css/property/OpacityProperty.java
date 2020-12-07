package com.spinyowl.spinygui.core.converter.css.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.OPACITY;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class OpacityProperty extends Property<Float> {

  private static final ValueExtractor<Float> extractor = ValueExtractors.of(Float.class);

  public OpacityProperty() {
    super(OPACITY, "1", !INHERITED, ANIMATABLE, NodeStyle::opacity, NodeStyle::opacity,
        extractor::extract, extractor::isValid);
  }
}
