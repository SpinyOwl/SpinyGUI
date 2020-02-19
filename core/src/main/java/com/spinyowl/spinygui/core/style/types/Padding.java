package com.spinyowl.spinygui.core.style.types;

import com.spinyowl.spinygui.core.style.types.length.Length;

public class Padding {

    private Length top;
    private Length bottom;
    private Length right;
    private Length left;

    public Padding() {
    }

    public Padding(Length padding) {
        this.top = this.bottom = this.left = this.right = padding;
    }

    public Padding(Length paddingTopBottom, Length paddingRightLeft) {
        this.top = this.bottom = paddingTopBottom;
        this.left = this.right = paddingRightLeft;
    }

    public Padding(Length paddingTop, Length paddingRightLeft, Length paddingBottom) {
        this.top = paddingTop;
        this.left = this.right = paddingRightLeft;
        this.bottom = paddingBottom;
    }

    public Padding(Length paddingTop, Length paddingRight, Length paddingBottom,
        Length paddingLeft) {
        this.top = paddingTop;
        this.left = paddingLeft;
        this.bottom = paddingBottom;
        this.right = paddingRight;
    }

    public Length getTop() {
        return top;
    }

    public void setTop(Length top) {
        this.top = top;
    }

    public Length getBottom() {
        return bottom;
    }

    public void setBottom(Length bottom) {
        this.bottom = bottom;
    }

    public Length getRight() {
        return right;
    }

    public void setRight(Length right) {
        this.right = right;
    }

    public Length getLeft() {
        return left;
    }

    public void setLeft(Length left) {
        this.left = left;
    }

}
