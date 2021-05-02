package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractorException;

/** Extracts integer value from string (10 radix). */
public class IntegerExtractor implements ValueExtractor<Integer> {

  @Override
  public Class<Integer> getType() {
    return Integer.class;
  }

  @Override
  public boolean isValid(String value) {
    if (value.isEmpty()) {
      return false;
    }
    for (var i = 0; i < value.length(); i++) {
      if (i == 0 && value.charAt(i) == '-') {
        if (value.length() == 1) {
          return false;
        } else {
          continue;
        }
      }
      if (Character.digit(value.charAt(i), 10) < 0) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Integer extract(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new ValueExtractorException(getType(), value);
    }
  }
}
