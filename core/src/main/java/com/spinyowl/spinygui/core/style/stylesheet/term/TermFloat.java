package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TermFloat extends Term<Float> {

  public TermFloat(Float value) {
    super(value);
  }

  @Override
  public String toString() {
    return Float.toString(value);
  }
}
