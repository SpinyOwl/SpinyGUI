package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.FlexWrap;

import java.util.Arrays;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_WRAP;

public class FlexWrapProperty extends Property {

    public FlexWrapProperty() {
        super(FLEX_WRAP, "nowrap", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, FlexWrap.NOWRAP, (s, v) -> s.getFlex().setFlexWrap(v), s -> s.getFlex().getFlexWrap(), FlexWrap::valueOf);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || Arrays.stream(FlexWrap.values()).anyMatch(ac -> ac.name().equalsIgnoreCase(value));
    }
}
