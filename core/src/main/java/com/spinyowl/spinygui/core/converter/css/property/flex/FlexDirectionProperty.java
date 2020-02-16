package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_DIRECTION;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import java.util.Set;
import java.util.stream.Collectors;

public class FlexDirectionProperty extends Property<FlexDirection> {

    public FlexDirectionProperty() {
        super(FLEX_DIRECTION, "row", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setFlexDirection(v), s -> s.getFlex().getFlexDirection(),
            FlexDirection::find, FlexDirection::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return FlexDirection.values().stream().map(FlexDirection::getName)
            .collect(Collectors.toSet());
    }

}
