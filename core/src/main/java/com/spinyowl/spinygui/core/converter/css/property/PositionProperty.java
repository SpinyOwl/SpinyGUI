package com.spinyowl.spinygui.core.converter.css.property;

import static com.spinyowl.spinygui.core.converter.css.Properties.POSITION;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.Position;

public class PositionProperty extends Property<Position> {

  public PositionProperty() {
    super(POSITION, Position.RELATIVE.name(), INHERITED, ANIMATABLE,
      NodeStyle::position, NodeStyle::position, Position::find, Position::contains);
  }

}
