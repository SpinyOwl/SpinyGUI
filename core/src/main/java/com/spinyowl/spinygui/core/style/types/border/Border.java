package com.spinyowl.spinygui.core.style.types.border;

public class Border {
    private final BorderItem top = new BorderItem();
    private final BorderItem right = new BorderItem();
    private final BorderItem bottom = new BorderItem();
    private final BorderItem left = new BorderItem();

    public BorderItem getTop() {
        return top;
    }

    public BorderItem getRight() {
        return right;
    }

    public BorderItem getBottom() {
        return bottom;
    }

    public BorderItem getLeft() {
        return left;
    }
}
