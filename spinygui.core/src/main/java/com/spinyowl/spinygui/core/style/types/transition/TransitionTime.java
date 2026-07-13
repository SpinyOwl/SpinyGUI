package com.spinyowl.spinygui.core.style.types.transition;

/** A non-negative CSS transition time, expressed in seconds. */
public record TransitionTime(double seconds) {
  public static final TransitionTime ZERO = new TransitionTime(0d);

  public TransitionTime {
    if (!Double.isFinite(seconds) || seconds < 0d) {
      throw new IllegalArgumentException("transition time must be finite and non-negative");
    }
  }
}
