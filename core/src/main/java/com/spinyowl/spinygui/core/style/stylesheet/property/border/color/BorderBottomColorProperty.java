package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.Color;

public class BorderBottomColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderBottomColorProperty() {
    super(BORDER_BOTTOM_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, c) -> s.border().bottom().color(c),
        s -> s.border().bottom().color(),
        extractor::extract, extractor::isValid);
  }
}
