package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.OPACITY;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class OpacityProperty extends Property<Float> {

  private static final ValueExtractor<Float> extractor = ValueExtractors.of(Float.class);

  public OpacityProperty() {
    super(OPACITY, "1", !INHERITED, ANIMATABLE, NodeStyle::opacity, NodeStyle::opacity,
        extractor::extract, extractor::isValid);
  }
}
