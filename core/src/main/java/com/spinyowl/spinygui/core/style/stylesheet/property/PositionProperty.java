package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Position;

public class PositionProperty extends Property {

  public PositionProperty() {
    super(
        POSITION,
        new TermIdent(Position.STATIC.name()),
        INHERITABLE,
        ANIMATABLE,
        put(POSITION, TermIdent.class, Position::find),
        check(TermIdent.class, Position::contains));
  }
}
