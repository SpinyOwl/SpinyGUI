package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;
import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;
import java.util.Map;

public class FlexDirectionProperty extends Property {

  public FlexDirectionProperty() {
    super(
        FLEX_DIRECTION,
        "row",
        !INHERITED,
        !ANIMATABLE,
        flexDirection -> Map.of(FLEX_DIRECTION, FlexDirection.find(flexDirection)),
        FlexDirection::contains);
  }
}
