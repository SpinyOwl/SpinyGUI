package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.style.css.Property;

public class ColorProperty extends Property {

    public ColorProperty() {
        super("color", null, true, true);
    }

    public ColorProperty(String value) {
        this();
        setValue(value);
    }
}
