package com.spinyowl.spinygui.core.converter.css.property.padding;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingBottomProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public PaddingBottomProperty() {
        super(Properties.PADDING_BOTTOM, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getPadding().setBottom(v), s -> s.getPadding().getBottom(),
            extractor::extract, extractor::isValid);
    }
}
