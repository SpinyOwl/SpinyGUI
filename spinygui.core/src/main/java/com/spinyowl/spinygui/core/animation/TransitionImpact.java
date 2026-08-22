package com.spinyowl.spinygui.core.animation;

/** Downstream impact of advancing active transition tracks. */
public enum TransitionImpact {
  NO_CHANGE,
  PAINT,
  TRANSFORM;

  public TransitionImpact combine(TransitionImpact other) {
    if (other == null) throw new NullPointerException("Transition impact must not be null");
    return ordinal() >= other.ordinal() ? this : other;
  }
}
