package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.types.Color;
import lombok.EqualsAndHashCode;

/** Represents color term. */
@EqualsAndHashCode
public class TermColor extends Term<Color> {
  public TermColor(Color value) {
    super(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }
}
