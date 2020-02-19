package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.AlignContent;

import static com.spinyowl.spinygui.core.converter.css.Properties.ALIGN_CONTENT;

public class AlignContentProperty extends Property<AlignContent> {

    public AlignContentProperty() {
        super(ALIGN_CONTENT, "stretch", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setAlignContent(v), s -> s.getFlex().getAlignContent(),
            AlignContent::find, AlignContent::contains);
    }

}
