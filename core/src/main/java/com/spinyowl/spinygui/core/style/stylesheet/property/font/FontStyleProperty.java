package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STYLE;

import com.spinyowl.spinygui.core.font.FontStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;

public class FontStyleProperty extends Property {

  public FontStyleProperty() {
    super(
        FONT_STYLE,
        new TermIdent(FontStyle.NORMAL.name()),
        INHERITABLE,
        !ANIMATABLE,
        put(FONT_STYLE, TermIdent.class, FontStyle::find),
        check(TermIdent.class, FontStyle::contains));
  }
}
