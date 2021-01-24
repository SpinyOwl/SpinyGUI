package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_RIGHT;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Length;

public class PaddingRightProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingRightProperty() {
    super(PADDING_RIGHT, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.padding().right(v), s -> s.padding().right(),
        extractor::extract, extractor::isValid);
  }
}
