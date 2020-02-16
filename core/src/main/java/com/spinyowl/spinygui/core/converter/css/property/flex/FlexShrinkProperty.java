package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_SHRINK;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;

public class FlexShrinkProperty extends Property<Integer> {

    public static final ValueExtractor<Integer> extractor = ValueExtractors.of(Integer.class);

    public FlexShrinkProperty() {
        super(FLEX_SHRINK, "0", !INHERITED, !ANIMATABLE,
            (s, v) -> s.getFlex().setFlexShrink(v), s -> s.getFlex().getFlexShrink(),
            extractor::extract, extractor::isValid);
    }
}
