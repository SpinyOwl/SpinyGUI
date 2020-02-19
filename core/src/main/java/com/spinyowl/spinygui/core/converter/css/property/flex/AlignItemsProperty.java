package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignItems;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_ITEMS;

public class AlignItemsProperty extends Property<AlignItems> {

    public AlignItemsProperty() {
        super(ALIGN_ITEMS, "stretch", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setAlignItems(v), s -> s.getFlex().getAlignItems(),
            AlignItems::find, AlignItems::contains);
    }

}
