package com.spinyowl.spinygui.core.style.stylesheet.property;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.Position;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;

public class PositionProperty extends Property {

  public PositionProperty() {
    super(
        POSITION,
        Position.STATIC.name(),
        INHERITED,
        ANIMATABLE,
        (position, styles) -> styles.put(POSITION, Position.find(position)),
        Position::contains);
  }
}
