package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractorException;
import org.apache.commons.lang3.math.NumberUtils;

/** Extracts integer value from string (10 radix). */
public class FloatExtractor implements ValueExtractor<Float> {

  @Override
  public Class<Float> getType() {
    return Float.class;
  }

  @Override
  public boolean isValid(String value) {
    return NumberUtils.isCreatable(value);
  }

  @Override
  public Float extract(String value) {
    try {
      return NumberUtils.createFloat(value);
    } catch (NumberFormatException e) {
      throw new ValueExtractorException(getType(), value);
    }
  }
}
