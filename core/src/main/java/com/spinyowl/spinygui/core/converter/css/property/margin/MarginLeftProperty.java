package com.spinyowl.spinygui.core.converter.css.property.margin;

import static com.spinyowl.spinygui.core.converter.css.Properties.MARGIN_LEFT;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginLeftProperty extends Property<Unit> {

    public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

    public MarginLeftProperty() {
        super(MARGIN_LEFT, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getMargin().setLeft(v), s -> s.getMargin().getLeft(),
            extractor::extract, extractor::isValid);
    }
}
