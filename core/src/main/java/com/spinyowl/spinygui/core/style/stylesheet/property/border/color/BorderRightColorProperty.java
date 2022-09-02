package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_RIGHT_COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;

public class BorderRightColorProperty extends Property {

  public BorderRightColorProperty() {
    super(
        BORDER_RIGHT_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_RIGHT_COLOR, TermIdent.class, Color::get)
            .or(put(BORDER_RIGHT_COLOR, TermColor.class)),
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }
}
