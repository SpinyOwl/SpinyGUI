package com.spinyowl.spinygui.core.converter.css.property.border.style;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderStyleProperty extends Property<Border> {

    public static final String DEFAULT_VALUE = "solid";

    public BorderStyleProperty() {
        super(Properties.BORDER_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, b) -> s.getBorder().setStyle(b), NodeStyle::getBorder,
            BorderStyleProperty::extract, BorderStyleProperty::test);
    }

    private static Border extract(String value) {
        String[] v = value.split("\\s+");
        Border border = new Border();
        if (v.length == 1) {
            border.setStyle(x(v[0]));
        } else if (v.length == 2) {
            border.setStyle(x(v[0]), x(v[1]));
        } else if (v.length == 3) {
            border.setStyle(x(v[0]), x(v[1]), x(v[2]));
        } else if (v.length == 4) {
            border.setStyle(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
        }
        return border;
    }

    private static BorderStyle x(String v) {
        return BorderStyle.create(v);
    }

    private static boolean test(String value) {
        return StyleUtils.testOneFourValue(value, BorderStyle::contains);
    }

}
