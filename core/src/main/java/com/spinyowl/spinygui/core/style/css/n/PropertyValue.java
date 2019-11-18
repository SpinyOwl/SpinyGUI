package com.spinyowl.spinygui.core.style.css.n;

public interface PropertyValue<T> {

    /**
     * Allows to check whether property equals <code>inherit</code> value
     *
     * @return <code>true</code>if value is <code>INHERIT</code>,
     * <code>false</code> otherwise
     */
    boolean equalsInherit();

    /**
     * Allows to check whether property equals <code>initial</code> value
     *
     * @return <code>true</code>if value is <code>INITIAL</code>,
     * <code>false</code> otherwise
     */
    boolean equalsInitial();

    /**
     * Allows to check whether property equals <code>unset</code> value
     *
     * @return <code>true</code>if value is <code>UNSET</code>,
     * <code>false</code> otherwise
     */
    boolean equalsUnset();

    /**
     * Used to get actual value.
     *
     * @return property value.
     */
    T getValue();
}
