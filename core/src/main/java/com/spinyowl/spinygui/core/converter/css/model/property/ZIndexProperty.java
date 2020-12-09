package com.spinyowl.spinygui.core.converter.css.model.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.Z_INDEX;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class ZIndexProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);
  private static final String AUTO = "auto";

  public ZIndexProperty() {
    super(Z_INDEX, AUTO, !INHERITED, !ANIMATABLE, NodeStyle::zIndex, NodeStyle::zIndex,
        v -> AUTO.equals(v) ? 0 : extractor.extract(v),
        value -> AUTO.equals(value) || extractor.isValid(value));
  }
}
