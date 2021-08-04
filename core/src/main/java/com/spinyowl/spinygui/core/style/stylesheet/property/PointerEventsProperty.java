package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.PointerEvents;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;

public class PointerEventsProperty extends Property {

  public PointerEventsProperty() {
    super(
        POINTER_EVENTS,
        PointerEvents.AUTO.name(),
        INHERITED,
        !ANIMATABLE,
        (pointerEvents, styles) -> styles.put(POINTER_EVENTS, PointerEvents.find(pointerEvents)),
        PointerEvents::contains);
  }
}
