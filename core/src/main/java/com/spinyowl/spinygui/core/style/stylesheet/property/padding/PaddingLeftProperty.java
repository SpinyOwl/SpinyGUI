package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_LEFT;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.length.Length;

public class PaddingLeftProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingLeftProperty() {
    super(PADDING_LEFT, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.padding().left(v), s -> s.padding().left(),
        extractor::extract, extractor::isValid);
  }
}
