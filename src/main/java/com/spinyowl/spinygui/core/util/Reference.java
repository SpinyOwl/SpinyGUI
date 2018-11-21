package com.spinyowl.spinygui.core.util;


public class Reference<T> {
    private final T object;

    private Reference(T object) {
        this.object = object;
    }

    public static <T> Reference<T> of(T object) {
        if (object == null) throw new NullPointerException("Argument could not be null.");
        return new Reference<>(object);
    }

    public T get() {
        return object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Reference<?> reference = (Reference<?>) o;

        return object == reference.object;
    }

    @Override
    public int hashCode() {
        return object.hashCode();
    }
}
