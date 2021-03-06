package com.spinyowl.spinygui.core.style.types.length;

import static com.spinyowl.spinygui.core.style.types.length.LengthType.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.LengthType.PIXEL;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class Length<T extends Number> implements Unit {

  public static final Length<Integer> ZERO =
      new Length<>(0, LengthType.of("0", Integer.class, (v, l) -> 0));

  @NonNull private final T value;
  @NonNull private final LengthType<T> type;

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

  public static Length<Integer> pixel(int value) {
    return new Length<>(value, PIXEL);
  }

  public static Length<Float> percent(float value) {
    return new Length<>(value, PERCENT);
  }

  @Override
  public String toString() {
    return value + " " + type;
  }
}
