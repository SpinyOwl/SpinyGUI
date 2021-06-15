package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class BackgroundColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

  public BackgroundColorProperty() {
    super(
        BACKGROUND_COLOR,
        "transparent",
        !INHERITED,
        ANIMATABLE,
        (s, c) -> s.background().color(c),
        s -> s.background().color(),
        colorExtractor::extract,
        colorExtractor::isValid);
  }
}
