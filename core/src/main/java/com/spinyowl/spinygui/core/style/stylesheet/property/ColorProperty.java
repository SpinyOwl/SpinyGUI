package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.COLOR;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermColor;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Color;
import java.util.function.Function;

public class ColorProperty extends Property {

  public ColorProperty() {
    super(
        COLOR,
        new TermColor(Color.BLACK),
        INHERITABLE,
        ANIMATABLE,
        put(COLOR, TermIdent.class, Color::get)
            .andThen(put(COLOR, TermColor.class, Function.identity())),
        check(TermIdent.class, Color::exists).or(TermColor.class::isInstance));
  }

}
