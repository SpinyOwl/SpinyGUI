package com.spinyowl.spinygui.core.style.types.length;

/**
 * Converts length to pixels.
 *
 * @param <T> type of length value.
 */
@FunctionalInterface
public interface LengthConverter<T extends Number> {

  /**
   * Used to convert length of specified type to pixels.
   *
   * @param original original length.
   * @param baseLength base length (that could be used to calculate relative length).
   * @return calculated length.
   */
  int convert(Length<T> original, float baseLength);

  /**
   * Used to convert length of specified type to pixels. Base length is 1.
   *
   * @param original original length.
   * @return calculated length.
   */
  default int convert(Length<T> original) {
    return convert(original, 1);
  }
}
