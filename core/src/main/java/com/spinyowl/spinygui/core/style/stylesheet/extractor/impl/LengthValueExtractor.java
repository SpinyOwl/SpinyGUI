package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import static com.spinyowl.spinygui.core.style.stylesheet.extractor.impl.UnitValueExtractor.getLength;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.types.length.Length;

/** Used to extract {@link Length} value from string. */
public class LengthValueExtractor implements ValueExtractor<Length> {

  @Override
  public Class<Length> getType() {
    return Length.class;
  }

  @Override
  public boolean isValid(String value) {
    if (value == null) {
      return true;
    }
    return value.matches(UnitValueExtractor.PERCENTAGE_REGEX)
        || value.matches(UnitValueExtractor.PIXEL_REGEX)
        || value.matches(UnitValueExtractor.ZERO_REGEX);
  }

  @Override
  public Length<?> extract(String value) {
    return getLength(value);
  }
}
