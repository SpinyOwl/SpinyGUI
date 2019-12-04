package com.spinyowl.spinygui.core.style.types.length;

import java.util.Objects;
import java.util.StringJoiner;

import static com.spinyowl.spinygui.core.style.types.length.Length.Type.PERCENT;
import static com.spinyowl.spinygui.core.style.types.length.Length.Type.PIXEL;

public class Length<T> extends Unit {
    private final T value;
    private final Type<T> type;

    public Length(T value, Type<T> type) {
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

    public Type<T> type() {
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
    public interface Converter<T> {
        float convert(Length<T> original, float baseLength);
    }

    public static final class Type<T> {
        public static final Type<Integer> PIXEL = new Type<>("PIXEL", Integer.class, (l, n) -> l.value);
        public static final Type<Float> PERCENT = new Type<>("PERCENT", Float.class, (l, n) -> l.value * n);

        private final String name;
        private final Class<T> type;
        private Converter<T> converter;

        public Type(String name, Class<T> type, Converter<T> converter) {
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
            return new StringJoiner(", ", Type.class.getSimpleName() + "[", "]")
                    .add("name='" + name + "'")
                    .add("type=" + type)
                    .toString();
        }
    }
}
