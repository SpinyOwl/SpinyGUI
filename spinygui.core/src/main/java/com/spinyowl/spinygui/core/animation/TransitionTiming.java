package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.style.types.transition.TransitionTimingFunction;

/** Evaluates M3 timing functions without renderer dependencies. */
final class TransitionTiming {
  private TransitionTiming() {}
  static double apply(TransitionTimingFunction function, double progress) {
    double value = Math.max(0d, Math.min(1d, progress));
    if (function instanceof TransitionTimingFunction.Named named) return switch (named) {
      case LINEAR -> value;
      case EASE -> cubic(0.25d, 0.1d, 0.25d, 1d, value);
      case EASE_IN -> cubic(0.42d, 0d, 1d, 1d, value);
      case EASE_OUT -> cubic(0d, 0d, 0.58d, 1d, value);
      case EASE_IN_OUT -> cubic(0.42d, 0d, 0.58d, 1d, value);
    };
    TransitionTimingFunction.CubicBezier bezier = (TransitionTimingFunction.CubicBezier) function;
    return cubic(bezier.x1(), bezier.y1(), bezier.x2(), bezier.y2(), value);
  }
  private static double cubic(double x1, double y1, double x2, double y2, double x) {
    double low = 0d, high = 1d, parameter = x;
    for (int i = 0; i < 16; i++) { parameter = (low + high) / 2d; if (curve(parameter, x1, x2) < x) low = parameter; else high = parameter; }
    return curve(parameter, y1, y2);
  }
  private static double curve(double t, double p1, double p2) { double inverse = 1d - t; return 3d * inverse * inverse * t * p1 + 3d * inverse * t * t * p2 + t * t * t; }
}
