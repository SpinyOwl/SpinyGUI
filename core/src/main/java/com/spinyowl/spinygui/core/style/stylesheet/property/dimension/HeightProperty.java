package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.HEIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class HeightProperty extends Property {

  public HeightProperty() {
    super(
        HEIGHT,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(HEIGHT, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .or(put(HEIGHT, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
