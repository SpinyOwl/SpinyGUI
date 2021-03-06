package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import com.spinyowl.spinygui.core.style.NodeStyle;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import com.spinyowl.spinygui.core.style.stylesheet.Property;

public class FlexDirectionProperty extends Property<FlexDirection> {

  public FlexDirectionProperty() {
    super(
        FLEX_DIRECTION,
        "row",
        !INHERITED,
        !ANIMATABLE,
        NodeStyle::flexDirection,
        NodeStyle::flexDirection,
        FlexDirection::find,
        FlexDirection::contains);
  }
}
