package com.spinyowl.spinygui.core.style.types;

import java.util.Optional;

/** An immutable backend-neutral two-dimensional affine transform. */
public record AffineTransform(float a, float b, float c, float d, float tx, float ty) {

  /** The transform that leaves every point unchanged. */
  public static final AffineTransform IDENTITY = new AffineTransform(1f, 0f, 0f, 1f, 0f, 0f);

  /** Determinants at or below this magnitude are considered too unstable to invert for input. */
  private static final float MIN_INVERTIBLE_DETERMINANT = 0.000001f;

  public AffineTransform {
    validateFinite(a, "a");
    validateFinite(b, "b");
    validateFinite(c, "c");
    validateFinite(d, "d");
    validateFinite(tx, "tx");
    validateFinite(ty, "ty");
  }

  /** Creates a translation transform. */
  public static AffineTransform translation(float x, float y) {
    return new AffineTransform(1f, 0f, 0f, 1f, x, y);
  }

  /** Creates a scale transform. */
  public static AffineTransform scale(float x, float y) {
    return new AffineTransform(x, 0f, 0f, y, 0f, 0f);
  }

  /** Creates a clockwise rotation transform in the screen coordinate system. */
  public static AffineTransform rotationDegrees(float degrees) {
    double radians = Math.toRadians(degrees);
    float cosine = (float) Math.cos(radians);
    float sine = (float) Math.sin(radians);
    return new AffineTransform(cosine, sine, -sine, cosine, 0f, 0f);
  }

  /**
   * Multiplies this transform by {@code other}.
   *
   * <p>The resulting transform applies {@code other} first, then this transform.
   */
  public AffineTransform multiply(AffineTransform other) {
    return new AffineTransform(
        a * other.a + c * other.b,
        b * other.a + d * other.b,
        a * other.c + c * other.d,
        b * other.c + d * other.d,
        a * other.tx + c * other.ty + tx,
        b * other.tx + d * other.ty + ty);
  }

  /** Applies this transform to a point. */
  public Point apply(float x, float y) {
    return new Point(a * x + c * y + tx, b * x + d * y + ty);
  }

  /**
   * Returns the inverse transform when its determinant is sufficiently far from zero.
   *
   * <p>Transforms whose determinant has an absolute value at or below {@value
   * #MIN_INVERTIBLE_DETERMINANT}, or whose inverse coefficients are non-finite, are not invertible
   * for pointer coordinate mapping. Consumers must treat that result as non-targetable rather than
   * falling back to layout-space coordinates.
   */
  public Optional<AffineTransform> inverse() {
    float determinant = a * d - b * c;
    if (Math.abs(determinant) <= MIN_INVERTIBLE_DETERMINANT) {
      return Optional.empty();
    }

    float inverseA = d / determinant;
    float inverseB = -b / determinant;
    float inverseC = -c / determinant;
    float inverseD = a / determinant;
    float inverseTx = (c * ty - d * tx) / determinant;
    float inverseTy = (b * tx - a * ty) / determinant;
    if (!Float.isFinite(inverseA)
        || !Float.isFinite(inverseB)
        || !Float.isFinite(inverseC)
        || !Float.isFinite(inverseD)
        || !Float.isFinite(inverseTx)
        || !Float.isFinite(inverseTy)) {
      return Optional.empty();
    }

    return Optional.of(
        new AffineTransform(inverseA, inverseB, inverseC, inverseD, inverseTx, inverseTy));
  }

  /** An immutable two-dimensional point. */
  public record Point(float x, float y) {}

  private static void validateFinite(float value, String name) {
    if (!Float.isFinite(value)) {
      throw new IllegalArgumentException(name + " must be finite");
    }
  }
}
