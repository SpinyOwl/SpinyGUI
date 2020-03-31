package com.spinyowl.spinygui.core.converter.css.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_SIZE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class FontSizeProperty extends Property<Length> {

    private static final ValueExtractor<Length> extractor = ValueExtractors.of(Length.class);

    public FontSizeProperty() {
        super(FONT_SIZE, "medium", INHERITED, ANIMATABLE, NodeStyle::setFontSize,
            NodeStyle::getFontSize, extractor::extract, extractor::isValid);
    }

}
