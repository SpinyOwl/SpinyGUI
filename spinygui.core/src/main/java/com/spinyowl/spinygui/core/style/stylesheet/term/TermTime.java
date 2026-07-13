package com.spinyowl.spinygui.core.style.stylesheet.term;

import com.spinyowl.spinygui.core.style.stylesheet.Term;

/** A CSS time value stored in seconds. */
public class TermTime extends Term<Double> {
  public TermTime(Double seconds) { super(seconds); }
  @Override public String toString() { return value + "s"; }
}
