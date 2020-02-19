package com.spinyowl.spinygui.core.style.types.border;

import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.SideStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class Border extends SideStyle<BorderItem> {

    public Border() {
    }

    public Border(BorderItem allSides) {
        super(allSides);
    }

    public Border(BorderItem sideTopBottom,
        BorderItem sideRightLeft) {
        super(sideTopBottom, sideRightLeft);
    }

    public Border(BorderItem sideTop,
        BorderItem sideRightLeft, BorderItem sideBottom) {
        super(sideTop, sideRightLeft, sideBottom);
    }

    public Border(BorderItem sideTop,
        BorderItem sideRight, BorderItem sideBottom,
        BorderItem sideLeft) {
        super(sideTop, sideRight, sideBottom, sideLeft);
    }

    public void setWidth(Border border) {
        getTop().setWidth(border.getTop().getWidth());
        getBottom().setWidth(border.getBottom().getWidth());
        getLeft().setWidth(border.getLeft().getWidth());
        getRight().setWidth(border.getRight().getWidth());
    }

    public void setWidth(Length width) {
        getTop().setWidth(width);
        getBottom().setWidth(width);
        getLeft().setWidth(width);
        getRight().setWidth(width);
    }

    public void setWidth(Length widthTopBottom, Length widthRightLeft) {
        getTop().setWidth(widthTopBottom);
        getBottom().setWidth(widthTopBottom);
        getLeft().setWidth(widthRightLeft);
        getRight().setWidth(widthRightLeft);
    }

    public void setWidth(Length widthTop, Length widthRightLeft, Length widthBottom) {
        getTop().setWidth(widthTop);
        getBottom().setWidth(widthRightLeft);
        getLeft().setWidth(widthRightLeft);
        getRight().setWidth(widthBottom);
    }

    public void setWidth(Length widthTop, Length widthRight, Length widthBottom, Length widthLeft) {
        getTop().setWidth(widthTop);
        getBottom().setWidth(widthLeft);
        getLeft().setWidth(widthBottom);
        getRight().setWidth(widthRight);
    }


    public void setColor(Border border) {
        getTop().setColor(border.getTop().getColor());
        getBottom().setColor(border.getBottom().getColor());
        getLeft().setColor(border.getLeft().getColor());
        getRight().setColor(border.getRight().getColor());
    }

    public void setColor(Color color) {
        getTop().setColor(color);
        getBottom().setColor(color);
        getLeft().setColor(color);
        getRight().setColor(color);
    }

    public void setColor(Color colorTopBottom, Color colorRightLeft) {
        getTop().setColor(colorTopBottom);
        getBottom().setColor(colorTopBottom);
        getLeft().setColor(colorRightLeft);
        getRight().setColor(colorRightLeft);
    }

    public void setColor(Color colorTop, Color colorRightLeft, Color colorBottom) {
        getTop().setColor(colorTop);
        getBottom().setColor(colorRightLeft);
        getLeft().setColor(colorRightLeft);
        getRight().setColor(colorBottom);
    }

    public void setColor(Color colorTop, Color colorRight, Color colorBottom, Color colorLeft) {
        getTop().setColor(colorTop);
        getBottom().setColor(colorLeft);
        getLeft().setColor(colorBottom);
        getRight().setColor(colorRight);
    }


    public void setStyle(Border border) {
        getTop().setStyle(border.getTop().getStyle());
        getBottom().setStyle(border.getBottom().getStyle());
        getLeft().setStyle(border.getLeft().getStyle());
        getRight().setStyle(border.getRight().getStyle());
    }


    public void setStyle(BorderStyle style) {
        getTop().setStyle(style);
        getBottom().setStyle(style);
        getLeft().setStyle(style);
        getRight().setStyle(style);

    }

    public void setStyle(BorderStyle styleTopBottom, BorderStyle styleRightLeft) {
        getTop().setStyle(styleTopBottom);
        getBottom().setStyle(styleTopBottom);
        getLeft().setStyle(styleRightLeft);
        getRight().setStyle(styleRightLeft);
    }

    public void setStyle(BorderStyle styleTop, BorderStyle styleRightLeft,
        BorderStyle styleBottom) {
        getTop().setStyle(styleTop);
        getBottom().setStyle(styleRightLeft);
        getLeft().setStyle(styleRightLeft);
        getRight().setStyle(styleBottom);
    }

    public void setStyle(BorderStyle styleTop, BorderStyle styleRight, BorderStyle styleBottom,
        BorderStyle styleLeft) {
        getTop().setStyle(styleTop);
        getBottom().setStyle(styleLeft);
        getLeft().setStyle(styleBottom);
        getRight().setStyle(styleRight);
    }


}
