package com.spinyowl.spinygui.core.converter.css.extractor;

public interface ValueExtractor<T> {

    boolean isValid(String value);

    T extract(String value);

}
