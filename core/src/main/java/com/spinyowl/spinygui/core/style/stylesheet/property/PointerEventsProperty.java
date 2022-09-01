package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.PointerEvents;

public class PointerEventsProperty extends Property {

  public PointerEventsProperty() {
    super(
        POINTER_EVENTS,
        new TermIdent(PointerEvents.AUTO.name()),
        INHERITABLE,
        !ANIMATABLE,
        put(POINTER_EVENTS, TermIdent.class, PointerEvents::find),
        check(TermIdent.class, PointerEvents::contains));
  }
}
