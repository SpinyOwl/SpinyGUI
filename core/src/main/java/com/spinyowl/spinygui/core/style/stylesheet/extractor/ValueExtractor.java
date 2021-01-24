package com.spinyowl.spinygui.core.style.stylesheet.extractor;

/**
 * Value extractor allows to extract value of type {@link T} from string.
 *
 * @param <T> value type.
 */
public interface ValueExtractor<T> {

  /**
   * Returns type of target value type.
   *
   * @return tupe of target value type.
   */
  Class<T> getType();

  /**
   * Returns true if string value is valid and could be extracted.
   *
   * @param value value to test.
   * @return true if valid and could be extracted.
   */
  boolean isValid(String value);

  /**
   * Returns extracted value.
   *
   * @param value string representation of value to extract.
   * @return extracted value.
   * @throws ValueExtractorException in case if value is not valid.
   */
  T extract(String value);

}
