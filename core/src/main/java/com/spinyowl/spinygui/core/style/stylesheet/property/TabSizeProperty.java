package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TAB_SIZE;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class TabSizeProperty extends Property<Integer> {

  private static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

  public TabSizeProperty() {
    super(
        TAB_SIZE,
        "4",
        INHERITED,
        !ANIMATABLE,
        NodeStyle::tabSize,
        NodeStyle::tabSize,
        extractor::extract,
        extractor::isValid);
  }
}
