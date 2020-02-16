package com.spinyowl.spinygui.core.style.types.border;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class Border {

    private final BorderItem top = new BorderItem();
    private final BorderItem right = new BorderItem();
    private final BorderItem bottom = new BorderItem();
    private final BorderItem left = new BorderItem();

    public BorderItem getTop() {
        return top;
    }

    public void setTop(BorderItem top) {
        this.top.setWidth(top.getWidth());
        this.top.setStyle(top.getStyle());
        this.top.setColor(top.getColor());
    }

    public BorderItem getRight() {
        return right;
    }

    public void setRight(BorderItem right) {
        this.right.setWidth(right.getWidth());
        this.right.setStyle(right.getStyle());
        this.right.setColor(right.getColor());
    }

    public BorderItem getBottom() {
        return bottom;
    }

    public void setBottom(BorderItem bottom) {
        this.bottom.setWidth(bottom.getWidth());
        this.bottom.setStyle(bottom.getStyle());
        this.bottom.setColor(bottom.getColor());
    }

    public BorderItem getLeft() {
        return left;
    }

    public void setLeft(BorderItem left) {
        this.left.setWidth(left.getWidth());
        this.left.setStyle(left.getStyle());
        this.left.setColor(left.getColor());
    }


    public void setWidth(Border border) {
        this.top.setWidth(border.getTop().getWidth());
        this.bottom.setWidth(border.getBottom().getWidth());
        this.left.setWidth(border.getLeft().getWidth());
        this.right.setWidth(border.getRight().getWidth());
    }

    public void setWidth(Length width) {
        this.top.setWidth(width);
        this.bottom.setWidth(width);
        this.left.setWidth(width);
        this.right.setWidth(width);
    }

    public void setWidth(Length widthTopBottom, Length widthRightLeft) {
        this.top.setWidth(widthTopBottom);
        this.bottom.setWidth(widthTopBottom);
        this.left.setWidth(widthRightLeft);
        this.right.setWidth(widthRightLeft);
    }

    public void setWidth(Length widthTop, Length widthRightLeft, Length widthBottom) {
        this.top.setWidth(widthTop);
        this.left.setWidth(widthRightLeft);
        this.right.setWidth(widthRightLeft);
        this.bottom.setWidth(widthBottom);
    }

    public void setWidth(Length widthTop, Length widthRight, Length widthBottom, Length widthLeft) {
        this.top.setWidth(widthTop);
        this.left.setWidth(widthLeft);
        this.bottom.setWidth(widthBottom);
        this.right.setWidth(widthRight);
    }


    public void setColor(Border border) {
        this.top.setColor(border.getTop().getColor());
        this.bottom.setColor(border.getBottom().getColor());
        this.left.setColor(border.getLeft().getColor());
        this.right.setColor(border.getRight().getColor());
    }

    public void setColor(Color color) {
        this.top.setColor(color);
        this.bottom.setColor(color);
        this.left.setColor(color);
        this.right.setColor(color);
    }

    public void setColor(Color colorTopBottom, Color colorRightLeft) {
        this.top.setColor(colorTopBottom);
        this.bottom.setColor(colorTopBottom);
        this.left.setColor(colorRightLeft);
        this.right.setColor(colorRightLeft);
    }

    public void setColor(Color colorTop, Color colorRightLeft, Color colorBottom) {
        this.top.setColor(colorTop);
        this.left.setColor(colorRightLeft);
        this.right.setColor(colorRightLeft);
        this.bottom.setColor(colorBottom);
    }

    public void setColor(Color colorTop, Color colorRight, Color colorBottom, Color colorLeft) {
        this.top.setColor(colorTop);
        this.left.setColor(colorLeft);
        this.bottom.setColor(colorBottom);
        this.right.setColor(colorRight);
    }


    public void setStyle(Border border) {
        this.top.setStyle(border.getTop().getStyle());
        this.bottom.setStyle(border.getBottom().getStyle());
        this.left.setStyle(border.getLeft().getStyle());
        this.right.setStyle(border.getRight().getStyle());
    }


    public void setStyle(BorderStyle style) {
        this.top.setStyle(style);
        this.bottom.setStyle(style);
        this.left.setStyle(style);
        this.right.setStyle(style);

    }

    public void setStyle(BorderStyle styleTopBottom, BorderStyle styleRightLeft) {
        this.top.setStyle(styleTopBottom);
        this.bottom.setStyle(styleTopBottom);
        this.left.setStyle(styleRightLeft);
        this.right.setStyle(styleRightLeft);
    }

    public void setStyle(BorderStyle styleTop, BorderStyle styleRightLeft,
        BorderStyle styleBottom) {
        this.top.setStyle(styleTop);
        this.left.setStyle(styleRightLeft);
        this.right.setStyle(styleRightLeft);
        this.bottom.setStyle(styleBottom);
    }

    public void setStyle(BorderStyle styleTop, BorderStyle styleRight, BorderStyle styleBottom,
        BorderStyle styleLeft) {
        this.top.setStyle(styleTop);
        this.left.setStyle(styleLeft);
        this.bottom.setStyle(styleBottom);
        this.right.setStyle(styleRight);
    }


}
