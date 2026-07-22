package com.spinyowl.spinygui.core.style.types.transition;

import java.util.List;
import java.util.Objects;

/** The four computed transition longhand lists. */
public record TransitionConfiguration(
    List<TransitionPropertySelection> properties,
    List<TransitionTime> durations,
    List<TransitionTimingFunction> timingFunctions,
    List<TransitionTime> delays) {
  public static final TransitionConfiguration INITIAL = new TransitionConfiguration(
      List.of(TransitionPropertySelection.ALL), List.of(TransitionTime.ZERO),
      List.of(TransitionTimingFunction.EASE), List.of(TransitionTime.ZERO));
  public TransitionConfiguration {
    properties = copyNonEmpty(properties, "properties");
    durations = copyNonEmpty(durations, "durations");
    timingFunctions = copyNonEmpty(timingFunctions, "timingFunctions");
    delays = copyNonEmpty(delays, "delays");
  }
  private static <T> List<T> copyNonEmpty(List<T> values, String name) {
    Objects.requireNonNull(values, name + " must not be null");
    if (values.isEmpty() || values.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException(name + " must not be empty or contain nulls");
    return List.copyOf(values);
  }
}
