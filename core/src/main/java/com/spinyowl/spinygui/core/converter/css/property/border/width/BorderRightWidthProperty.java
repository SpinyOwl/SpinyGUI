package com.spinyowl.spinygui.core.converter.css.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty.MEDIUM;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderRightWidthProperty extends Property<Length> {

    public BorderRightWidthProperty() {
        super(Properties.BORDER_RIGHT_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
            (s, v) -> s.getBorder().getRight().setWidth(v),
            s -> s.getBorder().getRight().getWidth(),
            BorderWidthProperty::extractOne, BorderWidthProperty::testOne);
    }
}
