package com.spinyowl.spinygui.core.animation;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Transform;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.transition.TransitionPropertyName;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.DoubleFunction;

/** Closed M3 interpolation registry. Unsupported or incompatible pairs are immediate. */
public final class TransitionInterpolator {
  private TransitionInterpolator() {}
  public static Optional<DoubleFunction<Object>> between(TransitionPropertyName property, Object source, Object target) {
    if (property == TransitionPropertyName.OPACITY && source instanceof Number from && target instanceof Number to)
      return Optional.of(progress -> from.floatValue() + (to.floatValue() - from.floatValue()) * (float) progress);
    if (isColor(property) && source instanceof Color from && target instanceof Color to)
      return Optional.of(progress -> new Color((float) (from.r() + (to.r() - from.r()) * progress), (float) (from.g() + (to.g() - from.g()) * progress), (float) (from.b() + (to.b() - from.b()) * progress), (float) (from.a() + (to.a() - from.a()) * progress)));
    if (property == TransitionPropertyName.TRANSFORM && source instanceof Transform from && target instanceof Transform to)
      return transform(from, to);
    return Optional.empty();
  }
  private static boolean isColor(TransitionPropertyName property) { return property != TransitionPropertyName.OPACITY && property != TransitionPropertyName.TRANSFORM; }
  private static Optional<DoubleFunction<Object>> transform(Transform from, Transform to) {
    if (from instanceof Transform.None && to instanceof Transform.Operations operations) {
      return operations(identityOperations(operations), operations)
          .map(interpolation -> progress -> interpolation.apply(progress));
    }
    if (from instanceof Transform.Operations operations && to instanceof Transform.None) {
      return operations(operations, identityOperations(operations))
          .map(interpolation -> progress -> interpolation.apply(progress));
    }
    if (from instanceof Transform.Operations a && to instanceof Transform.Operations b) {
      return operations(a, b).map(interpolation -> progress -> interpolation.apply(progress));
    }
    if (from instanceof Transform.Scale a && to instanceof Transform.Scale b) return Optional.of(p -> new Transform.Scale((float) (a.x() + (b.x() - a.x()) * p), (float) (a.y() + (b.y() - a.y()) * p)));
    if (from instanceof Transform.Rotate a && to instanceof Transform.Rotate b) return Optional.of(p -> new Transform.Rotate((float) (a.degrees() + (b.degrees() - a.degrees()) * p)));
    if (from instanceof Transform.Translate a && to instanceof Transform.Translate b && a.x().type().equals(b.x().type()) && a.y().type().equals(b.y().type())) return Optional.of(p -> new Transform.Translate(interpolateLength(a.x(), b.x(), p), interpolateLength(a.y(), b.y(), p)));
    return Optional.empty();
  }

  private static Optional<DoubleFunction<Transform.Operations>> operations(
      Transform.Operations from, Transform.Operations to) {
    if (from.values().size() != to.values().size()) {
      return Optional.empty();
    }
    List<DoubleFunction<Transform>> interpolations = new ArrayList<>(from.values().size());
    for (int index = 0; index < from.values().size(); index++) {
      Optional<DoubleFunction<Object>> interpolation = transform(from.values().get(index), to.values().get(index));
      if (interpolation.isEmpty()) {
        return Optional.empty();
      }
      interpolations.add(progress -> (Transform) interpolation.get().apply(progress));
    }
    return Optional.of(progress -> new Transform.Operations(interpolations.stream().map(interpolation -> interpolation.apply(progress)).toList()));
  }

  private static Transform.Operations identityOperations(Transform.Operations operations) {
    return new Transform.Operations(
        operations.values().stream().map(TransitionInterpolator::identity).toList());
  }

  private static Transform identity(Transform operation) {
    return switch (operation) {
      case Transform.Translate translate ->
          new Transform.Translate(zero(translate.x()), zero(translate.y()));
      case Transform.Scale ignored -> new Transform.Scale(1f, 1f);
      case Transform.Rotate ignored -> new Transform.Rotate(0f);
      case Transform.None ignored -> throw new IllegalArgumentException("none is not an operation");
      case Transform.Operations ignored ->
          throw new IllegalArgumentException("nested transform operations are not supported");
    };
  }

  private static Length<?> zero(Length<?> length) {
    return "%".equals(length.type()) ? Length.percent(0f) : Length.pixel(0f);
  }

  private static Length<?> interpolateLength(Length<?> from, Length<?> to, double progress) {
    float value = (float) (from.convert() + (to.convert() - from.convert()) * progress);
    return "%".equals(from.type()) ? Length.percent(value) : Length.pixel(value);
  }
}
