package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;

/** A CSS angle expressed in degrees. */
public class TermAngle extends Term<Float> {
  public TermAngle(Float value) {
    super(value);
  }

  @Override
  public String toString() {
    return value + "deg";
  }
}
