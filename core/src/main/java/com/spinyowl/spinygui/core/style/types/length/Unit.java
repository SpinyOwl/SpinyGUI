package com.spinyowl.spinygui.core.style.types.length;

public interface Unit {

    default boolean isLength() {
        return this instanceof Length;
    }

    default boolean isAuto() {
        return this instanceof Auto;
    }

    default Length asLength() {
        return (Length) this;
    }
}
