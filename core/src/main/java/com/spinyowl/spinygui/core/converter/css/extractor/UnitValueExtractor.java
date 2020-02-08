package com.spinyowl.spinygui.core.converter.css.extractor;

import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.types.length.Auto;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class UnitValueExtractor implements ValueExtractor<Unit> {

    public static final String PERCENTAGE_REGEX = "-?(\\d+(\\.\\d*)?|\\.\\d+)%";
    public static final String PIXEL_REGEX      = "-?\\d+px";
    public static final String AUTO_REGEX       = "auto";
    public static final String ZERO_REGEX       = "0+";

    public static Length getLength(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        if (value.matches(PIXEL_REGEX)) {
            String pixelValue = value.substring(0, value.length() - 2);
            return Length.pixel(Integer.parseInt(pixelValue));
        }

        if (value.matches(PERCENTAGE_REGEX)) {
            String percentageValue = value.substring(0, value.length() - 1);
            return Length.percent(Float.parseFloat(percentageValue));
        }

        if (value.matches(ZERO_REGEX)) {
            return Length.pixel(0);
        }

        return null;
    }

    @Override
    public boolean isValid(String value) {
        return value.matches(PERCENTAGE_REGEX) ||
                value.matches(PIXEL_REGEX) ||
                value.matches(AUTO_REGEX) ||
                value.matches(UnitValueExtractor.ZERO_REGEX);
    }

    @Override
    public Unit extract(String value) {
        if (value.matches(AUTO_REGEX)) {
            return Auto.AUTO;
        }

        return getLength(value);
    }
}
