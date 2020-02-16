package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.BorderRadius;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderRadiusProperty extends Property<BorderRadius> {

    private static ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BorderRadiusProperty() {
        super(Properties.BORDER_RADIUS, "0", !INHERITED, ANIMATABLE,
            (s, br) -> s.getBorderRadius().set(br), NodeStyle::getBorderRadius,
            BorderRadiusProperty::extract, BorderRadiusProperty::test);
    }

    private static BorderRadius extract(String value) {
        BorderRadius borderRadius = new BorderRadius();
        String[] v = value.split("\\s+");
        switch (v.length) {
            case 1:
                borderRadius.set(x(v[0]));
                break;
            case 2:
                borderRadius.set(x(v[0]), x(v[1]));
                break;
            case 3:
                borderRadius.set(x(v[0]), x(v[1]), x(v[2]));
                break;
            case 4:
                borderRadius.set(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
                break;
            default:
                break;
        }
        return borderRadius;
    }

    private static Length x(String value1) {
        return extractor.extract(value1);
    }

    public static boolean test(String value) {
        return StyleUtils
            .testOneFourValue(value, extractor::isValid);
    }
}
