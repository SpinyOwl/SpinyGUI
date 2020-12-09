package com.spinyowl.spinygui.core.converter.css.model.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.TAB_SIZE;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class TabSizeProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public TabSizeProperty() {
    super(TAB_SIZE, "4", INHERITED, !ANIMATABLE, NodeStyle::tabSize, NodeStyle::tabSize,
        extractor::extract, extractor::isValid);
  }
}
