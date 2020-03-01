package com.spinyowl.spinygui.core.converter.css.extractor.impl;

import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.style.types.length.Length;

import static com.spinyowl.spinygui.core.converter.css.extractor.impl.UnitValueExtractor.getLength;

public class LengthValueExtractor implements ValueExtractor<Length> {

    @Override
    public boolean isValid(String value) {
        if (value == null) {
            return true;
        }
        return value.matches(
                UnitValueExtractor.PERCENTAGE_REGEX) ||
                value.matches(UnitValueExtractor.PIXEL_REGEX) ||
                value.matches(UnitValueExtractor.ZERO_REGEX);
    }

    @Override
    public Length extract(String value) {
        return getLength(value);
    }
}
