package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_LEFT_STYLE;
import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderLeftStyleProperty extends Property<BorderStyle> {


    public BorderLeftStyleProperty() {
        super(BORDER_LEFT_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getLeft().setStyle(c),
            s -> s.getBorder().getLeft().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

}
