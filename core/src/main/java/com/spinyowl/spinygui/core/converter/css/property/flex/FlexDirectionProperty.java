package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_DIRECTION;

public class FlexDirectionProperty extends Property<FlexDirection> {

    public FlexDirectionProperty() {
        super(FLEX_DIRECTION, "row", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setFlexDirection(v), s -> s.getFlex().getFlexDirection(),
            FlexDirection::find, FlexDirection::contains);
    }

}
