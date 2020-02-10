package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;

import java.util.Arrays;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_DIRECTION;

public class FlexDirectionProperty extends Property {

    public FlexDirectionProperty() {
        super(FLEX_DIRECTION, "row", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, FlexDirection.ROW, (s, v) -> s.getFlex().setFlexDirection(v), s -> s.getFlex().getFlexDirection(), FlexDirection::valueOf);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || Arrays.stream(FlexDirection.values()).anyMatch(ac -> ac.name().equalsIgnoreCase(value));
    }
}
