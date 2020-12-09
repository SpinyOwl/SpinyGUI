package com.spinyowl.spinygui.core.converter.css.model.property.border.color;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.converter.css.model.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderRightColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderRightColorProperty() {
    super(BORDER_RIGHT_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, c) -> s.border().right().color(c),
        s -> s.border().right().color(),
        extractor::extract, extractor::isValid);
  }
}
