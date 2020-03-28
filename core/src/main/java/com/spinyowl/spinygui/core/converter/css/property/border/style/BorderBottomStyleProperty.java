package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM_STYLE;
import static com.spinyowl.spinygui.core.converter.css.property.border.style.BorderStyleProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderBottomStyleProperty extends Property<BorderStyle> {

    public BorderBottomStyleProperty() {
        super(BORDER_BOTTOM_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getBottom().setStyle(c),
            s -> s.getBorder().getBottom().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

}
