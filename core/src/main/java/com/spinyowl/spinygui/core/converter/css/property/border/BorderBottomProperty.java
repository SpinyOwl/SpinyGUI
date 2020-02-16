package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderBottomProperty extends Property<BorderItem> {

    public BorderBottomProperty() {
        super(Properties.BORDER_BOTTOM, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, i) -> s.getBorder().setRight(i), s -> s.getBorder().getRight(),
            BorderProperty::extract, BorderProperty::test);
    }

}
