package com.spinyowl.spinygui.core.style.stylesheet.extractor.impl;

import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.stylesheet.extractor.ValueExtractorException;
import com.spinyowl.spinygui.core.node.style.types.length.Length;
import com.spinyowl.spinygui.core.node.style.types.length.Unit;

/**
 * Used to extract {@link Unit} value from string.
 */
public class UnitValueExtractor implements ValueExtractor<Unit> {

  public static final String PERCENTAGE_REGEX = "-?(\\d+(\\.\\d*)?|\\.\\d+)%";
  public static final String PIXEL_REGEX = "-?\\d+[pP][xX]";
  public static final String AUTO_REGEX = "[aA][uU][tT][oO]";
  public static final String ZERO_REGEX = "0+";

  public static Length getLength(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    if (value.matches(PIXEL_REGEX)) {
      String pixelValue = value.substring(0, value.length() - 2);
      return Length.pixel(Integer.parseInt(pixelValue));
    }

    if (value.matches(PERCENTAGE_REGEX)) {
      String percentageValue = value.substring(0, value.length() - 1);
      return Length.percent(Float.parseFloat(percentageValue));
    }

    if (value.matches(ZERO_REGEX)) {
      return Length.pixel(0);
    }

    throw new ValueExtractorException(Length.class, value);
  }

  @Override
  public Class<Unit> getType() {
    return Unit.class;
  }

  @Override
  public boolean isValid(String value) {
    return value.matches(PERCENTAGE_REGEX) ||
        value.matches(PIXEL_REGEX) ||
        value.matches(AUTO_REGEX) ||
        value.matches(UnitValueExtractor.ZERO_REGEX);
  }

  @Override
  public Unit extract(String value) {
    if (value.matches(AUTO_REGEX)) {
      return Unit.AUTO;
    }

    return getLength(value);
  }
}
