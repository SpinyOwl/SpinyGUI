package com.spinyowl.spinygui.core.style.types.length;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Length<T extends Number> implements Unit {

  @NonNull private final T value;

  @NonNull private final String type;

  public static PixelLength zero() {
    return ZH.I;
  }

  public static PixelLength pixel(int value) {
    return new PixelLength(value);
  }

  public static PercentLength percent(float value) {
    return new PercentLength(value);
  }

  /**
   * Used to convert length to pixels. Base length is 1.
   *
   * @return calculated length.
   */
  public float convert() {
    return convert(1);
  }

  /**
   * Used to convert length to pixels.
   *
   * @param baseLength base length (that could be used to calculate relative length).
   * @return calculated length.
   */
  public float convert(float baseLength) {
    return value.floatValue() * baseLength;
  }

  @Override
  public String toString() {
    return value + "" + type;
  }

  public static class PixelLength extends Length<Integer> {
    public PixelLength(@NonNull Integer value) {
      super(value, "px");
    }

    @Override
    public float convert(float baseLength) {
      return super.value;
    }
  }

  public static class PercentLength extends Length<Float> {
    public PercentLength(@NonNull Float value) {
      super(value, "%");
    }
  }

  private static final class ZH {
    private static final PixelLength I = new PixelLength(0);
  }
}
