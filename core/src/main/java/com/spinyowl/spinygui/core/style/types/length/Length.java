package com.spinyowl.spinygui.core.style.types.length;

import static com.spinyowl.spinygui.core.style.types.length.LengthType.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.LengthType.PIXEL;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class Length<T extends Number> implements Unit {

  public static final PixelLength ZERO = pixel(0);

  @NonNull private final T value;
  @NonNull private final LengthType<T> type;

  public static class PixelLength extends Length<Integer> {
    public PixelLength(@NonNull Integer value) {
      super(value, PIXEL);
    }
  }

  public static class PercentLength extends Length<Float> {
    public PercentLength(@NonNull Float value) {
      super(value, PERCENT);
    }
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
  public int convert() {
    return type.converter().convert(this);
  }

  /**
   * Used to convert length to pixels.
   *
   * @param baseLength base length (that could be used to calculate relative length).
   * @return calculated length.
   */
  public int convert(float baseLength) {
    return type.converter().convert(this, baseLength);
  }

  @Override
  public String toString() {
    return value + "" + type;
  }
}
