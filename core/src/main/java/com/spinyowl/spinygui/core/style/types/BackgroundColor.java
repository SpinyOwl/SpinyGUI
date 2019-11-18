package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.css.property.PropertyValue;

public class BackgroundColor implements PropertyValue {

    private final Color value;

    public BackgroundColor(Color value) {
        this.value = value;
    }

    public Color getValue() {
        return value;
    }
}
