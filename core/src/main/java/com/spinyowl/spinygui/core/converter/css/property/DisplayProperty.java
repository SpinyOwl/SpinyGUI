package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Display;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class DisplayProperty extends Property<Display> {

    public DisplayProperty() {
        super(Properties.DISPLAY, Display.BLOCK.getName(), INHERITED, ANIMATABLE,
            NodeStyle::setDisplay, NodeStyle::getDisplay, Display::find, Display::contains);
    }

    @Override
    public Set<String> allowedValues() {
        var values = Display.values().stream().map(Display::getName)
            .collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
