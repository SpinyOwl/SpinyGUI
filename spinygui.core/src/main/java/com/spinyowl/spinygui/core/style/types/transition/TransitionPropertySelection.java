package com.spinyowl.spinygui.core.style.types.transition;

import java.util.Objects;

/** A validated transition-property selection. */
public sealed interface TransitionPropertySelection
    permits TransitionPropertySelection.None, TransitionPropertySelection.All, TransitionPropertySelection.Named {
  record None() implements TransitionPropertySelection {}
  record All() implements TransitionPropertySelection {}
  record Named(TransitionPropertyName property) implements TransitionPropertySelection {
    public Named { Objects.requireNonNull(property, "property must not be null"); }
  }
  TransitionPropertySelection NONE = new None();
  TransitionPropertySelection ALL = new All();
}
