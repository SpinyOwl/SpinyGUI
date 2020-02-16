package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WhiteSpaceProperty extends Property<WhiteSpace> {

    public WhiteSpaceProperty() {
        super(Properties.WHITE_SPACE, WhiteSpace.NORMAL.getName(), INHERITED, !ANIMATABLE,
            NodeStyle::setWhiteSpace, NodeStyle::getWhiteSpace,
            WhiteSpace::find, WhiteSpace::contains);
    }

    @Override
    public Set<String> allowedValues() {
        var values = WhiteSpace.values().stream().map(WhiteSpace::getName)
            .collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
