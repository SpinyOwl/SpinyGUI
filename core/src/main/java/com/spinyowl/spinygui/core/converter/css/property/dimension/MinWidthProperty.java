package com.spinyowl.spinygui.core.converter.css.property.dimension;

import static com.spinyowl.spinygui.core.converter.css.Properties.MIN_WIDTH;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MinWidthProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public MinWidthProperty() {
        super(MIN_WIDTH, "0px", !INHERITED, ANIMATABLE,
            NodeStyle::minWidth, NodeStyle::minWidth,
            extractor::extract, extractor::isValid);
    }
}
