package com.spinyowl.spinygui.core.style.stylesheet.property.font;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FONT_WEIGHT;

import com.spinyowl.spinygui.core.font.FontWeight;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;

public class FontWeightProperty extends Property {

  public FontWeightProperty() {
    super(
        FONT_WEIGHT,
        new TermIdent(FontWeight.NORMAL.name()),
        INHERITABLE,
        ANIMATABLE,
        put(FONT_WEIGHT, TermIdent.class, FontWeight::find),
        check(TermIdent.class, FontWeight::contains));
  }
}
