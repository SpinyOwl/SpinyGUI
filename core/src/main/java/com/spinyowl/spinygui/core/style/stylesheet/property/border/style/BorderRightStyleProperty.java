package com.spinyowl.spinygui.core.style.stylesheet.property.border.style;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_STYLE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.border.BorderStyle;

public class BorderRightStyleProperty extends Property {

  public BorderRightStyleProperty() {
    super(
        BORDER_RIGHT_STYLE,
        BorderStyleProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_RIGHT_STYLE, TermIdent.class, BorderStyle::find),
        check(TermIdent.class, BorderStyle::contains));
  }
}
