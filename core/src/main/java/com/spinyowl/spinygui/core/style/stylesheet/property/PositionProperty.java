package com.spinyowl.spinygui.core.style.stylesheet.property;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.POSITION;
import com.spinyowl.spinygui.core.node.style.NodeStyle;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.Position;

public class PositionProperty extends Property<Position> {

  public PositionProperty() {
    super(POSITION, Position.RELATIVE.name(), INHERITED, ANIMATABLE,
        NodeStyle::position, NodeStyle::position, Position::find, Position::contains);
  }

}
