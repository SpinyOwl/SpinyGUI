package com.spinyowl.spinygui.core.converter.css.property.dimension;

import static com.spinyowl.spinygui.core.converter.css.Properties.MAX_HEIGHT;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class MaxHeightProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public MaxHeightProperty() {
        super(MAX_HEIGHT, "none", !INHERITED, ANIMATABLE,
            NodeStyle::setMaxHeight, NodeStyle::getMaxHeight,
            value -> "none".equalsIgnoreCase(value) ?
                Length.pixel(Integer.MAX_VALUE)
                : extractor.extract(value),
            extractor::isValid);
    }
}
