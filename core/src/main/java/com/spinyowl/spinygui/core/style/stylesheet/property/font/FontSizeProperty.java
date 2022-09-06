package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_SIZE;

import com.spinyowl.spinygui.core.font.FontSize;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Length;

public class FontSizeProperty extends Property {
  public FontSizeProperty() {
    super(
        FONT_SIZE,
        new TermIdent(FontSize.MEDIUM.name()),
        INHERITABLE,
        ANIMATABLE,
        put(
                FONT_SIZE,
                TermIdent.class,
                FontSize::contains,
                name1 -> Length.pixel(FontSize.find(name1).size()))
            .or(put(FONT_SIZE, TermLength.class)),
        check(TermIdent.class, FontSize::contains).or(TermLength.class::isInstance));
  }
}
