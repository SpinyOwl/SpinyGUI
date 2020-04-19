package com.spinyowl.spinygui.core.converter.css.property.border.color;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM_COLOR;
import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderBottomColorProperty extends Property<Color> {

  public static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderBottomColorProperty() {
    super(BORDER_BOTTOM_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
        (s, c) -> s.border().bottom().color(c),
        s -> s.border().bottom().color(),
        extractor::extract, extractor::isValid);
  }
}
