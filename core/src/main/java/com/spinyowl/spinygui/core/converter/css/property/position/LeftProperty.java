package com.spinyowl.spinygui.core.converter.css.property.position;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class LeftProperty extends Property<Length> {

    public static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public LeftProperty() {
        super(Properties.LEFT, "auto", !INHERITED, ANIMATABLE,
            NodeStyle::setLeft, NodeStyle::getLeft,
            extractor::extract, extractor::isValid);
    }
}
