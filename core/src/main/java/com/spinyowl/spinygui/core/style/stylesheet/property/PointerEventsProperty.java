package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.PointerEvents;
import java.util.Map;

public class PointerEventsProperty extends Property {

  public PointerEventsProperty() {
    super(
        POINTER_EVENTS,
        PointerEvents.AUTO.name(),
        INHERITED,
        !ANIMATABLE,
        pointerEvents -> Map.of(POINTER_EVENTS, PointerEvents.find(pointerEvents)),
        PointerEvents::contains);
  }
}
