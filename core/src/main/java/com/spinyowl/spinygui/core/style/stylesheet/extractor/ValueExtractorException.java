package com.spinyowl.spinygui.core.style.stylesheet.extractor;

/**
 * Exception thrown during extracting non-valid value.
 */
public class ValueExtractorException extends RuntimeException {

  public ValueExtractorException(Class<?> clazz, String value) {
    super(String.format(
        "Failed to extract '%s' type from '%s' value.", clazz.getName(), value
    ));
  }

}
