package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.JUSTIFY_CONTENT;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.JustifyContent;
import java.util.Set;
import java.util.stream.Collectors;

public class JustifyContentProperty extends Property<JustifyContent> {

    public JustifyContentProperty() {
        super(JUSTIFY_CONTENT, "flex-start", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setJustifyContent(v), s -> s.getFlex().getJustifyContent(),
            JustifyContent::find, JustifyContent::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return JustifyContent.values().stream().map(JustifyContent::getName)
            .collect(Collectors.toSet());
    }
}
