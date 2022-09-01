package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.LEFT;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class LeftProperty extends Property {

  public LeftProperty() {
    super(
        LEFT,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(LEFT, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .andThen(put(LEFT, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
