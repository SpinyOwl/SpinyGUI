package com.spinyowl.spinygui.core.converter.css.property.margin;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginProperty extends Property<Margin> {

    private static ValueExtractor<Unit> unitValueExtractor = ValueExtractors.of(Unit.class);

    public MarginProperty() {
        super(Properties.MARGIN, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getMargin().set(v), NodeStyle::getMargin,
            MarginProperty::extract, MarginProperty::test);
    }

    private static Margin extract(String value) {
        if (value == null) {
            return null;
        }
        Margin margin = new Margin();
        String[] v = value.split("\\s+");
        switch (v.length) {
            case 1:
                margin.set(x(v[0]));
                break;
            case 2:
                margin.set(x(v[0]), x(v[1]));
                break;
            case 3:
                margin.set(x(v[0]), x(v[1]), x(v[2]));
                break;
            case 4:
            default:
                margin.set(x(v[0]), x(v[1]), x(v[2]), x(v[3]));
                break;
        }
        return margin;
    }

    private static Unit x(String v) {
        return unitValueExtractor.extract(v);
    }

    public static boolean test(String value) {
        return StyleUtils.testOneFourValue(value, unitValueExtractor::isValid);
    }
}
