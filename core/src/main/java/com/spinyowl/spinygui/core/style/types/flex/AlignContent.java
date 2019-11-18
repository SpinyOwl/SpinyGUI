package com.spinyowl.spinygui.core.style.types.flex;

/**
 * Specifies the alignment between the lines inside a flexible container when the items do not use all available space.
 */
public enum AlignContent {
    /**
     * Default value. Lines stretch to take up the remaining space.
     */
    STRETCH,
    /**
     * Lines are packed toward the center of the flex container.
     */
    CENTER,
    /**
     * Lines are packed toward the start of the flex container.
     */
    FLEX_START,
    /**
     * Lines are packed toward the end of the flex container.
     */
    FLEX_END,
    /**
     * Lines are evenly distributed in the flex container.
     */
    SPACE_BETWEEN,
    /**
     * Lines are evenly distributed in the flex container, with half-size spaces on either end.
     */
    SPACE_AROUND,
    /**
     * Sets this property to its default value.
     */
    INITIAL,
    /**
     * Inherits this property from its parent element..
     */
    INHERIT
}
