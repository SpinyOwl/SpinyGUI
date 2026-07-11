package com.spinyowl.spinygui.core.style.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.spinyowl.spinygui.core.style.types.length.Length;
import java.util.List;
import org.junit.jupiter.api.Test;

class TransformTest {

  @Test
  void none_isTheExplicitDefaultTransform() {
    assertInstanceOf(Transform.None.class, Transform.NONE);
    assertEquals("none", Transform.NONE.toString());
  }

  @Test
  void translate_retainsBothAxisLengths() {
    var translate = new Transform.Translate(Length.pixel(12f), Length.percent(0.25f));

    assertEquals(Length.pixel(12f), translate.x());
    assertEquals(Length.percent(0.25f), translate.y());
  }

  @Test
  void scale_retainsBothFactors() {
    var scale = new Transform.Scale(1.5f, 0.75f);

    assertEquals(1.5f, scale.x());
    assertEquals(0.75f, scale.y());
  }

  @Test
  void rotate_retainsDegrees() {
    var rotate = new Transform.Rotate(45f);

    assertEquals(45f, rotate.degrees());
  }

  @Test
  void origin_defaultsToTheCenterOfTheBorderBox() {
    assertEquals(
        new TransformOrigin(Length.percent(0.5f), Length.percent(0.5f)), TransformOrigin.CENTER);
  }

  @Test
  void operationsRejectNonFiniteNumericValues() {
    assertThrows(IllegalArgumentException.class, () -> new Transform.Scale(Float.NaN, 1f));
    assertThrows(IllegalArgumentException.class, () -> new Transform.Rotate(Float.POSITIVE_INFINITY));
    assertThrows(
        IllegalArgumentException.class,
        () -> new Transform.Translate(Length.pixel(Float.NEGATIVE_INFINITY), Length.pixel(0f)));
  }

  @Test
  void originRejectsNonFiniteNumericValues() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new TransformOrigin(Length.percent(Float.NaN), Length.pixel(0f)));
  }

  @Test
  void composition_usesDeclarationOrderForNonCommutativeOperations() {
    var transform =
        TransformComposition.compose(
            List.of(new Transform.Rotate(90f), new Transform.Translate(Length.pixel(10f), Length.ZERO)),
            new TransformOrigin(Length.ZERO, Length.ZERO),
            100f,
            100f);

    assertPoint(transform.apply(0f, 0f), 0f, 10f);
  }

  @Test
  void composition_appliesOperationsAroundTheResolvedOrigin() {
    var transform =
        TransformComposition.compose(
            List.of(new Transform.Scale(2f, 2f)), TransformOrigin.CENTER, 100f, 40f);

    assertPoint(transform.apply(50f, 20f), 50f, 20f);
    assertPoint(transform.apply(0f, 0f), -50f, -20f);
  }

  @Test
  void composition_resolvesPercentagesAgainstTheirBorderBoxAxes() {
    var transform =
        TransformComposition.compose(
            List.of(new Transform.Translate(Length.percent(0.25f), Length.percent(0.5f))),
            new TransformOrigin(Length.ZERO, Length.ZERO),
            200f,
            80f);

    assertPoint(transform.apply(0f, 0f), 50f, 40f);
  }

  @Test
  void composition_resolvesPercentagesToZeroForZeroSizedBoxes() {
    var transform =
        TransformComposition.compose(
            List.of(new Transform.Translate(Length.percent(0.5f), Length.percent(0.5f))),
            TransformOrigin.CENTER,
            0f,
            0f);

    assertPoint(transform.apply(3f, 4f), 3f, 4f);
  }

  @Test
  void inverse_roundTripsPointsThroughAnInvertibleComposedTransform() {
    var transform =
        TransformComposition.compose(
            List.of(
                new Transform.Rotate(30f),
                new Transform.Scale(1.5f, 0.75f),
                new Transform.Translate(Length.pixel(12f), Length.pixel(-8f))),
            TransformOrigin.CENTER,
            120f,
            80f);

    var inverse = transform.inverse();
    var transformedPoint = transform.apply(23f, 17f);

    assertTrue(inverse.isPresent());
    assertPoint(inverse.orElseThrow().apply(transformedPoint.x(), transformedPoint.y()), 23f, 17f);
  }

  @Test
  void inverse_returnsEmptyForZeroScaleTransforms() {
    var transform =
        TransformComposition.compose(
            List.of(new Transform.Scale(0f, 1f)), TransformOrigin.CENTER, 100f, 100f);

    assertFalse(transform.inverse().isPresent());
  }

  private void assertPoint(AffineTransform.Point point, float x, float y) {
    assertEquals(x, point.x(), 0.0001f);
    assertEquals(y, point.y(), 0.0001f);
  }
}
