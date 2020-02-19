package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignSelf;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_SELF;

public class AlignSelfProperty extends Property<AlignSelf> {

    public AlignSelfProperty() {
        super(ALIGN_SELF, "auto", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setAlignSelf(v), s -> s.getFlex().getAlignSelf(),
            AlignSelf::find, AlignSelf::contains);
    }

}
