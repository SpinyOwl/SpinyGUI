package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.converter.css.Property;

public class UnsupportedProperty extends Property {
    public UnsupportedProperty(String name) {
        super(name, null, false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        // do nothing because property not supported.
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        // return false because property not supported.
        return false;
    }
}
