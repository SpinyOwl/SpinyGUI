package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.Objects;

/** The two-axis point around which a transform is applied. */
public record TransformOrigin(Length<?> x, Length<?> y) {

  /** The CSS default: the center of the border box. */
  public static final TransformOrigin CENTER =
      new TransformOrigin(Length.percent(0.5f), Length.percent(0.5f));

  public TransformOrigin {
    validateLength(x, "x");
    validateLength(y, "y");
  }

  private static void validateLength(Length<?> value, String name) {
    Objects.requireNonNull(value, name + " must not be null");
    if (!Double.isFinite(value.value().doubleValue())) {
      throw new IllegalArgumentException(name + " must be finite");
    }
  }
}
