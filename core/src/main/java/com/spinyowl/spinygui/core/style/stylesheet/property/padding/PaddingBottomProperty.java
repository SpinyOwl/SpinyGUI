package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;

public class PaddingBottomProperty extends Property<Length> {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingBottomProperty() {
    super(
        PADDING_BOTTOM,
        "0",
        !INHERITED,
        ANIMATABLE,
        (s, v) -> s.padding().bottom(v),
        s -> s.padding().bottom(),
        extractor::extract,
        extractor::isValid);
  }
}
