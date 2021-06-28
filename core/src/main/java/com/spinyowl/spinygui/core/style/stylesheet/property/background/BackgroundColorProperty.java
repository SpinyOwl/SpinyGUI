package com.spinyowl.spinygui.core.style.stylesheet.property.background;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BACKGROUND_COLOR;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BackgroundColorProperty extends Property<Color> {

  private static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

  public BackgroundColorProperty() {
    super(
        BACKGROUND_COLOR,
        "transparent",
        !INHERITED,
        ANIMATABLE,
        NodeStyle::backgroundColor,
        NodeStyle::backgroundColor,
        colorExtractor::extract,
        colorExtractor::isValid);
  }
}
