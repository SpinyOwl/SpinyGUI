package com.spinyowl.spinygui.core.style;

/**
 * Style interface that could be used to get/set element's inline style (style attribute)
 */
public interface Style {

    /**
     * Common method. Used to set CSS property.
     *
     * @param property property name.
     * @param value    property value.
     */
    void set(String property, String value);

    /**
     * Common method. Used to get value of specified CSS property.
     *
     * @param property property name.
     * @return property value or null if not set.
     */
    String get(String property);

}
