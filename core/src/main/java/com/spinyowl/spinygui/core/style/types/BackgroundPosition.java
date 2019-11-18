package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.css.property.PropertyValue;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BackgroundPosition implements PropertyValue {

    private final Length positionX;
    private final Length positionY;

    public BackgroundPosition(Length positionX, Length positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public Length getPositionX() {
        return positionX;
    }

    public Length getPositionY() {
        return positionY;
    }
}
