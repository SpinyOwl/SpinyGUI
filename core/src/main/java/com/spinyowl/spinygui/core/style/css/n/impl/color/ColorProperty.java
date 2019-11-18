package com.spinyowl.spinygui.core.style.css.n.impl.color;

import com.spinyowl.spinygui.core.style.css.n.Property;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorProperty implements Property {
//    public static final ColorProperty INHERIT = new ColorProperty();
//    public static final ColorProperty INITIAL = new ColorProperty(Color.TRANSPARENT);
//    public static final ColorProperty UNSET = new ColorProperty();

//    private final Color value;
//
//    private ColorProperty() {
//        this.value = null;
//    }
//
//    public ColorProperty(Color value) {
//        this.value = value;
//    }

    @Override
    public String getName() {
        return "color";
    }

    /**
     * Defines if property inherited from parent by default.
     *
     * @return true if property inherited from parent by default.
     */
    @Override
    public final boolean inherited() {
        return false;
    }

    /**
     * Defies if property could be changed with css animations.
     *
     * @return if property could be changed with css animations.
     */
    @Override
    public final boolean animatable() {
        return false;
    }

//    /**
//     * Allows to check whether property equals <code>inherit</code> value
//     *
//     * @return <code>true</code>if value is <code>INHERIT</code>,
//     * <code>false</code> otherwise
//     */
//    @Override
//    public final boolean equalsInherit() {
//        return this == INHERIT;
//    }
//
//    /**
//     * Allows to check whether property equals <code>initial</code> value
//     *
//     * @return <code>true</code>if value is <code>INITIAL</code>,
//     * <code>false</code> otherwise
//     */
//    @Override
//    public final boolean equalsInitial() {
//        return this == INITIAL;
//    }
//
//    /**
//     * Allows to check whether property equals <code>unset</code> value
//     *
//     * @return <code>true</code>if value is <code>UNSET</code>,
//     * <code>false</code> otherwise
//     */
//    @Override
//    public final boolean equalsUnset() {
//        return this == UNSET;
//    }
//
//    @Override
//    public Color getValue() {
//        return value;
//    }
//
//    @Override
//    public Property defaultValue() {
//        return new ColorProperty(Color.TRANSPARENT);
//    }
}
