package com.spinyowl.spinygui.core.converter.css.property;

import com.spinyowl.spinygui.core.converter.css.Properties;
import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Color;

public class ColorProperty extends Property<Color> {

    public static final ValueExtractor<Color> extractor = ValueExtractors.of(Color.class);

    public ColorProperty() {
        super(Properties.COLOR, "black", INHERITED, ANIMATABLE,
            NodeStyle::setColor, NodeStyle::getColor,
            extractor::extract, extractor::isValid);
    }
}
