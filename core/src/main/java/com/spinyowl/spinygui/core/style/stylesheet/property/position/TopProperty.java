package com.spinyowl.spinygui.core.style.stylesheet.property.position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.TOP;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class TopProperty extends Property {
  public TopProperty() {
    super(
        TOP,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(TOP, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .or(put(TOP, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
