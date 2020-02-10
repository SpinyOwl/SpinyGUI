package com.spinyowl.spinygui.core.converter.css.extractor;

import com.spinyowl.spinygui.core.converter.css.ValueExtractor;

public class IntegerExtractor implements ValueExtractor<Integer> {
    @Override
    public boolean isValid(String value) {
        if (value.isEmpty()) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (i == 0 && value.charAt(i) == '-') {
                if (value.length() == 1) {
                    return false;
                } else {
                    continue;
                }
            }
            if (Character.digit(value.charAt(i), 10) < 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public Integer extract(String value) {
        return Integer.parseInt(value);
    }
}
