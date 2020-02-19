package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Unit;

public class Margin {

    private Unit top;
    private Unit bottom;
    private Unit right;
    private Unit left;

    public Margin() {
    }

    public Margin(Unit margin) {
        this.top = this.left = this.bottom = this.right = margin;
    }

    public Margin(Unit marginTopBottom, Unit marginRightLeft) {
        this.top = this.bottom = marginTopBottom;
        this.left = this.right = marginRightLeft;
    }

    public Margin(Unit marginTop, Unit marginRightLeft, Unit marginBottom) {
        this.top = marginTop;
        this.left = this.right = marginRightLeft;
        this.bottom = marginBottom;
    }

    public Margin(Unit marginTop, Unit marginRight, Unit marginBottom, Unit marginLeft) {
        this.top = marginTop;
        this.left = marginLeft;
        this.bottom = marginBottom;
        this.right = marginRight;
    }

    public Unit getTop() {
        return top;
    }

    public void setTop(Unit top) {
        this.top = top;
    }

    public Unit getBottom() {
        return bottom;
    }

    public void setBottom(Unit bottom) {
        this.bottom = bottom;
    }

    public Unit getRight() {
        return right;
    }

    public void setRight(Unit right) {
        this.right = right;
    }

    public Unit getLeft() {
        return left;
    }

    public void setLeft(Unit left) {
        this.left = left;
    }

}
