package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderProperty extends Property<Border> {

    public static final String DEFAULT_VALUE = "medium none transparent";

    private static ValueExtractor<Length> lengthValueExtractor = ValueExtractors.of(Length.class);
    private static ValueExtractor<Color> colorValueExtractor = ValueExtractors.of(Color.class);

    public BorderProperty() {
        super(Properties.BORDER, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            NodeStyle::setBorder, NodeStyle::getBorder,
            BorderProperty::x, BorderProperty::test
        );
    }

    public static boolean test(String value) {
        String[] values = value.split("\\s+");

        if (values.length == 0 || values.length > 3) {
            return false;
        }

        if (values.length == 1) {
            return BorderStyle.contains(values[0]);
        } else if (values.length == 2) {
            return BorderWidthProperty.testOne(values[0])
                && BorderStyle.contains(values[1]) ||
                BorderStyle.contains(values[0]) && colorValueExtractor.isValid(values[1]);
        } else {
            return BorderWidthProperty.testOne(values[0])
                && BorderStyle.contains(values[1]) && colorValueExtractor.isValid(values[2]);
        }
    }

    private static Border x(String value) {
        Border border = new Border();
        BorderItem i = extract(value);
        border.setStyle(i.getStyle());
        border.setColor(i.getColor());
        border.setWidth(i.getWidth());
        return border;
    }

    public static BorderItem extract(String value) {
        BorderItem i = new BorderItem();
        String[] values = value.split("\\s+");
        if (values.length == 1) {
            i.setStyle(BorderStyle.find(values[0]));
        } else if (values.length == 2) {
            if (BorderStyle.contains(values[0])) {
                i.setStyle(BorderStyle.find(values[0]));
                i.setColor(colorValueExtractor.extract(values[1]));
            } else if (BorderStyle.contains(values[1])) {
                i.setWidth(BorderWidthProperty.extractOne(values[0]));
                i.setStyle(BorderStyle.find(values[1]));
            }
        } else if (values.length == 3) {
            i.setWidth(BorderWidthProperty.extractOne(values[0]));
            i.setStyle(BorderStyle.find(values[1]));
            i.setColor(colorValueExtractor.extract(values[2]));
        }
        return i;
    }

}
