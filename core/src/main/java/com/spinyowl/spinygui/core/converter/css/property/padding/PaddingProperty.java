package com.spinyowl.spinygui.core.converter.css.property.padding;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingProperty extends Property<Padding> {

    private static ValueExtractor<Length> lengthValueExtractor = ValueExtractors.of(Length.class);

    public PaddingProperty() {
        super(Properties.PADDING, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getPadding().set(v), NodeStyle::getPadding,
            PaddingProperty::extract, PaddingProperty::test);
    }

    public static Padding extract(String value) {
        if (value == null) {
            return null;
        }
        Padding padding = new Padding();
        String[] values = value.split("\\s+");
        switch (values.length) {
            case 0:
                return null;
            case 1:
                padding.set(
                    lengthValueExtractor.extract(values[0])
                );
                break;
            case 2:
                padding.set(
                    lengthValueExtractor.extract(values[0]),
                    lengthValueExtractor.extract(values[1])
                );
                break;
            case 3:
                padding.set(
                    lengthValueExtractor.extract(values[0]),
                    lengthValueExtractor.extract(values[1]),
                    lengthValueExtractor.extract(values[2])
                );
                break;
            case 4:
            default:
                padding.set(
                    lengthValueExtractor.extract(values[0]),
                    lengthValueExtractor.extract(values[1]),
                    lengthValueExtractor.extract(values[2]),
                    lengthValueExtractor.extract(values[3])
                );
                break;
        }
        return padding;
    }

    public static boolean test(String value) {
        return StyleUtils.testOneFourValue(value, lengthValueExtractor::isValid);
    }
}
