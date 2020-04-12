package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_BASIS;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class FlexBasisProperty extends Property<Unit> {

    public static final String AUTO = "auto";
    public static final ValueExtractor<Unit> extractor = ValueExtractors.of(Unit.class);

    public FlexBasisProperty() {
        super(FLEX_BASIS, AUTO, !INHERITED, !ANIMATABLE,
            (s, v) -> s.flex().flexBasis(v), s -> s.flex().flexBasis(),
            extractor::extract, extractor::isValid);
    }

}
