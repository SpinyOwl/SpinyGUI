package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.Map;

public class BackgroundColorProperty extends Property {

  private static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

  public BackgroundColorProperty() {
    super(
        BACKGROUND_COLOR,
        "transparent",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(BACKGROUND_COLOR, colorExtractor.extract(value)),
        colorExtractor::isValid);
  }
}
