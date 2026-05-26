package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POINTER_EVENTS;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.PointerEvents;
import java.util.List;

public class PointerEventsPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(POINTER_EVENTS)
            .defaultValue(new TermIdent(PointerEvents.AUTO.name()))
            .inheritable(true)
            .updater(put(POINTER_EVENTS, TermIdent.class, PointerEvents::find))
            .validator(checkValue(TermIdent.class, PointerEvents::contains))
            .build());
  }
}
