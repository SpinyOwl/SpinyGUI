package com.spinyowl.spinygui.core.style.types.transition;

import java.util.Objects;

/** Immutable transition configuration entry before property-list resolution. */
public record TransitionDescriptor(
    TransitionPropertySelection selection,
    TransitionTime duration,
    TransitionTimingFunction timingFunction,
    TransitionTime delay) {
  public static final TransitionDescriptor INITIAL = new TransitionDescriptor(
      TransitionPropertySelection.ALL, TransitionTime.ZERO, TransitionTimingFunction.EASE, TransitionTime.ZERO);
  public TransitionDescriptor {
    Objects.requireNonNull(selection, "selection must not be null");
    Objects.requireNonNull(duration, "duration must not be null");
    Objects.requireNonNull(timingFunction, "timingFunction must not be null");
    Objects.requireNonNull(delay, "delay must not be null");
  }
}
