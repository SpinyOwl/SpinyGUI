package com.spinyowl.spinygui.core.style.types.length;

public interface Unit {

    //@formatter:off
    // DECLARATION OF AUTO VALUE.
    Unit AUTO = new Unit() {};
    //@formatter:on

    default boolean isLength() {
        return this instanceof Length;
    }

    default boolean isAuto() {
        return AUTO.equals(this);
    }

    default Length asLength() {
        return (Length) this;
    }
}
