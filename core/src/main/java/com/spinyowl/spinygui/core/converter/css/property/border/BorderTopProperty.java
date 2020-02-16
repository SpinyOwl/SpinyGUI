package com.spinyowl.spinygui.core.converter.css.property.border;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderTopProperty extends Property<BorderItem> {

    public BorderTopProperty() {
        super(Properties.BORDER_TOP, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, i) -> s.getBorder().setTop(i), s -> s.getBorder().getTop(),
            BorderProperty::extract, BorderProperty::test);
    }
}
