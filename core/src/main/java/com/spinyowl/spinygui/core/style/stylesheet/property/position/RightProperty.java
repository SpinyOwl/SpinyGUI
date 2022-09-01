package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.RIGHT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class RightProperty extends Property {

  public RightProperty() {
    super(
        RIGHT,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(RIGHT, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .andThen(put(RIGHT, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
