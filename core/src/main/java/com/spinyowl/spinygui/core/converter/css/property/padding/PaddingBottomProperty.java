package com.spinyowl.spinygui.core.converter.css.property.padding;

import static com.spinyowl.spinygui.core.converter.css.Properties.PADDING_BOTTOM;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingBottomProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingBottomProperty() {
    super(PADDING_BOTTOM, "0", !INHERITED, ANIMATABLE,
        (s, v) -> s.padding().bottom(v), s -> s.padding().bottom(),
        extractor::extract, extractor::isValid);
  }
}
