package com.spinyowl.spinygui.core.style.types.border;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderItem {
    private Color color;
    private BorderStyle style;
    private Length width;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public BorderStyle getStyle() {
        return style;
    }

    public void setStyle(BorderStyle style) {
        this.style = style;
    }

    public Length getWidth() {
        return width;
    }

    public void setWidth(Length width) {
        this.width = width;
    }
}
