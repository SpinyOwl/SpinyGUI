package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;

public class BackgroundColorProperty extends Property {

  private static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

  public BackgroundColorProperty() {
    super(
        BACKGROUND_COLOR,
        "transparent",
        !INHERITABLE,
        ANIMATABLE,
        (value, styles) -> styles.put(BACKGROUND_COLOR, colorExtractor.extract(value)),
        colorExtractor::isValid);
  }
}
