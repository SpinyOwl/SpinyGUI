package com.spinyowl.spinygui.core.converter.css.property.position;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class TopProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public TopProperty() {
        super(Properties.TOP, "auto", !INHERITED, ANIMATABLE,
            NodeStyle::setTop, NodeStyle::getTop,
            extractor::extract, extractor::isValid);
    }
}
