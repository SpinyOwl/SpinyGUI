package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Set;
import java.util.stream.Collectors;

public class BorderLeftStyleProperty extends Property<BorderStyle> {


    public BorderLeftStyleProperty() {
        super(Properties.BORDER_LEFT_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getLeft().setStyle(c),
            s -> s.getBorder().getLeft().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return BorderStyle.values().stream().map(BorderStyle::getName).collect(Collectors.toSet());
    }
}
