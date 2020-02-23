package com.spinyowl.spinygui.core.style.types.flex;

import com.spinyowl.spinygui.core.style.types.length.Unit;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class Flex {

    /**
     * Specifies the direction of the flexible items
     */
    @NonNull
    private FlexDirection flexDirection = FlexDirection.ROW;

    /**
     * Specifies the alignment between the items inside a flexible container when the items do not
     * use all available space.
     */
    @NonNull
    private JustifyContent justifyContent = JustifyContent.FLEX_START;

    /**
     * Specifies the alignment for items inside a flexible container.
     */
    @NonNull
    private AlignItems alignItems = AlignItems.STRETCH;

    /**
     * Specifies whether the flexible items should wrap or not.
     */
    @NonNull
    private FlexWrap flexWrap = FlexWrap.NOWRAP;

    /**
     * Specifies the alignment between the lines inside a flexible container when the items do not
     * use all available space.
     */
    @NonNull
    private AlignContent alignContent = AlignContent.STRETCH;

    /**
     * Specifies the alignment for selected items inside a flexible container.
     */
    @NonNull
    private AlignSelf alignSelf = AlignSelf.AUTO;

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
    @NonNull
    private Unit flexBasis = Unit.AUTO;


    public Flex setFlex(int flexGrow, int flexShrink, Unit flexBasis) {
        setFlexGrow(flexGrow);
        setFlexShrink(flexShrink);
        setFlexBasis(flexBasis);
        return this;
    }

    public Flex setFlexFlow(FlexDirection flexDirection, FlexWrap flexWrap) {
        setFlexDirection(flexDirection);
        setFlexWrap(flexWrap);
        return this;
    }

}
