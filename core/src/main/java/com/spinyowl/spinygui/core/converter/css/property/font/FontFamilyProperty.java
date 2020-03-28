package com.spinyowl.spinygui.core.converter.css.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_FAMILY;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.font.Font;
import com.spinyowl.spinygui.core.style.NodeStyle;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class FontFamilyProperty extends Property<Set<String>> {

    public FontFamilyProperty() {
        super(FONT_FAMILY, "default", INHERITED, !ANIMATABLE,
            NodeStyle::setFontFamilies, NodeStyle::getFontFamilies,
            v -> Arrays.stream(v.split("\\s+")).collect(Collectors.toSet()), Font::hasFont);
    }

}
