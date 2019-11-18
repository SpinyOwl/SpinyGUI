package com.spinyowl.spinygui.core.style.types.flex;

/**
 * Specifies the alignment between the items inside a flexible container when the items do not use all available space.
 */
public enum JustifyContent {
    /**
     * Default value. Items are positioned at the beginning of the container.
     */
    FLEX_START,
    /**
     * Items are positioned at the end of the container.
     */
    FLEX_END,
    /**
     * Items are positioned at the center of the container.
     */
    CENTER,
    /**
     * Items are positioned with space between the lines.
     */
    SPACE_BETWEEN,
    /**
     * Items are positioned with space before, between, and after the lines.
     */
    SPACE_AROUND,
    /**
     * Distribute items evenly. Items have equal space around them.
     */
    SPACE_EVENLY,
    /**
     * Sets this property to its default value.
     */
    INITIAL,
    /**
     * Inherits this property from its parent element..
     */
    INHERIT
}
