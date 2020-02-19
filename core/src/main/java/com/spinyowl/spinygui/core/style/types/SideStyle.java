package com.spinyowl.spinygui.core.style.types;

import java.util.Objects;

public abstract class SideStyle<T> {

    private T top;
    private T bottom;
    private T right;
    private T left;

    public SideStyle() {
    }

    public SideStyle(T allSides) {
        this.top = this.bottom = this.left = this.right = Objects.requireNonNull(allSides);
    }

    public SideStyle(T sideTopBottom, T sideRightLeft) {
        this.top = this.bottom = Objects.requireNonNull(sideTopBottom);
        this.left = this.right = Objects.requireNonNull(sideRightLeft);
    }

    public SideStyle(T sideTop, T sideRightLeft, T sideBottom) {
        this.top = Objects.requireNonNull(sideTop);
        this.left = this.right = Objects.requireNonNull(sideRightLeft);
        this.bottom = Objects.requireNonNull(sideBottom);
    }

    public SideStyle(T sideTop, T sideRight, T sideBottom, T sideLeft) {
        this.top = Objects.requireNonNull(sideTop);
        this.left = Objects.requireNonNull(sideLeft);
        this.bottom = Objects.requireNonNull(sideBottom);
        this.right = Objects.requireNonNull(sideRight);
    }

    public T getTop() {
        return top;
    }

    public void setTop(T top) {
        this.top = Objects.requireNonNull(top);
    }

    public T getBottom() {
        return bottom;
    }

    public void setBottom(T bottom) {
        this.bottom = Objects.requireNonNull(bottom);
    }

    public T getRight() {
        return right;
    }

    public void setRight(T right) {
        this.right = Objects.requireNonNull(right);
    }

    public T getLeft() {
        return left;
    }

    public void setLeft(T left) {
        this.left = Objects.requireNonNull(left);
    }
}
