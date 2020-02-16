package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.property.border.style.BorderStyleProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Set;
import java.util.stream.Collectors;

public class BorderBottomStyleProperty extends Property<BorderStyle> {

    public BorderBottomStyleProperty() {
        super(Properties.BORDER_BOTTOM_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getBottom().setStyle(c),
            s -> s.getBorder().getBottom().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return BorderStyle.values().stream().map(BorderStyle::getName).collect(Collectors.toSet());
    }
}
