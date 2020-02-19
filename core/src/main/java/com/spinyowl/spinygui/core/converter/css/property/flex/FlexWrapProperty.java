package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_WRAP;

public class FlexWrapProperty extends Property<FlexWrap> {

    public FlexWrapProperty() {
        super(FLEX_WRAP, "nowrap", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setFlexWrap(v), s -> s.getFlex().getFlexWrap(),
            FlexWrap::find, FlexWrap::contains);
    }

}
