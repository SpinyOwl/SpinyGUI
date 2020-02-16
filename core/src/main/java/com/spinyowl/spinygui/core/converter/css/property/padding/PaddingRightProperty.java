package com.spinyowl.spinygui.core.converter.css.property.padding;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class PaddingRightProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public PaddingRightProperty() {
        super(Properties.PADDING_RIGHT, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getPadding().setRight(v), s -> s.getPadding().getRight(),
            extractor::extract, extractor::isValid);
    }
}
