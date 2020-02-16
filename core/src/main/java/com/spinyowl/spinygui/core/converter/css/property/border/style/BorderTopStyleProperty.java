package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Set;
import java.util.stream.Collectors;

public class BorderTopStyleProperty extends Property<BorderStyle> {

    public BorderTopStyleProperty() {
        super(Properties.BORDER_TOP_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getTop().setStyle(c),
            s -> s.getBorder().getTop().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return BorderStyle.values().stream().map(BorderStyle::getName).collect(Collectors.toSet());
    }
}
