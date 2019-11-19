package com.spinyowl.spinygui.core.style.css;

public interface ValueExtractor<T> {

    boolean isValid(String value);

    T extract(String value);

}
