package com.spinyowl.spinygui.core.style.css.n;

public interface Property {

    /**
     * Returns property name.
     *
     * @return property name.
     */
    String getName();

    /**
     * Defines if property inherited from parent by default.
     *
     * @return true if property inherited from parent by default.
     */
    boolean inherited();

    /**
     * Defies if property could be changed with css animations.
     *
     * @return if property could be changed with css animations.
     */
    boolean animatable();
}
