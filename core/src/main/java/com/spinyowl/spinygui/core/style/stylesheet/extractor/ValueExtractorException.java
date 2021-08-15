package com.spinyowl.spinygui.core.style.stylesheet.extractor;

/**
 * Exception thrown during extracting non-valid value.
 */
public class ValueExtractorException extends RuntimeException {

  public ValueExtractorException(Class<?> clazz, String value, String explanation) {
    super(String.format("Failed to extract '%s' type from '%s' value. %s", clazz.getName(), value,
        explanation));
  }

  public ValueExtractorException(Class<?> clazz, String value, Throwable t) {
    super(String.format("Failed to extract '%s' type from '%s' value.", clazz.getName(), value), t);
  }
}
