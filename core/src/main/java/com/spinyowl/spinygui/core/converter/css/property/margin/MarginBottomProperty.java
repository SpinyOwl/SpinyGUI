package com.spinyowl.spinygui.core.converter.css.property.margin;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class MarginBottomProperty extends Property<Unit> {

    public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

    public MarginBottomProperty() {
        super(Properties.MARGIN_BOTTOM, "0", !INHERITED, ANIMATABLE,
            (s, v) -> s.getMargin().setBottom(v), s -> s.getMargin().getBottom(),
            extractor::extract, extractor::isValid);
    }
}
