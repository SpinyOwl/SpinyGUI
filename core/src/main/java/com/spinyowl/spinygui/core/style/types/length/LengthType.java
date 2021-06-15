package com.spinyowl.spinygui.core.style.types.length;

import static java.lang.Float.valueOf;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class LengthType<T extends Number> {

  public static final LengthType<Integer> PIXEL =
      of("PIXEL", Integer.class, (length, baseValue) -> valueOf(length.value()));
  public static final LengthType<Float> PERCENT =
      of("PERCENT", Float.class, (length, baseValue) -> length.value() * baseValue);

  @NonNull private final String name;
  @NonNull private Class<T> type;

  @NonNull
  @Getter(AccessLevel.NONE)
  private final LengthConverter<T> converter;

  public static <E extends Number> LengthType<E> of(
      String name, Class<E> type, LengthConverter<E> converter) {
    return new LengthType<>(name, type, converter);
  }

  public Length<T> create(@NonNull T value) {
    return new Length<>(value, this);
  }
}
