package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;
import static com.spinyowl.spinygui.core.style.stylesheet.property.border.color.BorderColorProperty.DEFAULT_VALUE;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.Map;

public class BorderRightColorProperty extends Property {

  private static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

  public BorderRightColorProperty() {
    super(
        BORDER_RIGHT_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BORDER_RIGHT_COLOR, extractor.extract(value)),
        extractor::isValid);
  }
}
