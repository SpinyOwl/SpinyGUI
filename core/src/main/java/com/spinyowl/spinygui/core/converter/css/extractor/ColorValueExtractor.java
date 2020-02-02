package com.spinyowl.spinygui.core.converter.css.extractor;

import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorValueExtractor implements ValueExtractor<Color> {
    private static final String hexStringRegex = "#([0-9a-f]{3}|[0-9a-f]{4}|[0-9a-f]{6}|[0-9a-f]{8})";

    private static final String rgbFunctionRegex = "rgb\\x28((2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){2}(2([0-4][0-9]|5[0-5])|[0-1]?[0-9]?[0-9])\\x29";
    private static final String rgbaFunctionRegex = "rgba\\x28(([2]([0-4][0-9]|[5][0-5])|[0-1]?[0-9]?[0-9])(\\s*?,\\s*?)){3}(1|0(.\\d+)?)\\x29";

//    private static final String hslFunctionRegex = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}\\x29";
//    private static final String hslaFunctionRegex = "hsl\\x28(3([0-5]\\d|60)|[1-2]\\d\\d|\\d{1,2})((\\s*?,\\s*?)(100|\\d{1,2})%){2}(\\s*?,\\s*?)(1|0(.\\d+)?)\\x29";

    @Override
    public boolean isValid(String value) {
        return value != null &&
                (value.matches(hexStringRegex) ||
                        value.matches(rgbFunctionRegex) ||
                        value.matches(rgbaFunctionRegex) ||
//              value.matches(hslFunctionRegex) ||
//              value.matches(hslaFunctionRegex) ||
                        Color.exists(value));
    }

    @Override
    public Color extract(String value) {
        if (value == null) {
            return null;
        }

        Color color = Color.getColorByName(value);
        if (color != null) {
            return color;
        }

        if (value.matches(hexStringRegex)) {
            return Color.parseHexString(value);
        }
        if (value.matches(rgbFunctionRegex)) {
            return Color.parseRGBAColorString(value.substring(4, value.length() - 1));
        }

        return null;
    }
}
