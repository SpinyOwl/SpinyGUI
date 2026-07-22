package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.style.types.transition.ResolvedTransitionDescriptor;
import java.util.Objects;
import java.util.function.DoubleFunction;

/** Generic, retargetable transition state owned by {@link TransitionCoordinator}. */
public final class TransitionTrack<T> {
  private final T source;
  private final T target;
  private final ResolvedTransitionDescriptor descriptor;
  private final double startTime;
  private final DoubleFunction<T> interpolation;
  public TransitionTrack(T source, T target, ResolvedTransitionDescriptor descriptor, double startTime, DoubleFunction<T> interpolation) {
    this.source = Objects.requireNonNull(source); this.target = Objects.requireNonNull(target);
    this.descriptor = Objects.requireNonNull(descriptor); this.startTime = startTime; this.interpolation = Objects.requireNonNull(interpolation);
  }
  public T valueAt(double time) {
    double elapsed = time - startTime - descriptor.delay().seconds();
    if (elapsed <= 0d) return source;
    if (descriptor.duration().seconds() == 0d) return target;
    return interpolation.apply(TransitionTiming.apply(descriptor.timingFunction(), Math.min(1d, elapsed / descriptor.duration().seconds())));
  }
  public boolean completeAt(double time) { return time >= startTime + descriptor.delay().seconds() + descriptor.duration().seconds(); }
  public T target() { return target; }
}
