package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderBottomLeftRadiusProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BorderBottomLeftRadiusProperty() {
        super(Properties.BORDER_BOTTOM_LEFT_RADIUS, "0", !INHERITED, ANIMATABLE,
            (s, l) -> s.getBorderRadius().setBottomLeft(l),
            s -> s.getBorderRadius().getBottomLeft(),
            extractor::extract, extractor::isValid);
    }
}
