package com.spinyowl.spinygui.core.style.manager;

/** Conservative downstream impact of one completed style recalculation. */
public enum StyleImpact {
  NO_CHANGE,
  PAINT_ONLY,
  TRANSFORM,
  LAYOUT,
  FULL_UNKNOWN;

  public StyleImpact combine(StyleImpact other) {
    if (other == null) throw new NullPointerException("Style impact must not be null");
    return ordinal() >= other.ordinal() ? this : other;
  }
}
