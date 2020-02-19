package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.style.types.Background;
import com.spinyowl.spinygui.core.style.types.BorderRadius;
import com.spinyowl.spinygui.core.style.types.Color;
import com.spinyowl.spinygui.core.style.types.Display;
import com.spinyowl.spinygui.core.style.types.Margin;
import com.spinyowl.spinygui.core.style.types.Padding;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.flex.Flex;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;
import java.util.Objects;

public class NodeStyle {

    private final Flex flex = new Flex();
    private final Background background = new Background();
    private Border border = new Border();
    private BorderRadius borderRadius = new BorderRadius();
    private Padding padding = new Padding();
    private Margin margin = new Margin();
    private Display display = Display.BLOCK;
    private Position position = Position.RELATIVE;
    private Color color;

    private Unit width;
    private Unit height;

    private Length minWidth;
    private Length minHeight;

    private Length maxWidth;
    private Length maxHeight;

    private Length top;
    private Length bottom;
    private Length right;
    private Length left;

    private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

    public Padding getPadding() {
        return padding;
    }

    public void setPadding(Padding padding) {
        this.padding = Objects.requireNonNull(padding);
    }

    public BorderRadius getBorderRadius() {
        return borderRadius;
    }

    public void setBorderRadius(BorderRadius borderRadius) {
        this.borderRadius = Objects.requireNonNull(borderRadius);
    }

    public Unit getWidth() {
        return width;
    }

    public void setWidth(Unit width) {
        this.width = width;
    }

    public Unit getHeight() {
        return height;
    }

    public void setHeight(Unit height) {
        this.height = height;
    }

    public Length getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(Length minWidth) {
        this.minWidth = minWidth;
    }

    public Length getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(Length minHeight) {
        this.minHeight = minHeight;
    }

    public Length getMaxWidth() {
        return maxWidth;
    }

    public void setMaxWidth(Length maxWidth) {
        this.maxWidth = maxWidth;
    }

    public Length getMaxHeight() {
        return maxHeight;
    }

    public void setMaxHeight(Length maxHeight) {
        this.maxHeight = maxHeight;
    }

    public Display getDisplay() {
        return display;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Border getBorder() {
        return border;
    }

    public void setBorder(Border border) {
        this.border = Objects.requireNonNull(border);
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

    public Background getBackground() {
        return background;
    }

    public WhiteSpace getWhiteSpace() {
        return whiteSpace;
    }

    public void setWhiteSpace(WhiteSpace whiteSpace) {
        this.whiteSpace = whiteSpace;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

    public Margin getMargin() {
        return margin;
    }

    public void setMargin(Margin margin) {
        this.margin = Objects.requireNonNull(margin);
    }

    public Flex getFlex() {
        return flex;
    }
}
