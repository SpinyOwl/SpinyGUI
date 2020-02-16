package com.spinyowl.spinygui.core.converter.css.property.border.style;

import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;
import java.util.Set;
import java.util.stream.Collectors;

public class BorderRightStyleProperty extends Property<BorderStyle> {


    public BorderRightStyleProperty() {
        super(Properties.BORDER_RIGHT_STYLE, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getRight().setStyle(c),
            s -> s.getBorder().getRight().getStyle(),
            BorderStyle::find, BorderStyle::contains);
    }

    @Override
    public Set<String> allowedValues() {
        return BorderStyle.values().stream().map(BorderStyle::getName).collect(Collectors.toSet());
    }
}
