package com.spinyowl.spinygui.core.converter.css.property.border.color;

import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderRightColorProperty extends Property<Color> {

    public static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

    public BorderRightColorProperty() {
        super(Properties.BORDER_RIGHT_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getRight().setColor(c),
            s -> s.getBorder().getRight().getColor(),
            extractor::extract, extractor::isValid);
    }
}
