package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class WhiteSpaceProperty extends Property {

    public WhiteSpaceProperty() {
        super(Properties.WHITE_SPACE, WhiteSpace.NORMAL.getName(), true, false);
    }

    public WhiteSpaceProperty(String value) {
        super(Properties.WHITE_SPACE, WhiteSpace.NORMAL.getName(), true, false, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, WhiteSpace.NORMAL, NodeStyle::setWhiteSpace, NodeStyle::getWhiteSpace, v -> {
            if (WhiteSpace.contains(v)) {
                return WhiteSpace.of(v);
            }
            return null;
        }, false);
    }

    /**
     * Used to check if value is valid or not.
     *
     * @return true if value is valid. By default returns false.
     */
    @Override
    public boolean isValid() {
        return super.isValid() || WhiteSpace.contains(value);
    }

    @Override
    public Set<String> allowedValues() {
        var values = WhiteSpace.values().stream().map(WhiteSpace::getName).collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
