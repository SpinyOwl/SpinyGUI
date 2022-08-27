package com.spinyowl.spinygui.core.style.stylesheet.property.border.color;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BORDER_BOTTOM_COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.function.Function;

public class BorderBottomColorProperty extends Property {

  public BorderBottomColorProperty() {
    super(
        BORDER_BOTTOM_COLOR,
        BorderColorProperty.DEFAULT_VALUE,
        !INHERITABLE,
        ANIMATABLE,
        put(BORDER_BOTTOM_COLOR, TermIdent.class, Color::get)
            .andThen(put(BORDER_BOTTOM_COLOR, TermColor.class, Function.identity())),
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }
}
