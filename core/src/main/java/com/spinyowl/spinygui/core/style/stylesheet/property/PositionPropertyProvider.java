package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.checkValue;
import static com.spinyowl.spinygui.core.style.stylesheet.Property.put;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.stylesheet.PropertyProvider;
import com.spinyowl.spinygui.core.style.stylesheet.term.TermIdent;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.List;

public class PositionPropertyProvider implements PropertyProvider {

  @Override
  public List<Property> getProperties() {
    return List.of(
        Property.builder()
            .name(POSITION)
            .defaultValue(new TermIdent(Position.STATIC.name()))
            .inheritable(true)
            .animatable(true)
            .updater(put(POSITION, TermIdent.class, Position::find))
            .validator(checkValue(TermIdent.class, Position::contains))
            .build());
  }
}
