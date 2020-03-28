package com.spinyowl.spinygui.core.converter.css.property.border;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_BOTTOM;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderBottomProperty extends Property<BorderItem> {

    public BorderBottomProperty() {
        super(BORDER_BOTTOM, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, i) -> s.getBorder().setRight(i), s -> s.getBorder().getRight(),
            BorderProperty::extract, BorderProperty::test);
    }

}
