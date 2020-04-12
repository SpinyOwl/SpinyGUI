package com.spinyowl.spinygui.core.converter.css.property.position;

import static com.spinyowl.spinygui.core.converter.css.Properties.BOTTOM;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class BottomProperty extends Property<Unit> {

    public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

    public BottomProperty() {
        super(BOTTOM, "auto", !INHERITED, ANIMATABLE,
            NodeStyle::bottom, NodeStyle::bottom,
            extractor::extract, extractor::isValid);
    }
}
