package com.spinyowl.spinygui.core.converter.css.property.border.radius;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BorderBottomRightRadiusProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BorderBottomRightRadiusProperty() {
        super(Properties.BORDER_BOTTOM_RIGHT_RADIUS, "0", !INHERITED, ANIMATABLE,
            (s, l) -> s.getBorderRadius().setBottomRight(l),
            s -> s.getBorderRadius().getBottomRight(),
            extractor::extract, extractor::isValid);
    }

}
