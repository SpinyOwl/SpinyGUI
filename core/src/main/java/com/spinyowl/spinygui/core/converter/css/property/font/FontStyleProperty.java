package com.spinyowl.spinygui.core.converter.css.property.font;

import static com.spinyowl.spinygui.core.converter.css.Properties.FONT_STYLE;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.NodeStyle;

public class FontStyleProperty extends Property<FontStyle> {

    public FontStyleProperty() {
        super(FONT_STYLE, "normal", INHERITED, !ANIMATABLE, NodeStyle::setFontStyle,
            NodeStyle::getFontStyle, FontStyle::find, FontStyle::contains);
    }

}
