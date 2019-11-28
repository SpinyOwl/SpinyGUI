package com.spinyowl.spinygui.core.style;

import com.spinyowl.spinygui.core.style.types.*;
import com.spinyowl.spinygui.core.style.types.border.Border;
import com.spinyowl.spinygui.core.style.types.flex.*;
import com.spinyowl.spinygui.core.style.types.length.Length;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class NodeStyle {

    // background
    private final Background background = new Background();
    private final Border border = new Border();
    private final BorderRadius borderRadius = new BorderRadius();

    // FLEX SECTION
    // BACKGROUND properties
    private final Padding padding = new Padding();
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
    // font related properties
    private Color color;
    private Unit marginTop;
    private Unit marginBottom;
    private Unit marginRight;
    private Unit marginLeft;

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

    private Display display = Display.BLOCK;
    private Position position = Position.RELATIVE;
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

    public Padding getPadding() {
        return padding;
    }

    public void setFlexFlow(FlexDirection flexDirection, FlexWrap flexWrap) {
        setFlexDirection(flexDirection);
        setFlexWrap(flexWrap);
    }


    public BorderRadius getBorderRadius() {
        return borderRadius;
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

    public void setMargin(Unit margin) {
        this.marginTop = this.marginBottom = this.marginLeft = this.marginRight = margin;
    }

    public void setMargin(Unit marginTopBottom, Unit marginRightLeft) {
        this.marginTop = this.marginBottom = marginTopBottom;
        this.marginLeft = this.marginRight = marginRightLeft;
    }


    public void setMargin(Unit marginTop, Unit marginRightLeft, Unit marginBottom) {
        this.marginTop = marginTop;
        this.marginLeft = this.marginRight = marginRightLeft;
        this.marginBottom = marginBottom;
    }


    public void setMargin(Unit marginTop, Unit marginRight, Unit marginBottom, Unit marginLeft) {
        this.marginTop = marginTop;
        this.marginLeft = marginLeft;
        this.marginBottom = marginBottom;
        this.marginRight = marginRight;
    }

    public Unit getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(Unit marginTop) {
        this.marginTop = marginTop;
    }

    public Unit getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Unit marginBottom) {
        this.marginBottom = marginBottom;
    }

    public Unit getMarginRight() {
        return marginRight;
    }

    public void setMarginRight(Unit marginRight) {
        this.marginRight = marginRight;
    }

    public Unit getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(Unit marginLeft) {
        this.marginLeft = marginLeft;
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

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        if (color != null) {
            this.color = color;
        }
    }

}
