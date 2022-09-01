package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.BOTTOM;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class BottomProperty extends Property {
  public BottomProperty() {
    super(
        BOTTOM,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(BOTTOM, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .andThen(put(BOTTOM, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
