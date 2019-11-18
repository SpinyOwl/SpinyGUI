package com.spinyowl.spinygui.core.style.css.n.impl.color;

import com.spinyowl.spinygui.core.style.css.n.PropertyBuilder;
import com.spinyowl.spinygui.core.style.types.Color;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorPropertyBuilder implements PropertyBuilder<ColorProperty> {

    public static Color parseValue(String value) {
        String hexPattern = "^#([A-Fa-f0-9]{8}|[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        Matcher matcher = Pattern.compile(hexPattern).matcher(value);
        if (matcher.find()) {
            return Color.parseHexString(value.substring(1));
        }

        String rgbExprPattern = "(\\s*\\d+\\s*,){2,3}(\\s*\\d+\\s*)";
        matcher = Pattern.compile(rgbExprPattern).matcher(value);
        if (matcher.find()) {
            return Color.parseColorString(matcher.group());
        }

        return Color.getColorByName(value);
    }

    @Override
    public ColorProperty createInherit() {
        return ColorProperty.INHERIT;
    }

    @Override
    public ColorProperty createInitial() {
        return ColorProperty.INITIAL;
    }

    @Override
    public ColorProperty createUnset() {
        return ColorProperty.UNSET;
    }

    @Override
    public ColorProperty createValue(String value) {
        switch (value) {
            case "INITIAL":
                return ColorProperty.INITIAL;
            case "INHERIT":
                return ColorProperty.INHERIT;
            case "UNSET":
                return ColorProperty.UNSET;
            default:
                return new ColorProperty(parseValue(value));
        }
    }
}
