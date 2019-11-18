package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.style.types.*;
import com.spinyowl.spinygui.core.style.types.flex.*;
import com.spinyowl.spinygui.core.style.types.length.*;
import com.spinyowl.spinygui.core.style.types.border.Border;

public class NodeStyle {

    // background
    private final Background background = new Background();
    private final Padding padding = new Padding();
    private final Margin margin = new Margin();
    private final Border border = new Border();
    private final BorderRadius borderRadius = new BorderRadius();

    // FLEX SECTION
    /**
     * Specifies the direction of the flexible items
     */
    private FlexDirection flexDirection;
    /**
     * Specifies the alignment between the items inside a flexible container when the items do not use all available space.
     */
    private JustifyContent justifyContent;
    /**
     * Specifies the alignment for items inside a flexible container.
     */
    private AlignItems alignItems;
    /**
     * Specifies whether the flexible items should wrap or not.
     */
    private FlexWrap flexWrap;
    /**
     * Specifies the alignment between the lines inside a flexible container when the items do not use all available space.
     */
    private AlignContent alignContent;
    /**
     * Specifies the alignment for selected items inside a flexible container.
     */
    private AlignSelf alignSelf;
    /**
     * A number specifying how much the item will grow relative to the rest of the flexible items.
     */
    private int flexGrow;
    /**
     * A number specifying how much the item will shrink relative to the rest of the flexible items.
     */
    private int flexShrink;
    /**
     * The length of the item. Legal values: a number in px.
     */
    private float flexBasis;

    // BACKGROUND properties
    private Color backgroundColor;

    private Length backgroundPositionX;
    private Length backgroundPositionY;

    private Length paddingTop;
    private Length paddingBottom;
    private Length paddingRight;
    private Length paddingLeft;

    private Length width;
    private Length height;
    private Length minWidth;
    private Length minHeight;
    private Length maxWidth;
    private Length maxHeight;
    private Display display = Display.BLOCK;
    private Position position = Position.RELATIVE;
    private Length top;
    private Length bottom;
    private Length right;
    private Length left;
    private WhiteSpace whiteSpace = WhiteSpace.NORMAL;

    // initialize with default values
    {


        // initialize flex styles.
        this.flexDirection = FlexDirection.ROW;
        this.justifyContent = JustifyContent.FLEX_START;
        this.alignItems = AlignItems.STRETCH;
        this.flexWrap = FlexWrap.NOWRAP;
        this.alignContent = AlignContent.STRETCH;
        this.alignSelf = AlignSelf.AUTO;
    }

    public AlignSelf getAlignSelf() {
        return alignSelf;
    }

    public void setAlignSelf(AlignSelf alignSelf) {
        this.alignSelf = alignSelf;
    }

    public int getFlexGrow() {
        return flexGrow;
    }

    public void setFlexGrow(int flexGrow) {
        this.flexGrow = flexGrow;
    }

    public int getFlexShrink() {
        return flexShrink;
    }

    public void setFlexShrink(int flexShrink) {
        this.flexShrink = flexShrink;
    }

    public float getFlexBasis() {
        return flexBasis;
    }

    public void setFlexBasis(float flexBasis) {
        this.flexBasis = flexBasis;
    }

    public FlexDirection getFlexDirection() {
        return flexDirection;
    }

    public void setFlexDirection(FlexDirection flexDirection) {
        if (flexDirection != null) {
            this.flexDirection = flexDirection;
        }
    }

    public JustifyContent getJustifyContent() {
        return justifyContent;
    }

    public void setJustifyContent(JustifyContent justifyContent) {
        if (justifyContent != null) {
            this.justifyContent = justifyContent;
        }
    }

    public AlignItems getAlignItems() {
        return alignItems;
    }

    public void setAlignItems(AlignItems alignItems) {
        if (alignItems != null) {
            this.alignItems = alignItems;
        }
    }

    public FlexWrap getFlexWrap() {
        return flexWrap;
    }

    public void setFlexWrap(FlexWrap flexWrap) {
        if (flexWrap != null) {
            this.flexWrap = flexWrap;
        }
    }

    public AlignContent getAlignContent() {
        return alignContent;
    }

    public void setAlignContent(AlignContent alignContent) {
        if (alignContent != null) {
            this.alignContent = alignContent;
        }
    }


    public void setFlex(int flexGrow, int flexShrink, float flexBasis) {
        setFlexGrow(flexGrow);
        setFlexShrink(flexShrink);
        setFlexBasis(flexBasis);
    }

    public void setFlexFlow(FlexDirection flexDirection, FlexWrap flexWrap) {
        setFlexDirection(flexDirection);
        setFlexWrap(flexWrap);
    }

    public Length getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(Length paddingTop) {
        this.paddingTop = paddingTop;
    }

    public Length getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(Length paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public Length getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(Length paddingRight) {
        this.paddingRight = paddingRight;
    }

    public Length getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(Length paddingLeft) {
        this.paddingLeft = paddingLeft;
    }


    public BorderRadius getBorderRadius() {
        return borderRadius;
    }

    public Length getWidth() {
        return width;
    }

    public void setWidth(Length width) {
        this.width = width;
    }

    public Length getHeight() {
        return height;
    }

    public void setHeight(Length height) {
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

    public Padding getPadding() {
        return padding;
    }

    public Margin getMargin() {
        return margin;
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
}
