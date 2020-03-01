package com.spinyowl.spinygui.core.converter.css.property.border.width;

import static com.spinyowl.spinygui.core.converter.css.property.border.width.BorderWidthProperty.MEDIUM;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderTopWidthProperty extends Property<Length> {

    public BorderTopWidthProperty() {
        super(Properties.BORDER_TOP_WIDTH, MEDIUM, !INHERITED, ANIMATABLE,
            (s, v) -> s.getBorder().getTop().setWidth(v),
            s -> s.getBorder().getTop().getWidth(),
            BorderWidthProperty::extractOne, BorderWidthProperty::testOne);
    }
}