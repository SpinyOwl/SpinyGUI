package com.spinyowl.spinygui.core.converter.css.model.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_COLOR;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BackgroundColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

  public BackgroundColorProperty() {
    super(BACKGROUND_COLOR, "transparent", !INHERITED, ANIMATABLE,
        (s, c) -> s.background().color(c),
        s -> s.background().color(),
        colorExtractor::extract,
        colorExtractor::isValid);
  }
}