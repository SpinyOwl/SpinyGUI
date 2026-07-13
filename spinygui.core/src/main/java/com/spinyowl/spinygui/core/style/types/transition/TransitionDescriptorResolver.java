package com.spinyowl.spinygui.core.style.types.transition;

import java.util.EnumMap;
import java.util.Map;

/** Deterministically resolves CSS transition lists for M3's supported properties. */
public final class TransitionDescriptorResolver {
  private TransitionDescriptorResolver() {}
  public static Map<TransitionPropertyName, ResolvedTransitionDescriptor> resolve(TransitionConfiguration configuration) {
    Map<TransitionPropertyName, ResolvedTransitionDescriptor> result = new EnumMap<>(TransitionPropertyName.class);
    if (configuration.properties().stream().anyMatch(TransitionPropertySelection.None.class::isInstance)) return result;
    for (int index = 0; index < configuration.properties().size(); index++) {
      TransitionPropertySelection selection = configuration.properties().get(index);
      TransitionTime duration = configuration.durations().get(index % configuration.durations().size());
      TransitionTime delay = configuration.delays().get(index % configuration.delays().size());
      TransitionTimingFunction timing = configuration.timingFunctions().get(index % configuration.timingFunctions().size());
      if (selection instanceof TransitionPropertySelection.All) {
        for (TransitionPropertyName property : TransitionPropertyName.values()) result.putIfAbsent(property, new ResolvedTransitionDescriptor(property, duration, timing, delay));
      } else if (selection instanceof TransitionPropertySelection.Named named) {
        result.put(named.property(), new ResolvedTransitionDescriptor(named.property(), duration, timing, delay));
      }
    }
    return Map.copyOf(result);
  }
}
