package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

public class WhiteSpaceProperty extends Property<WhiteSpace> {

    public WhiteSpaceProperty() {
        super(Properties.WHITE_SPACE, WhiteSpace.NORMAL.getName(), INHERITED, !ANIMATABLE,
            NodeStyle::setWhiteSpace, NodeStyle::getWhiteSpace,
            WhiteSpace::find, WhiteSpace::contains);
    }

}
