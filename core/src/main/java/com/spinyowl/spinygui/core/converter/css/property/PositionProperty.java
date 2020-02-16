package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PositionProperty extends Property<Position> {

    public PositionProperty() {
        super(Properties.POSITION, Position.RELATIVE.getName(), INHERITED, ANIMATABLE,
            NodeStyle::setPosition, NodeStyle::getPosition, Position::find, Position::contains);
    }

    /**
     * Returns a set of constant property values allowed to use or an empty set (if there are no
     * static values for property).
     *
     * @return set of constant values or null.
     */
    @Override
    public Set<String> allowedValues() {
        var values = Position.values().stream().map(Position::getName)
            .collect(Collectors.toCollection(HashSet::new));
        values.add(INITIAL);
        values.add(INHERIT);
        return values;
    }
}
