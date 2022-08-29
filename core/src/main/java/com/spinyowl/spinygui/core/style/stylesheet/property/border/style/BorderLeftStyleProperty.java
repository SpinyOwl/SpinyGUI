package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_STYLE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderLeftStyleProperty extends Property {

  public BorderLeftStyleProperty() {
    super(
        BORDER_LEFT_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_LEFT_STYLE, TermIdent.class, BorderStyle::find),
        check(TermIdent.class, BorderStyle::contains));
  }
}
