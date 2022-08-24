package com.spinyowl.spinygui.core.style.stylesheet.property.flex;

import com.spinyowl.spinygui.core.style.stylesheet.Property;
import com.spinyowl.spinygui.core.style.types.flex.FlexDirection;

import static com.spinyowl.spinygui.core.style.stylesheet.Properties.FLEX_DIRECTION;

public class FlexDirectionProperty extends Property {

  public FlexDirectionProperty() {
    super(
        FLEX_DIRECTION,
        "row",
        !INHERITABLE,
        !ANIMATABLE,
        (flexDirection, styles) -> styles.put(FLEX_DIRECTION, FlexDirection.find(flexDirection)),
        FlexDirection::contains);
  }
}
