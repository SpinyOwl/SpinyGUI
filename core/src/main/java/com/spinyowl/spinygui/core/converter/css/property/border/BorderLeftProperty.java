package com.spinyowl.spinygui.core.converter.css.property.border;

import static com.spinyowl.spinygui.core.converter.css.Properties.BORDER_LEFT;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderItem;

public class BorderLeftProperty extends Property<BorderItem> {

    public BorderLeftProperty() {
        super(BORDER_LEFT, BorderProperty.DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, i) -> s.getBorder().setRight(i), s -> s.getBorder().getRight(),
            BorderProperty::extract, BorderProperty::test);
    }

}
