package com.spinyowl.spinygui.core.converter.css.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_WIDTH;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.converter.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderWidthProperty extends Property<Border> {

    public static final String THIN = "thin";
    public static final String MEDIUM = "medium";
    public static final String THICK = "thick";

    public static final Length THIN_VALUE = Length.pixel(2);
    public static final Length MEDIUM_VALUE = Length.pixel(4);
    public static final Length THICK_VALUE = Length.pixel(6);

    private static ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BorderWidthProperty() {
        super(BORDER_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
            (s, b) -> s.getBorder().setWidth(b), NodeStyle::getBorder,
            BorderWidthProperty::extract, BorderWidthProperty::test);
    }

    protected static Border extract(String value) {
        Border b = new Border();
        String[] v = value.split("\\s+");
        if (v.length == 1) {
            b.setWidth(extractOne(v[0]));
        } else if (v.length == 2) {
            b.setWidth(extractOne(v[0]), extractOne(v[1]));
        } else if (v.length == 3) {
            b.setWidth(extractOne(v[0]), extractOne(v[1]), extractOne(v[2]));
        } else if (v.length >= 4) {
            b.setWidth(extractOne(v[0]), extractOne(v[1]), extractOne(v[2]), extractOne(v[3]));
        }
        return b;
    }

    public static Length extractOne(String value) {
        Length v = null;
        if (THIN.equalsIgnoreCase(value)) {
            v = THIN_VALUE;
        } else if (MEDIUM.equalsIgnoreCase(value)) {
            v = MEDIUM_VALUE;
        } else if (THICK.equalsIgnoreCase(value)) {
            v = THICK_VALUE;
        } else {
            v = extractor.extract(value);
        }
        return v;
    }

    public static boolean testOne(String borderWidth) {
        return THIN.equalsIgnoreCase(borderWidth) ||
            MEDIUM.equalsIgnoreCase(borderWidth) ||
            THICK.equalsIgnoreCase(borderWidth) ||
            extractor.isValid(borderWidth);
    }

    private static boolean test(String value) {
        return StyleUtils.testOneFourValue(value, BorderWidthProperty::testOne);
    }

}
