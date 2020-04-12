package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_TOP_STYLE;
import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderTopStyleProperty extends Property<BorderStyle> {

    public BorderTopStyleProperty() {
        super(BORDER_TOP_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.border().top().style(c),
            s -> s.border().top().style(),
            BorderStyle::find, BorderStyle::contains);
    }

}
