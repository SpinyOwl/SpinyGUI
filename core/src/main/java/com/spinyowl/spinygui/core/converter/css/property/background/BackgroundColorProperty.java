package com.spinyowl.spinygui.core.converter.css.property.background;

import static com.spinyowl.spinygui.core.converter.css.Properties.BACKGROUND_COLOR;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractor;
import com.spinyowl.spinygui.core.converter.css.extractor.ValueExtractors;
import com.spinyowl.spinygui.core.style.types.Color;

public class BackgroundColorProperty extends Property<Color> {

    public static final ValueExtractor<Color> colorExtractor = ValueExtractors.of(Color.class);

    public BackgroundColorProperty() {
        super(BACKGROUND_COLOR, "transparent", !INHERITED, ANIMATABLE,
            (s, c) -> s.getBackground().setColor(c),
            s -> s.getBackground().getColor(),
            colorExtractor::extract,
            colorExtractor::isValid);
    }

}
