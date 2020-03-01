package com.spinyowl.spinygui.core.converter.css.property.position;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class BottomProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public BottomProperty() {
        super(Properties.BOTTOM, "auto", !INHERITED, ANIMATABLE,
            NodeStyle::setBottom, NodeStyle::getBottom,
            extractor::extract, extractor::isValid);
    }
}
