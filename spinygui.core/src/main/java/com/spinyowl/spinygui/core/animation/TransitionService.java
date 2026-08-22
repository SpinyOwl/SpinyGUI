package com.spinyowl.spinygui.core.animation;

/** Host-owned transition phase. */
@FunctionalInterface
public interface TransitionService {
  TransitionImpact tick();
}
