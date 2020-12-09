package com.spinyowl.spinygui.core.converter.css.model.property.padding;

import static com.spinyowl.spinygui.core.converter.css.Properties.PADDING_LEFT;
import com.spinyowl.spinygui.core.converter.css.model.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingLeftProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingLeftProperty() {
    super(PADDING_LEFT, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.padding().left(v), s -> s.padding().left(),
        extractor::extract, extractor::isValid);
  }
}
