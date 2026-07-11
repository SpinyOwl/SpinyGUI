package com.spinyowl.spinygui.core.style.types;

import java.util.List;
import java.util.Objects;

/** Resolves supported CSS transform operations into one affine transform. */
public final class TransformComposition {

  private TransformComposition() {}

  /**
   * Resolves a transform list around its origin within a border box.
   *
   * <p>Operations are multiplied in declaration order. For example, {@code rotate(...) translate(...)}
   * creates {@code rotate * translate}, so the translation applies to a point before the rotation.
   * Percentage translations and origin components resolve against their matching border-box axis.
   */
  public static AffineTransform compose(
      List<? extends Transform> operations,
      TransformOrigin origin,
      float borderBoxWidth,
      float borderBoxHeight) {
    validateBoxSize(borderBoxWidth, "borderBoxWidth");
    validateBoxSize(borderBoxHeight, "borderBoxHeight");
    Objects.requireNonNull(operations, "operations must not be null");
    Objects.requireNonNull(origin, "origin must not be null");

    AffineTransform operationsTransform = AffineTransform.IDENTITY;
    for (Transform operation : operations) {
      operationsTransform =
          operationsTransform.multiply(toAffine(operation, borderBoxWidth, borderBoxHeight));
    }

    float originX = origin.x().convert(borderBoxWidth);
    float originY = origin.y().convert(borderBoxHeight);
    return AffineTransform.translation(originX, originY)
        .multiply(operationsTransform)
        .multiply(AffineTransform.translation(-originX, -originY));
  }

  private static AffineTransform toAffine(
      Transform operation, float borderBoxWidth, float borderBoxHeight) {
    Objects.requireNonNull(operation, "transform operation must not be null");
    return switch (operation) {
      case Transform.None ignored -> AffineTransform.IDENTITY;
      case Transform.Operations operations -> composeOperations(operations.values(), borderBoxWidth, borderBoxHeight);
      case Transform.Translate translate ->
          AffineTransform.translation(
              translate.x().convert(borderBoxWidth), translate.y().convert(borderBoxHeight));
      case Transform.Scale scale -> AffineTransform.scale(scale.x(), scale.y());
      case Transform.Rotate rotate -> AffineTransform.rotationDegrees(rotate.degrees());
    };
  }

  private static AffineTransform composeOperations(
      List<Transform> operations, float borderBoxWidth, float borderBoxHeight) {
    AffineTransform result = AffineTransform.IDENTITY;
    for (Transform operation : operations) {
      result = result.multiply(toAffine(operation, borderBoxWidth, borderBoxHeight));
    }
    return result;
  }

  private static void validateBoxSize(float value, String name) {
    if (!Float.isFinite(value) || value < 0f) {
      throw new IllegalArgumentException(name + " must be finite and non-negative");
    }
  }
}
