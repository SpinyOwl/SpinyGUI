package com.spinyowl.spinygui.core.converter.css.property.border.color;

import static com.spinyowl.spinygui.core.converter.css.property.border.color.BorderColorProperty.DEFAULT_VALUE;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderBottomColorProperty extends Property<Color> {

    public static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

    public BorderBottomColorProperty() {
        super(Properties.BORDER_BOTTOM_COLOR, DEFAULT_VALUE, !INHERITED, ANIMATABLE,
            (s, c) -> s.getBorder().getBottom().setColor(c),
            s -> s.getBorder().getBottom().getColor(),
            extractor::extract, extractor::isValid);
    }
}
