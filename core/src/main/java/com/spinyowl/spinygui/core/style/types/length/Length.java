package com.spinyowl.spinygui.core.style.types.length;

import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.Length.LType.PIXEL;

import java.util.Objects;
import java.util.StringJoiner;

public class Length<T extends Number> implements Unit {

  public static final Length<Integer> ZERO = Length.pixel(0);

  private final T value;
  private final LType<T> type;

  public Length(T value, LType<T> type) {
    Objects.requireNonNull(value);
    Objects.requireNonNull(type);
    this.value = value;
    this.type = type;
  }

  public static Length<Integer> pixel(int value) {
    return new Length<>(value, PIXEL);
  }

  public static Length<Float> percent(float value) {
    return new Length<>(value, PERCENT);
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
  public interface Converter<T extends Number> {

    float convert(Length<T> original, float baseLength);

  }

  public static final class LType<T extends Number> {

    public static final LType<Integer> PIXEL =
      new LType<>("PIXEL", Integer.class, (l, n) -> l.value);

    public static final LType<Float> PERCENT =
      new LType<>("PERCENT", Float.class, (l, n) -> l.value * n);


    private final String name;
    private final Class<T> type;
    private Converter<T> converter;

    public LType(String name, Class<T> type, Converter<T> converter) {
      this.name = name;
      this.type = type;
      this.converter = converter;
    }

    public String name() {
      return name;
    }

    public Class<T> type() {
      return type;
    }

    public Length<T> length(T value) {
      if (value == null) {
        return null;
      }
      return new Length<>(value, this);
    }

    public Converter<T> converter() {
      return converter;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", LType.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("type=" + type)
        .toString();
    }
  }
}
