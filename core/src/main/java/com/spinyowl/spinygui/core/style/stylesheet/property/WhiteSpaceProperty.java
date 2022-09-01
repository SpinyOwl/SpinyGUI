package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.WHITE_SPACE;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.WhiteSpace;

public class WhiteSpaceProperty extends Property {

  public WhiteSpaceProperty() {
    super(
        WHITE_SPACE,
        new TermIdent(WhiteSpace.NORMAL.name()),
        INHERITABLE,
        !ANIMATABLE,
        put(WHITE_SPACE, TermIdent.class, WhiteSpace::find),
        check(TermIdent.class, WhiteSpace::contains));
  }
}
