package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_TOP;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class PaddingTopProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingTopProperty() {
    super(
        PADDING_TOP,
        "0",
        !INHERITED,
        ANIMATABLE,
        (s, v) -> s.padding().top(v),
        s -> s.padding().top(),
        extractor::extract,
        extractor::isValid);
  }
}
