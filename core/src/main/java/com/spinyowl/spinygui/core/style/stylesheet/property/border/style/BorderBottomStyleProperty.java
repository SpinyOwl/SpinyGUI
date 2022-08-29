package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_STYLE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderBottomStyleProperty extends Property {

  public BorderBottomStyleProperty() {
    super(
        BORDER_BOTTOM_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_BOTTOM_STYLE, TermIdent.class, BorderStyle::find),
        check(TermIdent.class, BorderStyle::contains));
  }
}
