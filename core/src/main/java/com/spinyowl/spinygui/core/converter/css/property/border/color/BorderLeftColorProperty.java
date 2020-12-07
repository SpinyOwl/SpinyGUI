package com.spinyowl.spinygui.core.converter.css.property.border.color;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_LEFT_COLOR;
import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderLeftColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderLeftColorProperty() {
    super(BORDER_LEFT_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, c) -> s.border().left().color(c),
        s -> s.border().left().color(),
        extractor::extract, extractor::isValid);
  }
}
