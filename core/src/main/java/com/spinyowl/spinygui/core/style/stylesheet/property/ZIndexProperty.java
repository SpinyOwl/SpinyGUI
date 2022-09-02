package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.Z_INDEX;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.Term;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermInteger;

public class ZIndexProperty extends Property {

  private static final Term<?> AUTO = new TermIdent("auto");

  public ZIndexProperty() {
    super(
        Z_INDEX,
        AUTO,
        !INHERITABLE,
        !ANIMATABLE,
        put(Z_INDEX, TermIdent.class, v -> 0).or(put(Z_INDEX, TermInteger.class)),
        check(TermIdent.class, "auto"::equalsIgnoreCase).or(TermInteger.class::isInstance));
  }
}
