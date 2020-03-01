package com.spinyowl.spinygui.core.converter.css.property.dimension;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class HeightProperty extends Property<Unit> {

    public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

    public HeightProperty() {
        super(Properties.HEIGHT, "auto", !INHERITED, ANIMATABLE,
            NodeStyle::setHeight, NodeStyle::getHeight,
            extractor::extract, extractor::isValid);
    }
}
