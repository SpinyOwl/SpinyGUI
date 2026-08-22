package com.spinyowl.spinygui.core.event.processor;

/** Conservative presentation impact of one processed input batch. */
public enum InputImpact {
  NO_IMPACT,
  HOVER_STYLE,
  FULL_REFRESH,
  FULL_UNKNOWN;

  public InputImpact combine(InputImpact other) {
    if (other == null) throw new NullPointerException("Input impact must not be null");
    return ordinal() >= other.ordinal() ? this : other;
  }
}
