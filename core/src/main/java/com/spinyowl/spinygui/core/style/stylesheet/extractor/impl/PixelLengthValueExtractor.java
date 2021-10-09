package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import static com.spinyowl.spinygui.core.style.stylesheet.extractor.impl.UnitValueExtractor.getPixelLength;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Length.PixelLength;

/** Used to extract {@link Length} value from string. */
public class PixelLengthValueExtractor implements ValueExtractor<PixelLength> {

  @Override
  public Class<PixelLength> getType() {
    return PixelLength.class;
  }

  @Override
  public boolean isValid(String value) {
    if (value == null) {
      return true;
    }
    return value.matches(UnitValueExtractor.PIXEL_REGEX)
        || value.matches(UnitValueExtractor.ZERO_REGEX);
  }

  @Override
  public PixelLength extract(String value) {
    return value.matches(UnitValueExtractor.PERCENTAGE_REGEX) ? Length.ZERO : getPixelLength(value);
  }
}
