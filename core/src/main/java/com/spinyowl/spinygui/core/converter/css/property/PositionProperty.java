package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Position;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PositionProperty extends Property {

    public PositionProperty() {
        super(Properties.POSITION, Position.RELATIVE.getName(), true, true);
    }

    public PositionProperty(String value) {
        super(Properties.POSITION, Position.RELATIVE.getName(), true, true, value);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, Position.RELATIVE, NodeStyle::setPosition, NodeStyle::getPosition, v -> {
            if (Position.contains(v)) {
                return Position.of(v);
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
        return super.isValid() || Position.contains(value);
    }

    @Override
    public Set<String> allowedValues() {
        var values = Position.values().stream().map(Position::getName).collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
