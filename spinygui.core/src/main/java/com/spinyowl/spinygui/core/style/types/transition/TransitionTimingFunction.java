package com.spinyowl.spinygui.core.style.types.transition;

/** A bounded timing function supported by the transition runtime. */
public sealed interface TransitionTimingFunction
    permits TransitionTimingFunction.Named, TransitionTimingFunction.CubicBezier {
  Named LINEAR = Named.LINEAR;
  Named EASE = Named.EASE;

  enum Named implements TransitionTimingFunction {
    LINEAR,
    EASE,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT
  }

  record CubicBezier(double x1, double y1, double x2, double y2) implements TransitionTimingFunction {
    public CubicBezier {
      if (!Double.isFinite(x1) || !Double.isFinite(y1) || !Double.isFinite(x2) || !Double.isFinite(y2)
          || x1 < 0d || x1 > 1d || x2 < 0d || x2 > 1d) {
        throw new IllegalArgumentException("cubic-bezier requires finite values and x coordinates in [0, 1]");
      }
    }
  }
}
