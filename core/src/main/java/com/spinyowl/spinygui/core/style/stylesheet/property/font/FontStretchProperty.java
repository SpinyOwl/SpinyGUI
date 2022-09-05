package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_STRETCH;

import com.spinyowl.spinygui.core.font.FontStretch;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;

public class FontStretchProperty extends Property {
  public FontStretchProperty() {
    super(
        FONT_STRETCH,
        new TermIdent(FontStretch.NORMAL.name()),
        INHERITABLE,
        ANIMATABLE,
        put(FONT_STRETCH, TermIdent.class, FontStretch::find),
        check(TermIdent.class, FontStretch::contains));
  }
}
