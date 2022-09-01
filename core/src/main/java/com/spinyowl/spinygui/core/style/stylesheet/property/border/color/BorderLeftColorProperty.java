package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_LEFT_COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderLeftColorProperty extends Property {

  public BorderLeftColorProperty() {
    super(
        BORDER_LEFT_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_LEFT_COLOR, TermIdent.class, Color::get)
            .andThen(put(BORDER_LEFT_COLOR, TermColor.class)),
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }
}
