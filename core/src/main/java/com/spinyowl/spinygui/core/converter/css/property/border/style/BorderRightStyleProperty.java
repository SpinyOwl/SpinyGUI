package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_RIGHT_STYLE;
import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderRightStyleProperty extends Property<BorderStyle> {


    public BorderRightStyleProperty() {
        super(BORDER_RIGHT_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getRight().setStyle(c),
            s -> s.getBorder().getRight().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }
}
