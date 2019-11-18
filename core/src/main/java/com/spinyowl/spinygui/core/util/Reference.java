package com.spinyowl.spinygui.core.util;

import java.util.Collection;
import java.util.Objects;

public final class Reference {
    private Reference() {
    }

    public static <T> boolean contains(Collection<T> collection, T element) {
        Objects.requireNonNull(collection);
        for (T elementOfCollection : collection) {
            if (elementOfCollection == element) return true;
        }
        return false;
    }
}
