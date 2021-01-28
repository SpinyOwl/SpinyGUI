package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.node.style.types.PointerEvents;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class PointerEventsProperty extends Property<PointerEvents> {

  public PointerEventsProperty() {
    super(POINTER_EVENTS, PointerEvents.AUTO.name(), INHERITED, !ANIMATABLE,
        NodeStyle::pointerEvents, NodeStyle::pointerEvents,
        PointerEvents::find, PointerEvents::contains);
  }
}
