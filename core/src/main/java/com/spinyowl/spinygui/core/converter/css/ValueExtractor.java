package com.spinyowl.spinygui.core.converter.css;

public interface ValueExtractor<T> {

    boolean isValid(String value);

    T extract(String value);

}
