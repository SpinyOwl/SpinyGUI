package com.spinyowl.spinygui.core.converter.css.property.border.style;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

import static com.spinyowl.spinygui.core.converter.css.property.border.style.BorderStyleProperty.DEFAULT_VALUE;

public class BorderBottomStyleProperty extends Property<BorderStyle> {

    public BorderBottomStyleProperty() {
        super(Properties.BORDER_BOTTOM_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getBottom().setStyle(c),
            s -> s.getBorder().getBottom().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

}
