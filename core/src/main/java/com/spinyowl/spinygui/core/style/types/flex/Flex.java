package com.spinyowl.spinygui.core.style.types.flex;

import com.spinyowl.spinygui.core.style.types.length.Unit;

public class Flex {

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
    private Unit flexBasis;

    public Flex() {
        this.flexDirection = FlexDirection.ROW;
        this.justifyContent = JustifyContent.FLEX_START;
        this.alignItems = AlignItems.STRETCH;
        this.flexWrap = FlexWrap.NOWRAP;
        this.alignContent = AlignContent.STRETCH;
        this.alignSelf = AlignSelf.AUTO;
    }

    public void setFlex(int flexGrow, int flexShrink, Unit flexBasis) {
        setFlexGrow(flexGrow);
        setFlexShrink(flexShrink);
        setFlexBasis(flexBasis);
    }

    public void setFlexFlow(FlexDirection flexDirection, FlexWrap flexWrap) {
        setFlexDirection(flexDirection);
        setFlexWrap(flexWrap);
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

    public Unit getFlexBasis() {
        return flexBasis;
    }

    public void setFlexBasis(Unit flexBasis) {
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
}
