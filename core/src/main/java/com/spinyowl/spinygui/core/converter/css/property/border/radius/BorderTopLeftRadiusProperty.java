package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderTopLeftRadiusProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BorderTopLeftRadiusProperty() {
        super(Properties.BORDER_TOP_LEFT_RADIUS, "0", !INHERITED, ANIMATABLE,
            (s, l) -> s.getBorderRadius().setTopLeft(l),
            s -> s.getBorderRadius().getTopLeft(),
            extractor::extract, extractor::isValid);
    }
}
