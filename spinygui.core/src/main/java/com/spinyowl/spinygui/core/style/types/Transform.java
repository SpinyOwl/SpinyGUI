package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Objects;

/** A single supported 2D CSS transform operation. */
public sealed interface Transform
    permits Transform.None, Transform.Operations, Transform.Translate, Transform.Scale, Transform.Rotate {

  /** The default transform, which leaves visual coordinates unchanged. */
  Transform NONE = new None();

  /** The {@code none} transform. */
  final class None implements Transform {
    private None() {}

    @Override
    public boolean equals(Object other) {
      return other instanceof None;
    }

    @Override
    public int hashCode() {
      return None.class.hashCode();
    }

    @Override
    public String toString() {
      return "none";
    }
  }

  /** An immutable ordered transform declaration. */
  record Operations(java.util.List<Transform> values) implements Transform {
    public Operations {
      values = java.util.List.copyOf(values);
      if (values.isEmpty() || values.stream().anyMatch(value -> value instanceof None || value instanceof Operations)) {
        throw new IllegalArgumentException("operations must contain supported transform functions");
      }
    }
  }

  /** A two-axis translation. Values may be pixels or percentages. */
  record Translate(Length<?> x, Length<?> y) implements Transform {
    public Translate {
      validateLength(x, "x");
      validateLength(y, "y");
    }
  }

  /** A two-axis scale. */
  record Scale(float x, float y) implements Transform {
    public Scale {
      validateFinite(x, "x");
      validateFinite(y, "y");
    }
  }

  /** A clockwise rotation in degrees. */
  record Rotate(float degrees) implements Transform {
    public Rotate {
      validateFinite(degrees, "degrees");
    }
  }

  private static void validateLength(Length<?> value, String name) {
    Objects.requireNonNull(value, name + " must not be null");
    validateFinite(value.value().doubleValue(), name);
  }

  private static void validateFinite(float value, String name) {
    validateFinite((double) value, name);
  }

  private static void validateFinite(double value, String name) {
    if (!Double.isFinite(value)) {
      throw new IllegalArgumentException(name + " must be finite");
    }
  }
}
