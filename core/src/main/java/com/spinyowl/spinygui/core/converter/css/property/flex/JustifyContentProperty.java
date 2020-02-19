package com.spinyowl.spinygui.core.converter.css.property.flex;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;

import static com.spinyowl.spinygui.core.converter.css.Properties.JUSTIFY_CONTENT;

public class JustifyContentProperty extends Property<JustifyContent> {

    public JustifyContentProperty() {
        super(JUSTIFY_CONTENT, "flex-start", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setJustifyContent(v), s -> s.getFlex().getJustifyContent(),
            JustifyContent::find, JustifyContent::contains);
    }
}
