package com.spinyowl.spinygui.core.style.stylesheet.property.padding;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.PADDING_BOTTOM;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Map;

public class PaddingBottomProperty extends Property {

  private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

  public PaddingBottomProperty() {
    super(
        PADDING_BOTTOM,
        "0",
        !INHERITED,
        ANIMATABLE,
        value -> Map.of(PADDING_BOTTOM, extractor.extract(value)),
        extractor::isValid);
  }
}
