package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import lombok.EqualsAndHashCode;

/** Represents integer term. */
@EqualsAndHashCode
public class TermInteger extends Term<Integer> {

  public TermInteger(Integer value) {
    super(value);
  }

  @Override
  public String toString() {
    return Integer.toString(value);
  }
}
