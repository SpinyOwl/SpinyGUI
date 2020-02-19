package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Display;

public class DisplayProperty extends Property<Display> {

    public DisplayProperty() {
        super(Properties.DISPLAY, Display.BLOCK.getName(), INHERITED, ANIMATABLE,
            NodeStyle::setDisplay, NodeStyle::getDisplay, Display::find, Display::contains);
    }

}
