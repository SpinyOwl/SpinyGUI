package com.spinyowl.spinygui.core.converter.css.property.dimension;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MinWidthProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public MinWidthProperty() {
        super(Properties.MIN_WIDTH, "0px", !INHERITED, ANIMATABLE,
            NodeStyle::setMinWidth, NodeStyle::getMinWidth,
            extractor::extract, extractor::isValid);
    }
}
