package com.spinyowl.spinygui.core.style.types.length;

import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PERCENT_TYPE;
import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PIXEL_TYPE;
import static java.lang.Float.valueOf;
import java.util.Objects;
import java.util.StringJoiner;

public class Length<T extends Number> implements Unit {

  public static final Length<Integer> ZERO = new Length<>(0, LType.of("0", (v, l) -> 0f));

  private final T value;
  private final LType<T> type;

  public Length(T value, LType<T> type) {
    this.value = Objects.requireNonNull(value);
    this.type = Objects.requireNonNull(type);
  }

  public static Length<Integer> pixel(int value) {
    return new Length<>(value, PIXEL_TYPE);
  }

  public static Length<Float> percent(float value) {
    return new Length<>(value, PERCENT_TYPE);
  }

  public T get() {
    return value;
  }

  public LType<T> type() {
    return this.type;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Length.class.getSimpleName() + "[", "]")
        .add("value=" + value)
        .add("type=" + type)
        .toString();
  }


  /**
   * Converts length to pixels.
   *
   * @param <T> type of length value.
   */
  @FunctionalInterface
  public interface Converter<T extends Number> {
    float convert(Length<T> original, float baseLength);
  }

  public static final class LType<T extends Number> {

    public static final LType<Integer> PIXEL_TYPE = LType.of("PIXEL", (l, n) -> valueOf(l.value));
    public static final LType<Float> PERCENT_TYPE = LType.of("PERCENT", (l, n) -> l.value * n);


    private final String name;
    private final Converter<T> converter;

    private LType(String name, Converter<T> converter) {
      this.name = name;
      this.converter = converter;
    }

    public static <E extends Number> LType<E>
    of(String name, Converter<E> converter) {
      return new LType<>(name, converter);
    }

    public String name() {
      return name;
    }

    public Length<T> length(T value) {
      return value == null ? null : new Length<>(value, this);
    }

    public Converter<T> converter() {
      return converter;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", LType.class.getSimpleName() + "[", "]")
          .add("name='" + name + "'")
          .toString();
    }
  }
}
