package com.spinyowl.spinygui.core.converter.css.property.flex;

import static com.spinyowl.spinygui.core.converter.css.Properties.FLEX_DIRECTION;

import com.spinyowl.spinygui.core.converter.css.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;

public class FlexDirectionProperty extends Property<FlexDirection> {

  public FlexDirectionProperty() {
    super(FLEX_DIRECTION, "row", !INHERITED, !ANIMATABLE,
      (s, v) -> s.flex().flexDirection(v), s -> s.flex().flexDirection(),
      FlexDirection::find, FlexDirection::contains);
  }

}
