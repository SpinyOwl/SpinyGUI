package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.node.style.types.flex.FlexDirection;

public class FlexDirectionProperty extends Property<FlexDirection> {

  public FlexDirectionProperty() {
    super(FLEX_DIRECTION, "row", !INHERITED, !ANIMATABLE,
        (s, v) -> s.flex().flexDirection(v), s -> s.flex().flexDirection(),
        FlexDirection::find, FlexDirection::contains);
  }

}
