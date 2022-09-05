package com.spinyowl.spinygui.core.style.stylesheet.property.dimension;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WIDTH;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermLength;
import com.spinyowl.spinygui.core.style.types.length.Unit;

public class WidthProperty extends Property {
  public WidthProperty() {
    super(
        WIDTH,
        new TermIdent("auto"),
        !INHERITABLE,
        ANIMATABLE,
        put(WIDTH, TermIdent.class, "auto"::equalsIgnoreCase, v -> Unit.AUTO)
            .or(put(WIDTH, TermLength.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermLength.class::isInstance));
  }
}
