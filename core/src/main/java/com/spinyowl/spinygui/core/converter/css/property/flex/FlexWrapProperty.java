package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_WRAP;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;

public class FlexWrapProperty extends Property<FlexWrap> {

    public FlexWrapProperty() {
        super(FLEX_WRAP, "nowrap", !INHERITED, !ANIMATABLE,
            (s, v) -> s.flex().flexWrap(v), s -> s.flex().flexWrap(),
            FlexWrap::find, FlexWrap::contains);
    }

}
