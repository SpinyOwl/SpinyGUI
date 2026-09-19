package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.CURSOR;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.CursorType;
import java.util.List;

/** Registers the bounded CSS {@code cursor} property. */
public class CursorPropertyProvider implements PropertyProvider {
  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(CURSOR)
            .defaultValue(new TermIdent(CursorType.AUTO.cssName()))
            .inheritable(true)
            .updater(put(CURSOR, TermIdent.class, CursorType::find))
            .validator(checkValue(TermIdent.class, CursorType::contains))
            .build());
  }
}
