package com.spinyowl.spinygui.core.style.types.flex;

/**
 * Specifies the alignment for selected items inside a flexible container.
 */
public enum AlignSelf {
    /**
     * Default. The element inherits its parent container's align-items property, or "stretch" if it has no parent container.
     */
    AUTO,
    /**
     * The element is positioned to fit the container.
     */
    STRETCH,
    /**
     * The element is positioned at the center of the container.
     */
    CENTER,
    /**
     * The element is positioned at the beginning of the container.
     */
    FLEX_START,
    /**
     * The element is positioned at the end of the container.
     */
    FLEX_END,
    /**
     * The element is positioned at the baseline of the container.
     */
    BASELINE,
    /**
     * Sets this property to its default value.
     */
    INITIAL,
    /**
     * Inherits this property from its parent element..
     */
    INHERIT
}
