package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Position;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class PositionProperty extends Property<Position> {

  public PositionProperty() {
    super(
        POSITION,
        Position.RELATIVE.name(),
        INHERITED,
        ANIMATABLE,
        NodeStyle::position,
        NodeStyle::position,
        Position::find,
        Position::contains);
  }
}
