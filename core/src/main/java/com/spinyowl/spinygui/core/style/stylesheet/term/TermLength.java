package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.types.length.Length;
import lombok.EqualsAndHashCode;

/** Represents length term. */
@EqualsAndHashCode
public class TermLength extends TermUnit<Length<?>> {
  public TermLength(Length<?> value) {
    super(value);
  }

  public String toString() {
    return value.toString();
  }
}
