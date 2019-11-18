package com.spinyowl.spinygui.core.style.css.n;

public interface PropertyBuilder<T extends PropertyValue> {

    T createInherit();

    T createInitial();

    T createUnset();

    T createValue(String value);

    T createDefault();

}
