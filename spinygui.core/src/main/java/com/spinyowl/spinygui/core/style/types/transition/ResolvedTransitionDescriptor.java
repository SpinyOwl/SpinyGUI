package com.spinyowl.spinygui.core.style.types.transition;

import java.util.Objects;

/** A single property-specific descriptor after CSS list matching. */
public record ResolvedTransitionDescriptor(
    TransitionPropertyName property, TransitionTime duration, TransitionTimingFunction timingFunction, TransitionTime delay) {
  public ResolvedTransitionDescriptor {
    Objects.requireNonNull(property, "property must not be null");
    Objects.requireNonNull(duration, "duration must not be null");
    Objects.requireNonNull(timingFunction, "timingFunction must not be null");
    Objects.requireNonNull(delay, "delay must not be null");
  }
  public boolean isImmediate() { return duration.seconds() == 0d && delay.seconds() == 0d; }
}
