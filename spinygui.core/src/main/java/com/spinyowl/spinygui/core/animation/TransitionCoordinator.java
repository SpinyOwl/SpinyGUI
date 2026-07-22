package com.spinyowl.spinygui.core.animation;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.DISPLAY;

import com.spinyowl.spinygui.core.node.Element;
import com.spinyowl.spinygui.core.node.Frame;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.transition.ResolvedTransitionDescriptor;
import com.spinyowl.spinygui.core.style.types.transition.TransitionDescriptorResolver;
import com.spinyowl.spinygui.core.style.types.transition.TransitionPropertyName;
import com.spinyowl.spinygui.core.time.TimeService;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;

/**
 * Host-owned transition service. Hosts call {@link #tick()} once per frame after style
 * recalculation and before layout/render; E2 may later provide an optional higher-level runtime.
 */
public final class TransitionCoordinator implements StyleChangeListener {
  private final TimeService timeService;
  private final Map<Element, Map<TransitionPropertyName, TransitionTrack<Object>>> tracks = new WeakHashMap<>();
  private boolean clockInitialized;
  public TransitionCoordinator(TimeService timeService) { this.timeService = Objects.requireNonNull(timeService); }
  @Override public void stylesResolved(Element element, Map<String, Object> previous, Map<String, Object> current) {
    if (previous.isEmpty()) { presentTargets(element, current); return; }
    if (hidden(element)) { cancel(element); element.presentationState().reset(); return; }
    Map<TransitionPropertyName, ResolvedTransitionDescriptor> descriptors = TransitionDescriptorResolver.resolve(element.resolvedStyle().transitionConfiguration());
    for (TransitionPropertyName property : TransitionPropertyName.values()) {
      Object oldTarget = previous.get(property.cssName()); Object newTarget = current.get(property.cssName());
      if (Objects.equals(oldTarget, newTarget)) continue;
      ResolvedTransitionDescriptor descriptor = descriptors.get(property);
      var interpolation = descriptor == null || descriptor.duration().seconds() == 0d ? java.util.Optional.<java.util.function.DoubleFunction<Object>>empty() : TransitionInterpolator.between(property, presentedOr(element, property, oldTarget), newTarget);
      if (interpolation.isEmpty()) { remove(element, property); element.presentationState().setValue(property.cssName(), newTarget); }
      else tracks.computeIfAbsent(element, ignored -> new EnumMap<>(TransitionPropertyName.class)).put(property, new TransitionTrack<>(presentedOr(element, property, oldTarget), newTarget, descriptor, now(), interpolation.get()));
    }
  }
  public void tick() {
    double time = now(); if (!clockInitialized) { clockInitialized = true; return; }
    tracks.entrySet().removeIf(entry -> {
      Element element = entry.getKey();
      if (!(element instanceof Frame) && element.parent() == null) {
        element.presentationState().reset();
        return true;
      }
      if (hidden(element)) { element.presentationState().reset(); return true; }
      entry.getValue().entrySet().removeIf(track -> { element.presentationState().setValue(track.getKey().cssName(), track.getValue().valueAt(time)); return track.getValue().completeAt(time); });
      return entry.getValue().isEmpty();
    });
  }
  public void cancel(Element element) { tracks.remove(element); element.presentationState().clearValues(); }
  public void removed(Element element) { cancel(element); }
  public int activeTrackCount() { return tracks.values().stream().mapToInt(Map::size).sum(); }
  private void presentTargets(Element element, Map<String, Object> current) { for (TransitionPropertyName property : TransitionPropertyName.values()) element.presentationState().setValue(property.cssName(), current.get(property.cssName())); }
  private Object presentedOr(Element element, TransitionPropertyName property, Object fallback) { return element.presentationState().value(property.cssName(), fallback); }
  private void remove(Element element, TransitionPropertyName property) { Map<TransitionPropertyName, TransitionTrack<Object>> elementTracks = tracks.get(element); if (elementTracks != null) { elementTracks.remove(property); if (elementTracks.isEmpty()) tracks.remove(element); } }
  private boolean hidden(Element element) {
    for (Element current = element; current != null; current = current.parent()) {
      if (Display.NONE.equals(current.resolvedStyle().get(DISPLAY))) {
        return true;
      }
    }
    return false;
  }
  private double now() { return timeService.currentTime(); }
}
