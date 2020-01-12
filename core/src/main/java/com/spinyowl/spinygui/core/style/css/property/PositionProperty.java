package com.spinyowl.spinygui.core.style.css.property;

import com.spinyowl.spinygui.core.node.base.Container;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.css.Properties;
import com.spinyowl.spinygui.core.style.css.Property;
import com.spinyowl.spinygui.core.style.css.util.StyleUtils;
import com.spinyowl.spinygui.core.style.types.Display;
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
        NodeStyle nodeStyle = element.getCalculatedStyle();

        if (value != null) {
            if (INITIAL.equals(value)) {
                nodeStyle.setPosition(Position.RELATIVE);
            } else if (INHERIT.equals(value)) {
                NodeStyle parentStyle = StyleUtils.getParentCalculatedStyle(element);
                if (parentStyle != null) {
                    Position pd = parentStyle.getPosition();
                    nodeStyle.setPosition(pd == null ? Position.RELATIVE : pd);
                } else {
                    nodeStyle.setPosition(Position.RELATIVE);
                }
            } else if (Position.contains(value)) {
                nodeStyle.setPosition(Position.of(value));
            }
        }
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
