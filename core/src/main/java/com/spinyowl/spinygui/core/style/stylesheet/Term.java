package com.spinyowl.spinygui.core.style.stylesheet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.NotImplementedException;

/**
 * Term is a part of a property value. For example, in the following declaration: <br>
 * <code>font-size: 12px;</code> <br>
 * <code>font-size</code> is a property name, <code>12px</code> is a property value, <code>12</code>
 * and <code>px</code> are terms.
 *
 * <p>Note: toString() method is used to generate css-compatible value.
 *
 * @param <T> type of term value.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Term<T> {
  protected T value;

  public String toString() {
    throw new NotImplementedException("toString() method is not implemented for " + getClass());
  }
}
