package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.node.style.types.Color;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BorderRightColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderRightColorProperty() {
    super(
        BORDER_RIGHT_COLOR,
        DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        (s, c) -> s.border().right().color(c),
        s -> s.border().right().color(),
        extractor::extract,
        extractor::isValid);
  }
}
