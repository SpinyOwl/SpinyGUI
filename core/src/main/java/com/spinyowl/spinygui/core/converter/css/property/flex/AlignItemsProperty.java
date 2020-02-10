package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.node.base.Element;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;

import java.util.Arrays;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_ITEMS;

public class AlignItemsProperty extends Property {

    public AlignItemsProperty() {
        super(ALIGN_ITEMS, "stretch", false, false);
    }

    /**
     * Used to update calculated node style of specified element.
     *
     * @param element element to update calculated style.
     */
    @Override
    protected void updateNodeStyle(Element element) {
        update(element, AlignItems.STRETCH, (s, v) -> s.getFlex().setAlignItems(v), s -> s.getFlex().getAlignItems(), AlignItems::valueOf);
    }

    @Override
    public boolean isValid() {
        return super.isValid() || Arrays.stream(AlignItems.values()).anyMatch(ac -> ac.name().equalsIgnoreCase(value));
    }
}
