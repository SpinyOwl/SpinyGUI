package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.Position;
import java.util.Map;

public class PositionProperty extends Property {

  public PositionProperty() {
    super(
        POSITION,
        Position.RELATIVE.name(),
        INHERITED,
        ANIMATABLE,
        position -> Map.of(POSITION, Position.find(position)),
        Position::contains);
  }
}
